package com.example.truboyob_vitaliy.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class SyncedContainer extends SimpleContainer {

    private AbstractContainerMenu menu;

    public SyncedContainer(int size) {
        super(size);
    }

    public void setMenu(AbstractContainerMenu menu) {
        this.menu = menu;
    }

    public AbstractContainerMenu getMenu() {
        return menu;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.menu != null) {
            this.menu.slotsChanged(this);
        }
    }
}