package com.example.truboyob_vitaliy.entity;

import com.example.truboyob_vitaliy.inventory.EntityCraftingContainer;
import com.example.truboyob_vitaliy.inventory.SyncedContainer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VitaliyPetEntity extends TamableAnimal {

    private static final EntityDataAccessor<Boolean> DATA_CUSTOM_SITTING =
            SynchedEntityData.defineId(VitaliyPetEntity.class, EntityDataSerializers.BOOLEAN);

    public final SyncedContainer inventory = new SyncedContainer(18);
    private final EntityCraftingContainer craftMatrix = new EntityCraftingContainer(9);
    private final SyncedContainer armorInventory = new SyncedContainer(4);

    public VitaliyPetEntity(EntityType<? extends VitaliyPetEntity> entityType, Level level) {
        super(entityType, level);
        this.setCustomName(Component.literal("Трубоёб Виталий"));
        this.setCustomNameVisible(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CUSTOM_SITTING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D,
                Ingredient.of(Items.IRON_INGOT), false));
        // ✅ В 1.21.1 конструктор без boolean
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F, 1.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack itemInHand = player.getItemInHand(hand);

        if (!this.isTame() && this.isFood(itemInHand)) {
            itemInHand.shrink(1);
            if (this.random.nextInt(3) == 0) {
                this.tame(player);
                this.setCustomSitting(true);
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer serverPlayer) {
                // ✅ В NeoForge 1.21.1 используем ванильный openMenu с MenuProvider
                serverPlayer.openMenu(this.getMenuProvider(), buf -> buf.writeVarInt(this.getId()));
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && itemInHand.isEmpty() && !player.isShiftKeyDown()) {
            boolean currentSitting = this.isCustomSitting();
            this.setCustomSitting(!currentSitting);
            this.jumping = false;
            this.navigation.stop();
            this.setTarget(null);
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    public void setCustomSitting(boolean sitting) {
        this.entityData.set(DATA_CUSTOM_SITTING, sitting);
        super.setOrderedToSit(sitting);
    }

    public boolean isCustomSitting() {
        return this.entityData.get(DATA_CUSTOM_SITTING);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.IRON_INGOT);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        // ✅ В 1.21.1 createTag/fromTag требуют HolderLookup.Provider
        HolderLookup.Provider registries = this.level().registryAccess();
        tag.put("PetInventory", this.inventory.createTag(registries));
        tag.put("CraftMatrix", this.craftMatrix.createTag(registries));
        tag.put("ArmorInventory", this.armorInventory.createTag(registries));
        tag.putBoolean("CustomSitting", this.isCustomSitting());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        HolderLookup.Provider registries = this.level().registryAccess();
        if (tag.contains("PetInventory", Tag.TAG_LIST)) {
            this.inventory.fromTag(tag.getList("PetInventory", Tag.TAG_COMPOUND), registries);
        }
        if (tag.contains("CraftMatrix", Tag.TAG_LIST)) {
            this.craftMatrix.fromTag(tag.getList("CraftMatrix", Tag.TAG_COMPOUND), registries);
        }
        if (tag.contains("ArmorInventory", Tag.TAG_LIST)) {
            this.armorInventory.fromTag(tag.getList("ArmorInventory", Tag.TAG_COMPOUND), registries);
        }
        this.setCustomSitting(tag.getBoolean("CustomSitting"));
    }

    public @Nullable MenuProvider getMenuProvider() {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return VitaliyPetEntity.this.getDisplayName();
            }

            @Override
            public @Nullable AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
                return new com.example.truboyob_vitaliy.inventory.VitaliyPetMenu(
                        id, playerInventory, VitaliyPetEntity.this);
            }
        };
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        for (int i = 0; i < this.inventory.getContainerSize(); ++i) {
            ItemStack stack = this.inventory.getItem(i);
            if (!stack.isEmpty()) this.spawnAtLocation(stack);
        }
        for (int i = 0; i < this.armorInventory.getContainerSize(); ++i) {
            ItemStack stack = this.armorInventory.getItem(i);
            if (!stack.isEmpty()) this.spawnAtLocation(stack);
        }
        for (int i = 0; i < this.craftMatrix.getContainerSize(); ++i) {
            ItemStack stack = this.craftMatrix.getItem(i);
            if (!stack.isEmpty()) this.spawnAtLocation(stack);
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mob) {
        return null;
    }

    public EntityCraftingContainer getCraftMatrix() {
        return craftMatrix;
    }

    public SyncedContainer getArmorInventory() {
        return armorInventory;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return new ItemStack(Items.SPYGLASS);
        }
        // ✅ В 1.21.1 Type.ARMOR переименован в Type.HUMANOID_ARMOR
        if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
            int guiIndex = 3 - slot.getIndex();
            if (guiIndex >= 0 && guiIndex < this.armorInventory.getContainerSize()) {
                return this.armorInventory.getItem(guiIndex);
            }
        }
        return super.getItemBySlot(slot);
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        // ✅ Аналогично: HUMANOID_ARMOR
        if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
            int guiIndex = 3 - slot.getIndex();
            if (guiIndex >= 0 && guiIndex < this.armorInventory.getContainerSize()) {
                this.armorInventory.setItem(guiIndex, stack);
            }
            return;
        }
        super.setItemSlot(slot, stack);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < this.armorInventory.getContainerSize(); i++) {
            list.add(this.armorInventory.getItem(i));
        }
        return list;
    }

    @Override
    public boolean canTakeItem(ItemStack itemStack) {
        return false;
    }
}