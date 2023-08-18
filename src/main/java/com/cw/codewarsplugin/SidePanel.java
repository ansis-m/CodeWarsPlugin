package com.cw.codewarsplugin;

import com.cw.codewarsplugin.state.Store;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {


    public SidePanel(Project project) {
        setLayout(new BorderLayout());
        revalidate();
        repaint();
        new Store(this, project);
    }

}
