package main;

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

		// options.addOption(Option.builder("x").longOpt("rotate").desc("rotates
		// the image trigonometrically")
		// .numberOfArgs(1).optionalArg(false).build());

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

		BufferedImage img = readImage(cl.getOptionValue('i'));
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
					img = Scalr.resize(img, Scalr.Mode.AUTOMATIC, width, height);
				else if (mode.equals("exact"))
					img = Scalr.resize(img, Scalr.Mode.FIT_EXACT, width, height);
				else if (mode.equals("width"))
					img = Scalr.resize(img, Scalr.Mode.FIT_TO_WIDTH, width, height);
				else if (mode.equals("height"))
					img = Scalr.resize(img, Scalr.Mode.FIT_TO_HEIGHT, width, height);
				else {
					img = Scalr.resize(img, Scalr.Mode.AUTOMATIC, width, height);
					System.out.println("Warning. Resize mode set to 'auto'");
				}
			} else if (o == 'p') {
				int pad = Integer.parseInt(opt.getValue(0));
				img = Scalr.pad(img, pad);
			} else if (o == 'c') {
				int x = Integer.parseInt(values[0]);
				int y = Integer.parseInt(values[1]);
				int dx = Integer.parseInt(values[2].split("x")[0]);
				int dy = Integer.parseInt(values[2].split("x")[1]);
				img = Scalr.crop(img, x, y, dx, dy);
			} else if (o == 's') {
				int colour1 = parseInt(values[0]);
				int colour2 = parseInt(values[1]);
				for (int x = 0; x < img.getWidth(); x++)
					for (int y = 0; y < img.getHeight(); y++) {
						if (img.getRGB(x, y) == colour1)
							img.setRGB(x, y, colour2);
					}
			}

		}

		String[] aux = dest.getPath().split("\\.");
		String format = aux[aux.length - 1];
		try {
			ImageIO.write(img, format, dest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static int parseInt(String s) {
		s = s.toLowerCase();
		if (s.startsWith("0x"))
			return (int) Long.parseLong(s.substring(2), 16);
		else if (s.startsWith("0b"))
			return (int) Long.parseLong(s.substring(2), 2);
		else if (s.startsWith("0"))
			return (int) Long.parseLong(s, 8);
		else
			return (int) Long.parseLong(s);

	}

	public static BufferedImage readImage(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
			BufferedImage convertedImg = new BufferedImage(img.getWidth(), img.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			convertedImg.getGraphics().drawImage(img, 0, 0, null);
			img = convertedImg;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return img;
	}

}