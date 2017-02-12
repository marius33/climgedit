package main.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private main.Image activeImage;
    private Dimension preferredDimension;

    public GUI() {

        createUIComponents();

        openButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(preferredDimension == null)
                    preferredDimension = previewPanel.getSize();
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(root);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        BufferedImage img = ImageIO.read(file);
                        activeImage = new main.Image(img);
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
                int returnVal = fc.showOpenDialog(root);
            }
        });
        executeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                String[] args = execArgs.getText().split("\\s+");
                System.out.println(Arrays.toString(args));
                int op = opsList.getSelectedIndex();
                switch (op) {
                    case 0: {
                        int angle = Integer.parseInt(args[0]);
                        main.Image.RotateMode mode;
                        if (args[1].equalsIgnoreCase("crop")) {
                            mode = main.Image.RotateMode.CROP;
                        } else if (args[1].equalsIgnoreCase("pad")) {
                            mode = main.Image.RotateMode.PAD;
                        } else mode = main.Image.RotateMode.PAD_KEEP_SIZE;

                        activeImage.rotate(angle, mode);
                        displayImage(activeImage.getImage());

                    }
                }

            }
        });
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
        for (int x = 0; x < (width + 2 * checkeredSize); x += (2 * checkeredSize))
            for (int y = 0; y < (height + 2 * checkeredSize); y += checkeredSize) {
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
