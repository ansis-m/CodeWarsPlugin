package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.KataSetupPanel;
import com.example.codewarsplugin.services.files.parse.KataDirectoryParser;
import com.example.codewarsplugin.state.Store;

import java.awt.*;

public class LogedInView{
    private final Store store;
    private final SidePanel sidePanel;

    public LogedInView(Store store) {
        super();
        this.store = store;
        this.sidePanel = store.getSidePanel();
    }

    public void setup() {

        if ( KataDirectoryParser.getDirectoryList().size() > 0) {
            sidePanel.add(new KataSetupPanel(store), BorderLayout.CENTER);
        }

        sidePanel.revalidate();
        sidePanel.repaint();
    }

    public void cleanup(){
        sidePanel.removeAll();
    }


}
