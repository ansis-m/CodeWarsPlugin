package com.example.codewarsplugin.persistence;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "CodewarsPluginData", storages = {@Storage("codewars.xml")})
public class DataStore implements PersistentStateComponent<DataStore> {


    @Override
    public @Nullable DataStore getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull DataStore state) {
        // Copy the data from the loaded state to your instance
//        this.dataItems = state.dataItems;
//        this.someOtherField = state.someOtherField;
        XmlSerializerUtil.copyBean(state, this);
    }
}
