package com.example.codewarsplugin.components;

import javax.swing.*;
import java.awt.*;

public class WaitPanel extends JPanel {



    public WaitPanel(){
        super();
        // We suppose you have already set your JFrame
        ImageIcon imgIcon = new ImageIcon(this.getClass().getResource("/icons/giphy.gif"));



        //Image originalImage = imgIcon.getImage();

        // Scale the image to the desired dimensions
        //Image scaledImage = originalImage.getScaledInstance(30, 30, Image.SCALE_DEFAULT);

        // Create a new ImageIcon with the scaled image
        //ImageIcon scaledIcon = new ImageIcon(scaledImage);




        JLabel label = new JLabel(imgIcon);
        //label.setBounds(668, 43, 46, 14); // You can use your own values
        add(label, BorderLayout.NORTH);

    }

}
