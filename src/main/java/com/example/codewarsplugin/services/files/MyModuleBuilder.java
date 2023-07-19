package com.example.codewarsplugin.services.files;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MyModuleBuilder extends ModuleBuilder {

    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel){
        // Perform any additional configuration to the module, if needed
        // For example, you can set the content root and source folders

        // Set the content root (where the module's files will be located)
        VirtualFile contentRoot = LocalFileSystem.getInstance().findFileByPath(getModuleFilePath());
        if (contentRoot != null) {
            ContentEntry contentEntry = modifiableRootModel.addContentEntry(contentRoot);
            // Set the source folders for your module
            VirtualFile sourceFolder = null;
            try {
                sourceFolder = contentRoot.createChildDirectory(this, "src");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            contentEntry.addSourceFolder(sourceFolder, false);

            // Mark the source folder as a sources root to enable language-specific features
            ModuleRootModificationUtil.setModuleSdk(modifiableRootModel.getModule(), null);
        }
    }

    @Override
    public ModuleType<?> getModuleType() {
        return ModuleType.EMPTY;
    }

    @Override
    public String getPresentableName() {
        // Return the name of your module
        return "MyModule";
    }

    @Override
    public String getParentGroup() {
        // Return the name of the module's parent group (if applicable)
        // You can return null if you don't want to group the module under a specific category
        return null;
    }

    @Override
    public String getGroupName() {
        // Return the name of the module's group (if applicable)
        // You can return null if you don't want to group the module under a specific category
        return null;
    }

    public Module createModule(Project project) throws ConfigurationException {
        // Create the module using the ModuleManager
        @NotNull Module module = ModuleManager.getInstance(project).newModule(getModuleFilePath(), getModuleType().getId());

        // Initialize the root model
        ModifiableRootModel modifiableRootModel = ModuleRootManager.getInstance(module).getModifiableModel();
        try {
            setupRootModel(modifiableRootModel);
        } finally {
            modifiableRootModel.commit();
        }

        return module;
    }
}