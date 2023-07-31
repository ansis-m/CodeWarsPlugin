package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.KataSubmitPanel;
import com.example.codewarsplugin.components.NavigationBar;
import com.example.codewarsplugin.components.UserPanel;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.services.project.MyProjectManager;
import com.example.codewarsplugin.state.Store;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;

import java.awt.*;

public class WorkView implements View{


    private Store store;
    private SidePanel sidePanel;
    private UserPanel userPanel;
    private NavigationBar navigationBar;
    private KataSubmitPanel submitPanel;

    private KataInput input;
    private KataRecord record;

    public WorkView(Store store){
        this.store = store;
        this.sidePanel = store.getSidePanel();
    }


    @Override
    public void setup() {

        var directory = store.getDirectory();

        OpenFileDescriptor descriptor = new OpenFileDescriptor(MyProjectManager.getProject(), directory.getTestFile());
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(MyProjectManager.getProject());
        fileEditorManager.openTextEditor(descriptor, false);

        descriptor = new OpenFileDescriptor(MyProjectManager.getProject(), directory.getWorkFile());
        fileEditorManager.openTextEditor(descriptor, true);

        userPanel = new UserPanel(UserService.getUser(), store);
        navigationBar = new NavigationBar(store);
        submitPanel = new KataSubmitPanel(store);


        sidePanel.add(submitPanel, BorderLayout.CENTER);
        sidePanel.add(userPanel, BorderLayout.NORTH);
        sidePanel.add(navigationBar, BorderLayout.SOUTH);
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    @Override
    public void cleanup() {
        sidePanel.removeAll();
        userPanel = null;
        navigationBar = null;
        sidePanel.revalidate();
        sidePanel.repaint();

    }

    @Override
    public void refreshUserPanel() {
        sidePanel.remove(userPanel);
        userPanel = new UserPanel(UserService.getUser(), store);
        sidePanel.add(userPanel, BorderLayout.NORTH);
        sidePanel.revalidate();
        sidePanel.repaint();
    }
}
