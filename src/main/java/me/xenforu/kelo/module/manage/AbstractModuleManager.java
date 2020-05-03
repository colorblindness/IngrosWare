package me.xenforu.kelo.module.manage;

import me.xenforu.kelo.manager.impl.MapManager;
import me.xenforu.kelo.module.IModule;
import me.xenforu.kelo.module.type.ToggleableModule;

import java.util.ArrayList;
import java.util.List;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public abstract class AbstractModuleManager extends MapManager<String, IModule> {

    protected void register(IModule module) {
        put(module.getLabel(), module);
    }

    public IModule getModule(String name) {
        return getMap().get(name);
    }

    public ToggleableModule getToggleByName(String name) {
        for(ToggleableModule toggleableModule : getToggles()) {
            if(toggleableModule.getLabel().equalsIgnoreCase(name)) return toggleableModule;
        }

        return null;
    }

    public List<ToggleableModule> getToggles() {
        List<ToggleableModule> toggleableModules = new ArrayList<>();
        for(IModule module : getValues()) {
            if(module instanceof ToggleableModule)
                toggleableModules.add((ToggleableModule) module);
        }

        return toggleableModules;
    }


}
