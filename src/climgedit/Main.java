package climgedit;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.*;

import climgedit.gui.GUI;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

    public static void main(String[] args) {

        Image img = null;

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

        options.addOption(Option.builder("s").longOpt("replace-colors").argName("color1 color2").desc("swap the first colour with the second")
                .numberOfArgs(2).optionalArg(false).build());

        options.addOption(Option.builder("x").longOpt("rotate").argName("degrees [mode]").desc("rotates the image counterclockwise")
                .numberOfArgs(2).optionalArg(true).build());

        CommandLineParser clParser = new DefaultParser();
        CommandLine cl = null;
        try {
            cl = clParser.parse(options, args);
            if (cl.hasOption('h')) {
                HelpFormatter hl = new HelpFormatter();
                hl.printHelp("imgEdit -i <INPUT FILE> [operations] -o <OUTPUT FILE>", options);
                return;
            } else if (!cl.hasOption('i')) {
                startGUI(null);
            } else {
                String inPath = cl.getOptionValue('i');
                img = new Image(inPath);

                Iterator<Option> it = cl.iterator();
                while (it.hasNext()) {
                    Option opt = it.next();
                    char o = opt.getOpt().charAt(0);
                    if (o == 'i' || o == 'o' || o == 'h')
                        continue;
                    String[] values = opt.getValues();
                    switch (opt.getOpt().charAt(0)) {
                        case 'r':
                            CLIOptionsParser.parseResize(img, values);
                            break;
                        case 'x':
                            CLIOptionsParser.parseRotate(img, values);
                            break;
                        case 'c':
                            CLIOptionsParser.parseCrop(img, values);
                            break;
                        case 'p':
                            CLIOptionsParser.parsePad(img, values);
                            break;
                        case 's':
                            CLIOptionsParser.parseReplaceColors(img, values);
                            break;
                    }
                }

                if (cl.hasOption('o')) {
                    String outPath = cl.getOptionValue('o');
                    img.saveToFile(new File(outPath));
                } else {
                    startGUI(img);
                }
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    private static void startGUI(Image img) {
        JFrame frame = new JFrame("GUI");
        GUI gui = new GUI();
        frame.setContentPane(new GUI().root);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        if (img != null)
            gui.displayImage(img);
    }
}
