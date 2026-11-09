package com.example.truboyob_vitaliy.client.renderer;

import com.example.truboyob_vitaliy.TruboyobVitaliyMod;
import com.example.truboyob_vitaliy.client.model.VitaliyPetModel;
import com.example.truboyob_vitaliy.entity.VitaliyPetEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

// ✅ Убираем bus = EventBusSubscriber.Bus.MOD (deprecated в NeoForge 21.1.265)
@EventBusSubscriber(modid = TruboyobVitaliyMod.MODID, value = Dist.CLIENT)
public class VitaliyPetRenderer extends MobRenderer<VitaliyPetEntity, VitaliyPetModel> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.parse(TruboyobVitaliyMod.MODID + ":vitaliy_pet"),
                    "main");

    public static final ModelLayerLocation ARMOR_INNER_LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.parse(TruboyobVitaliyMod.MODID + ":vitaliy_pet_armor_inner"),
                    "main");

    public static final ModelLayerLocation ARMOR_OUTER_LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.parse(TruboyobVitaliyMod.MODID + ":vitaliy_pet_armor_outer"),
                    "main");

    private static final ResourceLocation TEXTURE =
            ResourceLocation.parse(TruboyobVitaliyMod.MODID + ":textures/entity/vitaliy_pet.png");

    public VitaliyPetRenderer(EntityRendererProvider.Context context) {
        super(context, new VitaliyPetModel(context.bakeLayer(LAYER_LOCATION), false), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(
                this,
                new HumanoidModel<>(context.bakeLayer(ARMOR_INNER_LAYER_LOCATION)),
                new HumanoidModel<>(context.bakeLayer(ARMOR_OUTER_LAYER_LOCATION)),
                context.getModelManager()
        ));

        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(VitaliyPetEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(VitaliyPetEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.isCustomSitting()) {
            poseStack.translate(0.0F, -0.5F, 0.0F);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LAYER_LOCATION, () ->
                LayerDefinition.create(PlayerModel.createMesh(CubeDeformation.NONE, false), 64, 64));

        event.registerLayerDefinition(ARMOR_INNER_LAYER_LOCATION, () ->
                LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(0.5F), 0.0F), 64, 32));

        event.registerLayerDefinition(ARMOR_OUTER_LAYER_LOCATION, () ->
                LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(1.0F), 0.0F), 64, 32));
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                com.example.truboyob_vitaliy.init.ModEntities.VITALIY_PET.get(),
                VitaliyPetRenderer::new
        );
    }
}