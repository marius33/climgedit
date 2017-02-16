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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created by Marius on 01/02/2017.
 */
public class Image {

    public BufferedImage getImage() {
        return image;
    }

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

        if (mode.equals(RotateMode.PAD) || mode.equals(RotateMode.PAD_KEEP_SIZE)) {
            int diagonal = (int) Math.round(Math.sqrt(image.getHeight() * image.getHeight() + image.getWidth() * image.getWidth()));
            double alpha = Math.asin(image.getWidth() / diagonal);
            int newWidth = (int) Math.abs(diagonal * Math.cos(alpha + theta));
            int newHeight = (int) Math.abs(diagonal * Math.sin(alpha + theta));
            pad((newWidth - image.getWidth()) / 2, (newHeight - image.getHeight()) / 2);
        }

        double anchorX = image.getWidth() / (double) 2;
        double anchorY = image.getHeight() / (double) 2;
        AffineTransform afTransform = AffineTransform.getRotateInstance(radians, anchorX, anchorY);
        AffineTransformOp afTransfOp = new AffineTransformOp(afTransform, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage dest = new BufferedImage(origWidth, origHeight, image.getType());
        image = afTransfOp.filter(image, dest);

        if (mode.equals(RotateMode.PAD)) {
            resize(origWidth, origHeight, ResizeMode.EXACT);
        }

    }

    public void resize(int width, int height, ResizeMode mode) {

        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();
        int dstWidth;
        int dstHeight;
        double wRatio;
        double hRatio;
        if (mode.equals(ResizeMode.EXACT)) {
            dstWidth = width;
            dstHeight = height;
            wRatio = (double) dstWidth / srcWidth;
            hRatio = (double) dstHeight / srcHeight;
        } else if (mode.equals(ResizeMode.CROP)) {
            wRatio = (double) width / srcWidth;
            hRatio = (double) height / srcHeight;
            double ratio = Math.max(wRatio, hRatio);
            dstWidth = (int) (srcWidth * ratio);
            dstHeight = (int) (srcHeight * ratio);
            wRatio = ratio;
            hRatio = ratio;
        } else if (mode.equals(ResizeMode.PAD)) {
            wRatio = (double) width / srcWidth;
            hRatio = (double) height / srcHeight;
            double ratio = Math.min(wRatio, hRatio);
            dstWidth = (int) (srcWidth * ratio);
            dstHeight = (int) (srcHeight * ratio);
            wRatio = ratio;
            hRatio = ratio;
        } else {
            dstWidth = srcWidth;
            dstHeight = srcHeight;
            wRatio = 1;
            hRatio = 1;
        }

        AffineTransform transform = AffineTransform.getScaleInstance(wRatio, hRatio);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
        // dest = new BufferedImage(dstWidth, dstHeight, image.getType());
        image = op.filter(image, null);
        //image = dest;

        int dx = dstWidth - width;
        int dy = dstHeight - height;

        if (mode.equals(ResizeMode.CROP))
            crop(dx / 2, dy / 2, width, height);
        else
            pad(dx / 2, dy / 2);

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
        pad(left, right, top, bottom, new Color(1));
    }

    public void pad(int leftRight, int topBottom, Color c) {
        pad(leftRight, leftRight, topBottom, topBottom, c);
    }

    public void pad(int leftRight, int topBottom) {
        pad(leftRight, topBottom, new Color(1));
    }

    public void pad(int thickness, Color c) {
        pad(thickness, thickness, thickness, thickness, c);
    }

    public void pad(int thickness) {
        pad(thickness, new Color(1));
    }

    public void crop(int startX, int startY, int width, int height) {
        BufferedImage dest = new BufferedImage(width, height, image.getType());
        Graphics g = dest.getGraphics();
        g.drawImage(image, 0, 0, width, height,
                startX, startY, startX + width, startY + height,
                new Color(1), null);
        g.dispose();
        image = dest;

    }

    public void crop(int width, int height) {
        int startX = (image.getWidth() - width) / 2;
        int startY = (image.getHeight() - height) / 2;
        crop(startX, startY, width, height);
    }

    public void replaceColors(Color src, Color dst, int threshold) {
        Raster srcData = image.getData();
        WritableRaster destData = srcData.createCompatibleWritableRaster();

        int[] rgbaSrc = new int[]{src.getRed(), src.getGreen(), src.getBlue(), src.getAlpha()};
        int[] rgbaDest = new int[]{dst.getRed(), dst.getGreen(), dst.getBlue(), dst.getAlpha()};

        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++) {

                int[] channels = new int[4];
                channels = srcData.getPixel(x, y, channels);
                double dr = rgbaSrc[0] - channels[0];
                double dg = rgbaSrc[1] - channels[1];
                double db = rgbaSrc[2] - channels[2];

                double distance = Math.sqrt(dr * dr + dg * dg + db * db);

                if (distance*(Math.abs(rgbaSrc[3]-channels[3])) <= threshold) {
                    destData.setPixel(x, y, rgbaDest);
                } else
                    destData.setPixel(x, y, channels);


            }

//        int threadCount = Runtime.getRuntime().availableProcessors();
//        ArrayList<Thread> threads = new ArrayList<Thread>(threadCount - 1);
//
//        int rangePerThread = image.getHeight() / threadCount;
//        int mainThreadRange = rangePerThread + (image.getHeight() % threadCount);
//
//        for (int t = 1; t < threadCount; t++) {
//            final int threadNumber = t;
//            threads.add(new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (int y = (threadNumber * rangePerThread); y < ((threadNumber + 1) * rangePerThread); y++)
//                        for (int x = 0; x < image.getWidth(); x++) {
//                            int[] channels = new int[4];
//                            channels = srcData.getPixel(x, y, channels);
//                            for (int i = 0; i < 4; i++) {
//                                if (channels[i] <= (rgbaSrc[i] + threshold) && channels[i] >= (rgbaSrc[i] - threshold))
//                                    destData.setPixel(x, y, rgbaDest);
//                            }
//                        }
//                }
//            }));
//        }

        image.setData(destData);
    }

    public void replaceColors(ColorReplacer replacer) {
        Raster srcData = image.getData();
        WritableRaster destData = srcData.createCompatibleWritableRaster();
        int[] hNumbers = IntStream.range(0, image.getHeight()).toArray();

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int[] channels = new int[4];
                channels = srcData.getPixel(x, y, channels);
                Color replacement = replacer.replace(new Color(channels[0], channels[1], channels[2], channels[3]));
                destData.setPixel(x, y, new int[]{replacement.getRed(), replacement.getGreen(), replacement.getBlue(), replacement.getAlpha()});
            }
        }

        image.setData(destData);
    }

    public enum RotateMode {

        CROP(0), PAD(1), PAD_KEEP_SIZE(2);

        private final int v;

        RotateMode(int value) {
            v = value;
        }

        public int getValue() {
            return v;
        }

    }

    public enum ResizeMode {

        PAD(0), CROP(1), EXACT(2);

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
