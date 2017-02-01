package main;

import org.imgscalr.Scalr;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Marius on 01/02/2017.
 */
public class Image {

    public BufferedImage getImage() {
        return image;
    }

    private BufferedImage image;

    public Image(BufferedImage img){
        image = img;
    }

    public void rotate(Scalr.Rotation rot){
        image = Scalr.rotate(image, rot);
    }

    public void resize(Scalr.Mode mode, int width, int height){
        image = Scalr.resize(image, mode, width, height);
    }

    public void pad(int thickness, Color c){
        image = Scalr.pad(image, thickness, c);
    }

    public void crop(int startX, int startY, int width, int height){
        image = Scalr.crop(image, startX, startY, width, height);
    }

    public void swapColours(ColourMatcher c, Color c2){

        Color c1;
        for (int x = 0; x < image.getWidth(); x++)
            for (int y = 0; y < image.getHeight(); y++) {
                c1 = new Color(image.getRGB(x, y));
                if(c.matches(c1))
                    image.setRGB(x, y, c2.getRGB());
            }
    }


}
