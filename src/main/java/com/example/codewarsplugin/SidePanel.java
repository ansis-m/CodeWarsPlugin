package com.example.codewarsplugin;

import com.example.codewarsplugin.services.files.MyModuleBuilder;
import com.example.codewarsplugin.services.login.LoginService;
import com.example.codewarsplugin.services.login.WebDriver;
import com.example.codewarsplugin.services.project.MyProjectManager;
import com.example.codewarsplugin.state.StateParams;
import com.example.codewarsplugin.state.SyncService;
import com.example.codewarsplugin.state.Vars;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.EmptyModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import org.jdom.JDOMException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

//177, 54, 30
public class SidePanel extends JPanel {
    private Vars vars;
    private StateParams stateParams;

    public SidePanel(Project project, ToolWindow toolWindow) {
        setLayout(new BorderLayout());
        initPlugin(project, toolWindow);
        vars.getCurrentView().setup();
        MyProjectManager.init(project, toolWindow);
        ProjectManager.getInstance().addProjectManagerListener(new MyProjectManagerListener()); //kautkaada servisa metode
    }

    private void initPlugin(Project project, ToolWindow toolWindow) {
        vars = new Vars(this);
        WebDriver.init();

        stateParams = new StateParams();
        stateParams.setProject(project);
        stateParams.setToolWindow(toolWindow);
        stateParams.setSidePanel(this);
        stateParams.setVars(vars);
        stateParams.setCurrentView(LoginService.loginSuccess? vars.getLogedInView() : vars.getLoginView());
        vars.setCurrentView(LoginService.loginSuccess? vars.getLogedInView() : vars.getLoginView());

        SyncService.addParams(stateParams);

        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));

//        ApplicationManager.getApplication().runWriteAction(() -> {
//            // Get the project's base directory (typically the project root)
//            VirtualFile baseDir = LocalFileSystem.getInstance().findFileByPath(project.getBasePath()+ "/mymodule");
//
//            if (baseDir != null) {
//                // Create the new sources root directory within the base directory
//                VirtualFile newSourcesRoot = null;
//                try {
//                    newSourcesRoot = baseDir.createChildDirectory(this, "module");
//                    newSourcesRoot.refresh(true, true);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return;
//                }
//
//                System.out.println("modules: " + Arrays.toString(ModuleManager.getInstance(project).getModules()));
//            }
//        });

        ArrayList<VirtualFile> firstLevelDirectories = new ArrayList<>();

        // Get the base directory of the project
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir != null && baseDir.isDirectory()) {
            // Get the children (directories/files) directly under the base directory
            VirtualFile[] children = baseDir.getChildren();
            for (VirtualFile child : children) {
                if (child.isDirectory()) {
                    // Check if the directory is a sources root of any module
                    if (isSourcesRoot(child, project)) {
                        // Mark this directory as a sources root
                        System.out.println("Sources Root: " + child.getPath());
                    }

                    // Add the directory to the list of first-level directories
                    firstLevelDirectories.add(child);
                }
            }
        }




    }

    private boolean isSourcesRoot(VirtualFile directory, Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
            for (VirtualFile sourceRoot : rootManager.getSourceRoots()) {
                if (VfsUtil.isAncestor(sourceRoot, directory, false)) {
                    return true;
                }
            }
        }
        return false;
    }




    private void cleanup() {
        WebDriver.quit();
    }

    public StateParams getStateParams() {
        return stateParams;
    }
}
