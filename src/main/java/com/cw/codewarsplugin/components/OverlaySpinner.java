package com.cw.codewarsplugin.components;

import com.intellij.ui.AnimatedIcon;

import javax.swing.*;
import java.awt.*;

public class OverlaySpinner extends JPanel {

    public OverlaySpinner(){
        super(new GridBagLayout());
        add(new JLabel(new AnimatedIcon.Big()));
    }
}
