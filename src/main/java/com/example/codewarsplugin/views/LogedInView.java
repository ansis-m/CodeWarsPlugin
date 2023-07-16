package com.example.codewarsplugin.views;

import com.example.codewarsplugin.models.user.User;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.state.SyncService;
import com.example.codewarsplugin.state.Vars;
import com.intellij.openapi.ui.ComboBox;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogedInView extends JPanel implements View{

    private User user;
    private JPanel userPanel;


    private JButton logoutButton = new JButton("LOG OUT");
    private Vars vars;

    public LogedInView(Vars vars) {
        super();
        this.vars = vars;
    }


    public void setup() {
        user = UserService.getUser();
        setupUserFields(user);
        vars.getSidePanel().add(vars.getKataPrompt(), BorderLayout.CENTER);
        vars.getSidePanel().revalidate();
        vars.getSidePanel().repaint();
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


    private void setupUserFields(User user) {

        userPanel = new JPanel();
        if (user == null) {
            return;
        }
        userPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        JLabel nameLabel = new JLabel(user.getUsername());
        Font newFont = nameLabel.getFont().deriveFont(15f);
        nameLabel.setFont(newFont);
        userPanel.add(nameLabel);
        try{
            String url = extractImageUrl(user.getAvatar_tag());
            URL imageUrl = new URL(url);
            BufferedImage image = ImageIO.read(imageUrl);
            ImageIcon imageIcon = new ImageIcon(image);
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
            userPanel.add(imageLabel, BorderLayout.NORTH);
        } catch (Exception ignored) {}

        JLabel rankLabel = new JLabel(String.valueOf(Math.abs(user.getRank())) + " kyu");
        rankLabel.setFont(newFont);
        userPanel.add(rankLabel);
        JLabel honorLabel = new JLabel(String.valueOf(user.getHonor()));
        honorLabel.setFont(newFont);
        userPanel.add(honorLabel);
        logoutButton.addActionListener((e) -> {
            SyncService.logout();
        });
        logoutButton.setVisible(true);
        userPanel.add(logoutButton);

        vars.getSidePanel().add(userPanel, BorderLayout.NORTH);
    }

    public void cleanup(){
        vars.getSidePanel().removeAll();
        userPanel = null;
    }

}
