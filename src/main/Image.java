package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created by Marius on 01/02/2017.
 */
public class Image {

    private BufferedImage image;

    public Image() {

    }

    public Image(String path) throws IOException {
        image = ImageIO.read(new File(path));
    }

    public Image(BufferedImage img) {
        image = img;
    }

    public void rotate(double theta, RotateMode mode) {
        double radians = Math.toRadians(theta);

        int origWidth = image.getWidth();
        int origHeight = image.getHeight();

        if (mode.equals(RotateMode.PAD) || mode.equals(RotateMode.PAD_RESIZE)) {
            int diagonal = (int) Math.round(Math.sqrt(image.getHeight() * image.getHeight() + image.getWidth() * image.getWidth()));
            double alpha = Math.asin(image.getWidth() / diagonal);
            int newWidth = (int) Math.abs(diagonal * Math.cos(alpha + theta));
            int newHeight = (int) Math.abs(diagonal * Math.sin(alpha + theta));
            pad((newWidth - image.getWidth()) / 2, (newHeight - image.getHeight()) / 2);
        }

        int anchorX = image.getWidth() / 2;
        int anchorY = image.getHeight() / 2;
        AffineTransform afTransform = AffineTransform.getRotateInstance(radians, anchorX, anchorY);
        AffineTransformOp afTransfOp = new AffineTransformOp(afTransform, AffineTransformOp.TYPE_BILINEAR);
        image = afTransfOp.filter(image, image);

        if(mode.equals(RotateMode.PAD)){
            resize(ResizeMode.FIT, origWidth, origHeight);
        }

    }

    public void resize(ResizeMode mode, int width, int height) {
        AffineTransform afTransform = AffineTransform.getScaleInstance(width/image.getWidth(), height/image.getHeight());
        AffineTransformOp op = new AffineTransformOp(afTransform, Affine)
    }

    public void pad(int left, int right, int top, int bottom, Color c) {
        int newWidth = left + right + image.getWidth();
        int newHeight = top + bottom + image.getHeight();
        BufferedImage newImage = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics g = newImage.getGraphics();
        g.setColor(c);
        g.drawImage(image, left, top, null);
        g.dispose();
        image = newImage;
    }

    public void pad(int left, int right, int top, int bottom) {
        pad(left, right, top, bottom, new Color(0));
    }

    public void pad(int leftRight, int topBottom, Color c) {
        pad(leftRight, leftRight, topBottom, topBottom, c);
    }

    public void pad(int leftRight, int topBottom) {
        pad(leftRight, topBottom, new Color(0));
    }

    public void pad(int thickness, Color c) {
        pad(thickness, thickness, thickness, thickness, c);
    }

    public void pad(int thickness) {
        pad(thickness, new Color(0));
    }

    public void crop(int startX, int startY, int width, int height) {
        BufferedImage dest = new BufferedImage(width, height, image.getType());
        Graphics g = dest.getGraphics();
        g.drawImage(image, 0, 0, width, height,
                startX, startY, startX + width, startY + height,
                new Color(0), null);
        g.dispose();
        image = dest;

    }

    public void replaceColors(Color src, Color dst, int threshold) {
        Raster srcData = image.getData();
        WritableRaster destData = srcData.createCompatibleWritableRaster();
        int[] hNumbers = IntStream.range(0, image.getHeight()).toArray();

        int[] rgbaSrc = new int[]{src.getRed(), src.getGreen(), src.getBlue(), src.getAlpha()};
        int[] rgbaDest = new int[]{dst.getRed(), dst.getGreen(), dst.getBlue(), dst.getAlpha()};

        Arrays.stream(hNumbers).parallel().forEach(y -> {
            for (int x = image.getWidth() - 1; x >= 0; x--) {

                int[] channels = new int[4];
                channels = srcData.getPixel(x, y, channels);
                for (int i = 0; i < 4; i++) {
                    if (channels[i] <= (rgbaSrc[i] + threshold) && channels[i] >= (rgbaSrc[i] - threshold))
                        destData.setPixel(x, y, rgbaDest);
                }
            }
        });

        image.setData(destData);
    }

    public void replaceColors(ColorReplacer replacer) {
        Raster srcData = image.getData();
        WritableRaster destData = srcData.createCompatibleWritableRaster();
        int[] hNumbers = IntStream.range(0, image.getHeight()).toArray();

        Arrays.stream(hNumbers).parallel().forEach(y -> {
            for (int x = image.getWidth() - 1; x >= 0; x--) {

                int[] channels = new int[4];
                channels = srcData.getPixel(x, y, channels);
                Color replacement = replacer.replace(new Color(channels[0], channels[1], channels[2], channels[3]));
                destData.setPixel(x, y, new int[]{replacement.getRed(), replacement.getGreen(), replacement.getBlue(), replacement.getAlpha()});
            }
        });

        image.setData(destData);
    }

    public enum RotateMode {

        CROP(0), PAD(1), PAD_RESIZE(2);

        private final int v;

        RotateMode(int value) {
            v = value;
        }

        public int getValue() {
            return v;
        }

    }

    public enum ResizeMode {

        FIT_PAD(0), FIT_CROP(1), FIT(2), PAD(3);

        private final int v;

        ResizeMode(int value) {
            v = value;
        }

        public int getValue() {
            return v;
        }

    }


    public interface ColorReplacer {

        Color replace(Color src);

    }


}
