package main;

import java.awt.*;

/**
 * Created by Marius on 12/02/2017.
 */
public class CLIOptionsParser {

    public static boolean parseRotate(Image img, String[] args){

        double theta = Double.parseDouble(args[0]);
        Image.RotateMode mode;
        if(args[1].equalsIgnoreCase("crop"))
            mode = Image.RotateMode.CROP;
        else if(args[1].equalsIgnoreCase("pad"))
            mode = Image.RotateMode.PAD;
        else if(args[1].equalsIgnoreCase("pad-keep-size"))
            mode = Image.RotateMode.PAD_KEEP_SIZE;
        else
            return false;

        img.rotate(theta, mode);
        return true;

    }

    public static boolean parseResize(Image img, String[] args){

        int width = Integer.parseInt(args[0]);
        int height = Integer.parseInt(args[1]);
        Image.ResizeMode mode;
        if(args.length==3){
            if(args[2].equalsIgnoreCase("exact"))
                mode = Image.ResizeMode.EXACT;
            else if(args[2].equalsIgnoreCase("crop"))
                mode = Image.ResizeMode.CROP;
            else if(args[2].equalsIgnoreCase("pad"))
                mode = Image.ResizeMode.PAD;
            else
                return false;
        }
        else
            mode = Image.ResizeMode.EXACT;

        img.resize(width, height, mode);
        return true;

    }

    public static boolean parsePad(Image img, String[] args){

        switch(args.length){
            case 1:{
                int thickness = Integer.parseInt(args[0]);
                img.pad(thickness);
                return true;
            }
            case 2:{
                int d1 = Integer.parseInt(args[0]);
                if(args[1].startsWith("0x")|| args[1].startsWith("0X")) {
                    int argb = Integer.parseInt(args[1].substring(2), 16);
                    Color c = new Color(argb, true);
                    img.pad(d1, c);
                    return true;
                }
                else{
                    int d2 = Integer.parseInt(args[1]);
                    img.pad(d1, d2);
                    return true;
                }
            }
            case 3:{
                int d1 = Integer.parseInt(args[0]);
                int d2 = Integer.parseInt(args[1]);
                int argb = Integer.parseInt(args[2].substring(2), 16);
                Color c = new Color(argb, true);
                img.pad(d1, d2, c);
                return true;
            }
            case 4:{
                int left = Integer.parseInt(args[0]);
                int right = Integer.parseInt(args[1]);
                int top = Integer.parseInt(args[2]);
                int bottom = Integer.parseInt(args[3]);
                img.pad(left, right, top, bottom);
                return true;
            }
            case 5:{
                int left = Integer.parseInt(args[0]);
                int right = Integer.parseInt(args[1]);
                int top = Integer.parseInt(args[2]);
                int bottom = Integer.parseInt(args[3]);
                int argb = Integer.parseInt(args[4].substring(2), 16);
                Color c = new Color(argb, true);
                img.pad(left, right, top, bottom, c);
                return true;
            }
        }
        return false;
    }

    public static boolean parseCrop(Image img, String[] args){

        int sx = Integer.parseInt(args[0]);
        int sy = Integer.parseInt(args[1]);
        int w = Integer.parseInt(args[2]);
        int h = Integer.parseInt(args[3]);
        img.crop(sx, sy, w, h);
        return true;

    }


}
