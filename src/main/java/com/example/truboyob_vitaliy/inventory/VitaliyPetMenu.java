package com.example.truboyob_vitaliy.inventory;

import com.example.truboyob_vitaliy.entity.VitaliyPetEntity;
import com.example.truboyob_vitaliy.init.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class VitaliyPetMenu extends AbstractContainerMenu {

    private final VitaliyPetEntity pet;
    private final ResultContainer resultContainer = new ResultContainer();
    private final CraftingContainer craftMatrix;
    private final SyncedContainer armorInventory;
    private final SyncedContainer petInventory;

    public VitaliyPetMenu(int id, Inventory playerInventory, VitaliyPetEntity pet) {
        super(ModMenuTypes.VITALIY_PET_MENU.get(), id);
        this.pet = pet;
        this.craftMatrix = pet.getCraftMatrix();
        this.armorInventory = pet.getArmorInventory();
        this.petInventory = pet.inventory;

        this.armorInventory.setMenu(this);
        this.petInventory.setMenu(this);
        if (this.craftMatrix instanceof SyncedContainer sc) {
            sc.setMenu(this);
        }

        this.addSlot(new ResultSlot(playerInventory.player, this.craftMatrix,
                this.resultContainer, 0, 134, 45));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new Slot(this.craftMatrix, col + row * 3,
                        62 + col * 18, 27 + row * 18));
            }
        }

        for (int i = 0; i < 4; ++i) {
            final int armorIndex = i;
            this.addSlot(new Slot(armorInventory, armorIndex, 8, 18 + i * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    if (stack.isEmpty()) return false;
                    if (!(stack.getItem() instanceof ArmorItem armor)) {
                        return false;
                    }
                    return switch (armorIndex) {
                        case 0 -> armor.getEquipmentSlot() == EquipmentSlot.HEAD;
                        case 1 -> armor.getEquipmentSlot() == EquipmentSlot.CHEST;
                        case 2 -> armor.getEquipmentSlot() == EquipmentSlot.LEGS;
                        case 3 -> armor.getEquipmentSlot() == EquipmentSlot.FEET;
                        default -> false;
                    };
                }
            });
        }

        for (int row = 0; row < 2; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(petInventory, col + row * 9,
                        8 + col * 18, 97 + row * 18));
            }
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        8 + col * 18, 140 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);

        if (container == this.craftMatrix) {
            this.updateCraftResult();
        }

        if (!this.pet.level().isClientSide) {
            for (int i = 0; i < this.slots.size(); i++) {
                this.setRemoteSlot(i, ItemStack.EMPTY);
            }
            this.broadcastChanges();
        }
    }

    private void updateCraftResult() {
        Level level = this.pet.level();
        ItemStack result = ItemStack.EMPTY;

        CraftingInput input = CraftingInput.of(3, 3, this.craftMatrix.getItems());
        var recipeOpt = level.getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, level);

        if (recipeOpt.isPresent()) {
            // ✅ В 1.21.1 getRecipeFor возвращает RecipeHolder<CraftingRecipe>
            CraftingRecipe recipe = recipeOpt.get().value();
            ItemStack recipeResult = recipe.assemble(input, level.registryAccess());
            if (recipeResult.isItemEnabled(level.enabledFeatures())) {
                result = recipeResult;
            }
        }

        this.resultContainer.setItem(0, result);
        this.setRemoteSlot(0, result);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.pet.isAlive() && this.pet.distanceToSqr(player) < 8.0D;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stackInSlot = slot.getItem();
        result = stackInSlot.copy();

        final int RESULT = 0;
        final int CRAFT_START = 1, CRAFT_END = 10;
        final int ARMOR_START = 10, ARMOR_END = 14;
        final int PET_INV_START = 14, PET_INV_END = 32;
        final int PLAYER_INV_START = 32, HOTBAR_END = 68;

        if (index == RESULT) {
            if (!this.moveItemStackTo(stackInSlot, PLAYER_INV_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stackInSlot, result);
        } else if (index >= CRAFT_START && index < CRAFT_END) {
            if (!this.moveItemStackTo(stackInSlot, PLAYER_INV_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= ARMOR_START && index < ARMOR_END) {
            if (!this.moveItemStackTo(stackInSlot, PLAYER_INV_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PET_INV_START && index < PET_INV_END) {
            if (!this.moveItemStackTo(stackInSlot, PLAYER_INV_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_INV_START && index < HOTBAR_END) {
            boolean equipped = false;
            if (stackInSlot.getItem() instanceof ArmorItem armor) {
                switch (armor.getEquipmentSlot()) {
                    case HEAD -> equipped = this.moveItemStackTo(stackInSlot,
                            ARMOR_START, ARMOR_START + 1, false);
                    case CHEST -> equipped = this.moveItemStackTo(stackInSlot,
                            ARMOR_START + 1, ARMOR_START + 2, false);
                    case LEGS -> equipped = this.moveItemStackTo(stackInSlot,
                            ARMOR_START + 2, ARMOR_START + 3, false);
                    case FEET -> equipped = this.moveItemStackTo(stackInSlot,
                            ARMOR_START + 3, ARMOR_START + 4, false);
                    default -> { }
                }
            }
            if (!equipped) {
                if (!this.moveItemStackTo(stackInSlot, PET_INV_START, PET_INV_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (stackInSlot.getCount() == result.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stackInSlot);
        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItem(0, 1);
        if (!player.level().isClientSide) {
            this.clearContainer(player, this.craftMatrix);
            this.armorInventory.setMenu(null);
            this.petInventory.setMenu(null);
            if (this.craftMatrix instanceof SyncedContainer sc) {
                sc.setMenu(null);
            }
        }
    }
}