package com.example.truboyob_vitaliy;

import com.example.truboyob_vitaliy.init.ModEntities;
import com.example.truboyob_vitaliy.init.ModItems;
import com.example.truboyob_vitaliy.init.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TruboyobVitaliyMod.MODID)
public class TruboyobVitaliyMod {
    public static final String MODID = "truboyob_vitaliy";
    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public TruboyobVitaliyMod(IEventBus modEventBus) {
        ModEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(this::registerScreens);
        }

        LOGGER.info("Мод '{}' успешно инициализирован!", MODID);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Выполняется общая настройка (Common Setup)...");
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.VITALIY_PET_MENU.get(),
                com.example.truboyob_vitaliy.client.screen.VitaliyPetScreen::new);
        LOGGER.info("✅ Экран меню питомца зарегистрирован!");
    }
}