package com.example.codewarsplugin;

import com.example.codewarsplugin.services.login.WebDriver;
import com.example.codewarsplugin.state.SyncService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.jcef.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static com.example.codewarsplugin.config.StringConstants.SIGN_IN_URL;

public class CodewarsToolWindowFactory implements ToolWindowFactory {

    public static final JBCefClient client = JBCefApp.getInstance().createClient();
    public static final JBCefBrowser browser = new JBCefBrowser(client, SIGN_IN_URL);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {





        SidePanel panel = new SidePanel(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
        panel.revalidate();
        panel.repaint();




        SwingUtilities.getWindowAncestor(panel).addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("closing");
                SyncService.remove(panel.getStateParams());
            }
        });
    }
}
