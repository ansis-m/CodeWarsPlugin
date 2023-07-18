package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.user.User;
import com.example.codewarsplugin.state.SyncService;
import com.example.codewarsplugin.state.Vars;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserPanel extends JPanel {

    private JButton logoutButton = new JButton("LOG OUT");

    public UserPanel(User user, Vars vars){
        if (user == null) {
            return;
        }
        setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        JLabel nameLabel = new JLabel(user.getUsername());
        Font newFont = nameLabel.getFont().deriveFont(15f);
        nameLabel.setFont(newFont);
        add(nameLabel);
        try{
            String url = extractImageUrl(user.getAvatar_tag());
            URL imageUrl = new URL(url);
            BufferedImage image = ImageIO.read(imageUrl);
            ImageIcon imageIcon = new ImageIcon(image);
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
            add(imageLabel, BorderLayout.NORTH);
        } catch (Exception ignored) {}

        JLabel rankLabel = new JLabel(String.valueOf(Math.abs(user.getRank())) + " kyu");
        rankLabel.setFont(newFont);
        add(rankLabel);
        JLabel honorLabel = new JLabel(String.valueOf(user.getHonor()));
        honorLabel.setFont(newFont);
        add(honorLabel);
        logoutButton.addActionListener((e) -> {
            SyncService.logout();
        });
        logoutButton.setVisible(true);
        add(logoutButton);
    }

    private static String extractImageUrl(String html) {
        String regex = "<img[^>]+src\\s*=\\s*\"([^\"]+)\"[^>]*>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}