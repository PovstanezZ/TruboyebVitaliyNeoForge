package com.example.truboyob_vitaliy.init;

import com.example.truboyob_vitaliy.TruboyobVitaliyMod;
import com.example.truboyob_vitaliy.entity.VitaliyPetEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TruboyobVitaliyMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<VitaliyPetEntity>> VITALIY_PET =
            ENTITY_TYPES.register("vitaliy_pet",
                    () -> EntityType.Builder.of(VitaliyPetEntity::new, MobCategory.CREATURE)
                            .sized(0.6F, 1.8F)
                            .build("vitaliy_pet"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
        eventBus.addListener(ModEntities::registerAttributes);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(VITALIY_PET.get(), TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .build());
    }
}