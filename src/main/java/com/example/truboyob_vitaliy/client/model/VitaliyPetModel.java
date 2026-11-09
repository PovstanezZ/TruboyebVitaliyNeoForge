package com.example.truboyob_vitaliy.client.model;

import com.example.truboyob_vitaliy.entity.VitaliyPetEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;

public class VitaliyPetModel extends PlayerModel<VitaliyPetEntity> {

    public VitaliyPetModel(ModelPart root, boolean slim) {
        super(root, slim);
    }

    @Override
    public void setupAnim(VitaliyPetEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        if (entity.isTame() && entity.isCustomSitting()) {
            this.body.xRot = -0.1745F;
            this.jacket.xRot = -0.1745F;

            this.leftArm.xRot = 0.0F;
            this.leftSleeve.xRot = 0.0F;

            this.rightArm.xRot = -0.8F;
            this.rightArm.yRot = -0.45F;
            this.rightArm.zRot = -0.4295F;

            this.rightSleeve.xRot = -0.8F;
            this.rightSleeve.yRot = -0.45F;
            this.rightSleeve.zRot = -0.4295F;

            this.leftLeg.xRot = -1.5708F;
            this.leftPants.xRot = -1.5708F;
            this.rightLeg.xRot = -1.5708F;
            this.rightPants.xRot = -1.5708F;
        }
    }
}