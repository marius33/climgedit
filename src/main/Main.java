package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import main.gui.GUI;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public static void main(String[] args) {

		if(args.length==0)
			startGUI();

		System.out.println("GUI opened");

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
			if (cl.hasOption('h')) {
				HelpFormatter hl = new HelpFormatter();
				hl.printHelp("imgEdit -i <INPUT FILE> [operations] -o <OUTPUT FILE>", options);
				return;
			}
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

//		Image img = new Image(readImage(cl.getOptionValue('i')));
//		File dest = new File(cl.getOptionValue('o'));
//		Iterator<Option> it = cl.iterator();
//		while (it.hasNext()) {
//			Option opt = it.next();
//			char o = opt.getOpt().charAt(0);
//			if (o == 'i' || o == 'o' || o == 'h')
//				continue;
//
//			String[] values = opt.getValues();
//
//		}
//
//		String[] aux = dest.getPath().split("\\.");
//		String format = aux[aux.length - 1];
//		try {
//			ImageIO.write(img, format, dest);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	private static void startGUI(){
		JFrame frame = new JFrame("GUI");
		frame.setContentPane(new GUI().root);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
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
