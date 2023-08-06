package com.example.codewarsplugin;

import com.example.codewarsplugin.state.Store;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {

    public SidePanel(Project project) {
        super();
        OverlayLayout overlayLayout = new OverlayLayout(this);
        setLayout(overlayLayout);
        new Store(this, project);
    }
}
