package com.example.truboyob_vitaliy.init;

import com.example.truboyob_vitaliy.TruboyobVitaliyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, TruboyobVitaliyMod.MODID);

    public static final DeferredHolder<Item, Item> VITALIY_SPAWN_EGG =
            ITEMS.register("vitaliy_spawn_egg",
                    () -> new DeferredSpawnEggItem(
                            ModEntities.VITALIY_PET,
                            0x000000,
                            0xFFFFFF,
                            new Item.Properties()
                    ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        eventBus.addListener(ModItems::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(VITALIY_SPAWN_EGG.get());
        }
    }
}