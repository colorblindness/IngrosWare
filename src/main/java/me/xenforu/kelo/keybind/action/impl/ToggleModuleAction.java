package me.xenforu.kelo.keybind.action.impl;

import me.xenforu.kelo.keybind.action.IAction;
import me.xenforu.kelo.module.type.ToggleableModule;

/**
 * made by Xen for Kelo
 * at 1/3/2020
 **/
public class ToggleModuleAction implements IAction {
    private ToggleableModule toggleableModule;

    public ToggleModuleAction(ToggleableModule toggleableModule) {
        this.toggleableModule = toggleableModule;
    }

    @Override
    public void execute() {
        toggleableModule.toggle();
    }
}
