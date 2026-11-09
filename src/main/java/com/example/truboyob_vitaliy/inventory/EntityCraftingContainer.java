package com.example.truboyob_vitaliy.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

public class EntityCraftingContainer extends SyncedContainer implements CraftingContainer {

    public EntityCraftingContainer(int size) {
        super(size);
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> list = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < this.getContainerSize(); i++) {
            list.set(i, this.getItem(i));
        }
        return list;
    }
}