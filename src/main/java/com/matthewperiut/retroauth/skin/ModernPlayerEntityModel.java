package com.matthewperiut.retroauth.skin;

import com.matthewperiut.retroauth.skin.data.ModelPartData;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public class ModernPlayerEntityModel extends HumanoidModel {
    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPantLeg;
    public final ModelPart rightPantLeg;
    public final ModelPart jacket;

    public ModernPlayerEntityModel(float scale, boolean thinArms) {
        super(scale);
        float slimShoulderHeight = 2.5F;

        if (thinArms) {
            this.leftArm = this.createModelPart(32, 48);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale);
            this.leftArm.setPos(5.0F, slimShoulderHeight, 0.0F);
            this.rightArm = this.createModelPart(40, 16);
            this.rightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale);
            this.rightArm.setPos(-5.0F, slimShoulderHeight, 0.0F);
            this.leftSleeve = this.createModelPart(48, 48);
            this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
            this.leftSleeve.setPos(5.0F, slimShoulderHeight, 0.0F);
            this.rightSleeve = this.createModelPart(40, 32);
            this.rightSleeve.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, scale + 0.25F);
            this.rightSleeve.setPos(-5.0F, slimShoulderHeight, 10.0F);
        } else {
            this.leftArm = this.createModelPart(32, 48);
            this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale);
            this.leftArm.setPos(5.0F, 2.0F, 0.0F);
            this.leftSleeve = this.createModelPart(48, 48);
            this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
            this.leftSleeve.setPos(5.0F, 2.0F, 0.0F);
            this.rightSleeve = this.createModelPart(40, 32);
            this.rightSleeve.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scale + 0.25F);
            this.rightSleeve.setPos(-5.0F, 2.0F, 10.0F);
        }

        this.leftLeg = this.createModelPart(16, 48);
        this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale);
        this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
        this.leftPantLeg = this.createModelPart(0, 48);
        this.leftPantLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
        this.leftPantLeg.setPos(1.9F, 12.0F, 0.0F);
        this.rightPantLeg = this.createModelPart(0, 32);
        this.rightPantLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scale + 0.25F);
        this.rightPantLeg.setPos(-1.9F, 12.0F, 0.0F);
        this.jacket = this.createModelPart(16, 32);
        this.jacket.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale + 0.25F);
        this.jacket.setPos(0.0F, 0.0F, 0.0F);
    }

    private ModelPart createModelPart(int x, int y) {
        ModelPart modelRenderer = new ModelPart(x, y);
        ((ModelPartData) modelRenderer).setTextureHeight(64);
        return modelRenderer;
    }

    @Override
    public void render(float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float scale) {
        super.render(limbAngle, limbDistance, animationProgress, headYaw, headPitch, scale);

        this.leftPantLeg.render(scale);
        this.rightPantLeg.render(scale);
        this.leftSleeve.render(scale);
        this.rightSleeve.render(scale);
        this.jacket.render(scale);
    }

    public void copyPositionAndRotation(ModelPart from, ModelPart to) {
        to.setPos(from.x, from.y, from.z);
        to.yRot = from.yRot;
        to.xRot = from.xRot;
        to.zRot = from.zRot;
    }

    @Override
    public void setupAnim(float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, float scale) {
        super.setupAnim(limbAngle, limbDistance, animationProgress, headYaw, headPitch, scale);
        this.copyPositionAndRotation(this.leftLeg, this.leftPantLeg);
        this.copyPositionAndRotation(this.rightLeg, this.rightPantLeg);
        this.copyPositionAndRotation(this.leftArm, this.leftSleeve);
        this.copyPositionAndRotation(this.rightArm, this.rightSleeve);
        this.copyPositionAndRotation(this.body, this.jacket);
        this.copyPositionAndRotation(this.head, this.hair);
    }
}
