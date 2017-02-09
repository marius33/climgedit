package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.FileChooserUI;
import java.awt.*;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Marius on 08/02/2017.
 */
public class GUI {
    private JButton browseButton;
    private JList list1;
    private JButton saveButton;
    private JButton executeButton;
    private JTextField textField1;
    JPanel root;
    private JLabel preview;

    public GUI() {

        browseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(root);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        BufferedImage img = ImageIO.read(file);
                        displayImage(img);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {

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
                super.mouseClicked(e);
            }
        });
    }

    void displayImage(java.awt.Image img){
        Image resized = img.getScaledInstance(800, 600, Image.SCALE_SMOOTH);
        preview.setIcon(new ImageIcon(resized));
    }
}
