package climgedit.gui;

import climgedit.CLIOptionsParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Marius on 08/02/2017.
 */
public class GUI {
    private JButton saveButton;
    private JButton executeButton;
    public JPanel root;
    private JList opsList;
    private JLabel preview;
    private JTextField execArgs;
    private JButton openButton;
    private JPanel previewPanel;
    private BufferedImage previewImage;
    private climgedit.Image activeImage;
    private Dimension preferredDimension;

    public GUI() {

        createUIComponents();

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] args = execArgs.getText().split("\\s+");
                System.out.println(Arrays.toString(args));
                int op = opsList.getSelectedIndex();
                switch (op) {
                    case 0:
                        CLIOptionsParser.parseRotate(activeImage, args);
                        break;
                    case 1:
                        CLIOptionsParser.parseResize(activeImage, args);
                        break;
                    case 2:
                        CLIOptionsParser.parsePad(activeImage, args);
                        break;
                    case 3:
                        CLIOptionsParser.parseCrop(activeImage, args);
                        break;
                    case 4:
                        CLIOptionsParser.parseReplaceColors(activeImage, args);
                        break;
                }

                displayImage(activeImage.getImage());
            }
        });

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (preferredDimension == null)
                    preferredDimension = previewPanel.getSize();
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(root);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        BufferedImage img = ImageIO.read(file);
                        activeImage = new climgedit.Image(img);
                        displayImage(img);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final JFileChooser fc = new JFileChooser();

                int returnVal = fc.showSaveDialog(root);
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    File f = fc.getSelectedFile();
                    activeImage.saveToFile(f);
                }
            }
        });

        execArgs.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '\n')
                    executeButton.doClick();
                else
                    super.keyPressed(e);
            }
        });


    }

    public void displayImage(climgedit.Image img){
        if (preferredDimension == null)
            preferredDimension = previewPanel.getSize();
        activeImage = img;
        displayImage(img.getImage());
    }

    void displayImage(java.awt.Image img) {

        Dimension imgDim = getBestDimension(preferredDimension.width, preferredDimension.height, img.getWidth(null), img.getHeight(null));
        BufferedImage background = generateCheckeredImage(imgDim.width, imgDim.height, 5);
        Image resize = img.getScaledInstance(imgDim.width, imgDim.height, Image.SCALE_SMOOTH);
        Graphics g = background.getGraphics();
        g.drawImage(resize, 0, 0, null);
        g.dispose();
        preview.setIcon(new ImageIcon(background));
        //System.out.println(preview.getIcon().getIconWidth());
    }

    private BufferedImage generateCheckeredImage(int width, int height, int checkeredSize) {
        Color light = new Color(0xFF, 0xFF, 0xFF, 0xFF);
        Color dark = new Color(0x7F, 0x7F, 0x7F, 0xFF);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(light);
        g.fillRect(0, 0, width, height);
        g.setColor(dark);
        boolean shift = false;
        for (int x = 0; x < (width + checkeredSize); x += (2 * checkeredSize))
            for (int y = 0; y < (height + checkeredSize); y += checkeredSize) {
                if (shift)
                    g.fillRect(x + checkeredSize, y, checkeredSize, checkeredSize);
                else
                    g.fillRect(x, y, checkeredSize, checkeredSize);
                shift = !shift;
            }
        g.dispose();
        return img;
    }

    private Dimension getBestDimension(int destWidth, int destHeight, int srcWidth, int srcHeight) {

        float wRatio = (float) destWidth / srcWidth;
        float hRatio = (float) destHeight / srcHeight;
        float ratio = Math.min(wRatio, hRatio);
        int width = (int) (srcWidth * ratio);
        int height = (int) (srcHeight * ratio);
        return new Dimension(width, height);

    }

    private void createUIComponents() {
        preview.setBorder(BorderFactory.createDashedBorder(null));
        previewPanel.setSize(800, 800);
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("-x / --rotate");
        model.addElement("-r / --resize");
        model.addElement("-p / --pad");
        model.addElement("-c / --crop");
        model.addElement("-s / --swap-colors");
        opsList.setModel(model);
        opsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
