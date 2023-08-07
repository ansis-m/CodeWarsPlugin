package com.example.codewarsplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

public class MyActionClass extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        Messages.showMessageDialog(
                "This is the message body.",
                "This is the title",
                Messages.getInformationIcon()
        );
    }
}
