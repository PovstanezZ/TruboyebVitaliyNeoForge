package com.example.truboyob_vitaliy.init;

import com.example.truboyob_vitaliy.TruboyobVitaliyMod;
import com.example.truboyob_vitaliy.entity.VitaliyPetEntity;
import com.example.truboyob_vitaliy.inventory.VitaliyPetMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, TruboyobVitaliyMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<VitaliyPetMenu>> VITALIY_PET_MENU =
            MENU_TYPES.register("vitaliy_pet_menu", () ->
                    IMenuTypeExtension.create((windowId, inv, data) ->
                            new VitaliyPetMenu(windowId, inv,
                                    (VitaliyPetEntity) inv.player.level().getEntity(data.readVarInt()))));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}