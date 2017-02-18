package climgedit;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Created by Marius on 01/02/2017.
 */
public class Image {

    public static final double DISTANCE_NORMAL = 255 / Math.sqrt(195075);

    public BufferedImage getImage() {
        return image;
    }

    private BufferedImage image;

    public Image(String path) throws IOException {
        image = ImageIO.read(new File(path));
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage aux = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = aux.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image = aux;
        }
    }

    public boolean saveToFile(File f){
        Matcher m = Pattern.compile("\\.[a-zA-Z0-9]$").matcher(f.getAbsolutePath());
        String format = "png";
        if(m.matches()) {
            format = m.group();
        }
        else{
            File nf = new File(f.getAbsolutePath()+".png");
            f = nf;
        }
        try{
            ImageIO.write(image, format, f);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Image(BufferedImage img) {
        image = img;
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage aux = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = aux.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image = aux;
        }
    }

    public void rotate(double theta, RotateMode mode) {

        double radians = Math.toRadians(-theta);

        int origWidth = image.getWidth();
        int origHeight = image.getHeight();
        double newWidth = origWidth;
        double newHeight = origHeight;

        if (mode.equals(RotateMode.PAD) || mode.equals(RotateMode.PAD_KEEP_SIZE)) {
            double alpha = Math.atan(origHeight / origWidth);
            newWidth = origHeight * (Math.abs(Math.sin(theta))) + origWidth * Math.abs((Math.cos(theta)));
            newHeight = origHeight * (Math.abs(Math.cos(theta))) + origWidth * Math.abs((Math.sin(theta)));
            pad((int) ((newWidth - origWidth) / 2), (int) ((newHeight - origHeight) / 2));
        }

        double anchorX = newWidth / 2;
        double anchorY = newHeight / 2;
        AffineTransform afTransform = AffineTransform.getRotateInstance(radians, anchorX, anchorY);
        AffineTransformOp afTransfOp = new AffineTransformOp(afTransform, AffineTransformOp.TYPE_BICUBIC);
        BufferedImage dest = new BufferedImage((int) newWidth, (int) newHeight, image.getType());
        image = afTransfOp.filter(image, dest);

        if (mode.equals(RotateMode.PAD_KEEP_SIZE)) {
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
        BufferedImage dest = new BufferedImage(dstWidth, dstHeight, image.getType());
        image = op.filter(image, dest);

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
        pad(left, right, top, bottom, new Color(1, true));
    }

    public void pad(int leftRight, int topBottom, Color c) {
        pad(leftRight, leftRight, topBottom, topBottom, c);
    }

    public void pad(int leftRight, int topBottom) {
        pad(leftRight, topBottom, new Color(1, true));
    }

    public void pad(int thickness, Color c) {
        pad(thickness, thickness, thickness, thickness, c);
    }

    public void pad(int thickness) {
        pad(thickness, new Color(1, true));
    }

    public void crop(int startX, int startY, int width, int height) {
        BufferedImage dest = new BufferedImage(width, height, image.getType());
        Graphics g = dest.getGraphics();
        g.drawImage(image, 0, 0, width, height,
                startX, startY, startX + width, startY + height,
                new Color(1, true), null);
        g.dispose();
        image = dest;

    }

    public void crop(int width, int height) {
        int startX = (image.getWidth() - width) / 2;
        int startY = (image.getHeight() - height) / 2;
        crop(startX, startY, width, height);
    }

    public void replaceColors(Color src, Color dst, int range) {
        replaceColors(src, dst, range, range);
    }

    public void replaceColors(Color src, Color dst, int colorRange, int alphaRange) {
        Raster srcData = image.getData();
        WritableRaster destData = srcData.createCompatibleWritableRaster();

        int[] rgbaSrc = new int[]{src.getRed(), src.getGreen(), src.getBlue(), src.getAlpha()};
        int[] rgbaDest = new int[]{dst.getRed(), dst.getGreen(), dst.getBlue(), dst.getAlpha()};

        int threadCount = Runtime.getRuntime().availableProcessors();
        ArrayList<Thread> threads = new ArrayList<>(threadCount - 1);

        final int rangePerThread = image.getHeight() / (threadCount - 1);
        final int mainThreadRange = image.getHeight() % (threadCount - 1);

        for (int t = 0; t < threadCount - 1; t++) {
            final int threadNumber = t;
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int y = (threadNumber * rangePerThread + mainThreadRange); y < ((threadNumber + 1) * rangePerThread + mainThreadRange); y++)
                        for (int x = 0; x < image.getWidth(); x++) {
                            int[] channels = new int[4];
                            channels = srcData.getPixel(x, y, channels);
                            double dr = rgbaSrc[0] - channels[0];
                            double dg = rgbaSrc[1] - channels[1];
                            double db = rgbaSrc[2] - channels[2];

                            double distance = Math.sqrt(dr * dr + dg * dg + db * db) * DISTANCE_NORMAL;
                            int alphaDistance = Math.abs(rgbaSrc[3] - channels[3]);

                            if (distance <= colorRange && alphaDistance <= alphaRange)
                                destData.setPixel(x, y, rgbaDest);
                            else
                                destData.setPixel(x, y, channels);
                        }
                }
            });
            threads.add(th);
            th.start();
        }

        for (int y = 0; y < mainThreadRange; y++)
            for (int x = 0; x < image.getWidth(); x++) {
                int[] channels = new int[4];
                channels = srcData.getPixel(x, y, channels);
                double dr = rgbaSrc[0] - channels[0];
                double dg = rgbaSrc[1] - channels[1];
                double db = rgbaSrc[2] - channels[2];

                double distance = Math.sqrt(dr * dr + dg * dg + db * db) * DISTANCE_NORMAL;
                int alphaDistance = Math.abs(rgbaSrc[3] - channels[3]);

                if (distance <= colorRange && alphaDistance <= alphaRange)
                    destData.setPixel(x, y, rgbaDest);
                else
                    destData.setPixel(x, y, channels);
            }

        while (true) {
            try {
                for (Thread t : threads)
                    t.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
