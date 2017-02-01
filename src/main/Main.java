package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.imgscalr.Scalr;

public class Main {

	public static void main(String[] args) {

		Options options = new Options();

		options.addOption(Option.builder("i").longOpt("input").desc("input file").argName("file").numberOfArgs(1)
				.optionalArg(false).build());

		options.addOption(Option.builder("o").longOpt("output").desc("output file").argName("file").numberOfArgs(1)
				.optionalArg(false).build());

		options.addOption(
				Option.builder("h").longOpt("help").desc("display help").numberOfArgs(0).optionalArg(false).build());

		options.addOption(Option.builder("r").longOpt("resize").desc("resize the image to the specified dimensions")
				.argName("WxH [mode]").numberOfArgs(2).optionalArg(true).build());

		options.addOption(Option.builder("p").longOpt("pad").argName("thickness")
				.desc("pad the image with a border equal on all sides").numberOfArgs(1).optionalArg(true).build());

		options.addOption(Option.builder("c").longOpt("crop").desc("crop the image").argName("x y WxH").numberOfArgs(3)
				.optionalArg(false).build());

		options.addOption(Option.builder("s").longOpt("swap-colours").argName("colour1 colour2").desc("swap the first colour with the second")
				.numberOfArgs(2).optionalArg(false).build());

		CommandLineParser clParser = new DefaultParser();
		CommandLine cl = null;
		try {
			cl = clParser.parse(options, args);
			if (cl.hasOption('h') || !cl.hasOption('i') || !cl.hasOption('o')) {
				HelpFormatter hl = new HelpFormatter();
				hl.printHelp("imgEdit -i <INPUT FILE> [operations] -o <OUTPUT FILE>", options);
				return;
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		Image img = new Image(readImage(cl.getOptionValue('i')));
		File dest = new File(cl.getOptionValue('o'));
		Iterator<Option> it = cl.iterator();
		while (it.hasNext()) {
			Option opt = it.next();
			char o = opt.getOpt().charAt(0);
			if (o == 'i' || o == 'o' || o == 'h')
				continue;
			String[] values = opt.getValues();
			if (o == 'r') {
				int width = Integer.parseInt(values[0].split("x")[0]);
				int height = Integer.parseInt(values[0].split("x")[1]);
				String mode = "auto";
				if (values.length == 2)
					mode = values[1];
				if (mode.equals("auto"))
					img.resize(Scalr.Mode.AUTOMATIC, width, height);
				else if (mode.equals("exact"))
                    img.resize(Scalr.Mode.FIT_EXACT, width, height);
				else if (mode.equals("width"))
                    img.resize(Scalr.Mode.FIT_TO_WIDTH, width, height);
				else if (mode.equals("height"))
                    img.resize(Scalr.Mode.FIT_TO_HEIGHT, width, height);
				else {
                    img.resize(Scalr.Mode.AUTOMATIC, width, height);
					System.out.println("Resize mode set to 'auto'");
				}
			} else if (o == 'p') {
				int pad = Integer.parseInt(opt.getValue(0));
				img.pad(pad, new Color(0xFF000000));
			} else if (o == 'c') {
				int x = Integer.parseInt(values[0]);
				int y = Integer.parseInt(values[1]);
				int dx = Integer.parseInt(values[2].split("x")[0]);
				int dy = Integer.parseInt(values[2].split("x")[1]);
				img.crop(x, y, dx, dy);
			} else if (o == 's') {
				ColourMatcher cm = new ColourMatcher(values[0]);
				Color c2 = new Color(Integer.parseInt(values[1], 16));
				img.swapColours(cm, c2);
			}

		}

		String[] aux = dest.getPath().split("\\.");
		String format = aux[aux.length - 1];
		try {
			ImageIO.write(img.getImage(), format, dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static BufferedImage readImage(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return img;
	}

}
