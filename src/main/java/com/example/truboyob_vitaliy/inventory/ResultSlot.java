package com.example.truboyob_vitaliy.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ResultSlot extends Slot {
    private final Player player;
    private int removeCount;
    private final CraftingContainer craftMatrix;
    private final ResultContainer resultContainer;

    public ResultSlot(Player player, CraftingContainer craftMatrix, ResultContainer resultContainer,
                      int slot, int x, int y) {
        super(resultContainer, slot, x, y);
        this.player = player;
        this.craftMatrix = craftMatrix;
        this.resultContainer = resultContainer;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }
        return super.remove(amount);
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }

    @Override
    protected void onSwapCraft(int numItemsCrafted) {
        this.removeCount += numItemsCrafted;
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
        this.removeCount = 0;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);
        Level level = player.level();
        RecipeManager recipeManager = level.getRecipeManager();

        CraftingInput input = CraftingInput.of(3, 3, this.craftMatrix.getItems());
        RecipeHolder<CraftingRecipe> recipeHolder = recipeManager
                .getRecipeFor(RecipeType.CRAFTING, input, level)
                .orElse(null);

        if (recipeHolder != null) {
            CraftingRecipe recipe = recipeHolder.value();

            // ✅ Защита: remainingItems может быть меньше размера матрицы
            NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(input);

            int matrixSize = this.craftMatrix.getContainerSize();
            int remainingSize = remainingItems.size();
            int limit = Math.min(matrixSize, remainingSize);

            for (int i = 0; i < limit; ++i) {
                ItemStack item = this.craftMatrix.getItem(i);
                ItemStack remaining = remainingItems.get(i);

                if (!item.isEmpty()) {
                    this.craftMatrix.removeItem(i, 1);
                    item = this.craftMatrix.getItem(i);
                }

                if (!remaining.isEmpty()) {
                    if (item.isEmpty()) {
                        this.craftMatrix.setItem(i, remaining);
                    } else if (ItemStack.isSameItemSameComponents(item, remaining)) {
                        remaining.grow(item.getCount());
                        this.craftMatrix.setItem(i, remaining);
                    } else if (!this.player.getInventory().add(remaining)) {
                        this.player.drop(remaining, false);
                    }
                }
            }

            // ✅ Для слотов за пределами remainingItems просто расходуем ингредиенты
            for (int i = limit; i < matrixSize; ++i) {
                ItemStack item = this.craftMatrix.getItem(i);
                if (!item.isEmpty()) {
                    this.craftMatrix.removeItem(i, 1);
                }
            }
        }
    }
}