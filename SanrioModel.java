package com.sanriofriends.client;

import com.sanriofriends.SanrioCharacter;
import com.sanriofriends.SanrioFriends;
import com.sanriofriends.entity.SanrioEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.EnumMap;
import java.util.Map;

/**
 * Modelo cartoon de cabeza grande, compartido por los cinco personajes.
 * Las orejas, lazos, capuchas y colas de cada uno salen de {@link ModelSpecs} (generado por tools/parts.py).
 */
public class SanrioModel extends EntityModel<SanrioEntity> {
    private static final Map<SanrioCharacter, ModelLayerLocation> LAYERS = new EnumMap<>(SanrioCharacter.class);

    static {
        for (SanrioCharacter c : SanrioCharacter.values()) {
            LAYERS.put(c, new ModelLayerLocation(new ResourceLocation(SanrioFriends.MOD_ID, c.id), "main"));
        }
    }

    public static ModelLayerLocation layer(SanrioCharacter c) {
        return LAYERS.get(c);
    }

    private final SanrioCharacter character;
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart earL;
    private final ModelPart earR;
    private final ModelPart tail;
    private final float earLBase;
    private final float earRBase;

    public SanrioModel(ModelPart root, SanrioCharacter character) {
        this.character = character;
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.earL = this.head.getChild("ear_l");
        this.earR = this.head.getChild("ear_r");
        this.tail = this.body.getChild("tail");
        this.earLBase = this.earL.zRot;
        this.earRBase = this.earR.zRot;
        if (character == SanrioCharacter.CINNAMOROLL) {
            this.head.getChild("acc2").visible = false;
        }
    }

    public static LayerDefinition createBodyLayer(SanrioCharacter c) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, 0.0F, -2.0F, 6.0F, 6.0F, 4.0F),
                PartPose.offset(0.0F, 14.0F, 0.0F));
        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(20, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offset(-4.0F, 15.0F, 0.0F));
        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(28, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offset(4.0F, 15.0F, 0.0F));
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 26).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F),
                PartPose.offset(-1.5F, 20.0F, 0.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(12, 26).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F),
                PartPose.offset(1.5F, 20.0F, 0.0F));

        for (ModelSpecs.Spec s : ModelSpecs.of(c)) {
            PartDefinition parent = s.parent().equals("body") ? body : head;
            parent.addOrReplaceChild(s.name(),
                    CubeListBuilder.create().texOffs(s.u(), s.v()).addBox(s.x(), s.y(), s.z(), s.w(), s.h(), s.d()),
                    PartPose.offsetAndRotation(s.px(), s.py(), s.pz(), s.rx(), s.ry(), s.rz()));
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(SanrioEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float rad = (float) Math.PI / 180.0F;
        this.head.yRot = netHeadYaw * rad;
        this.head.xRot = headPitch * rad;

        boolean flying = this.character == SanrioCharacter.CINNAMOROLL;
        if (flying) {
            float flap = Mth.sin(ageInTicks * 0.3F);
            this.rightLeg.xRot = 0.25F + Mth.sin(ageInTicks * 0.12F) * 0.15F;
            this.leftLeg.xRot = 0.25F - Mth.sin(ageInTicks * 0.12F) * 0.15F;
            this.rightArm.zRot = 0.35F + flap * 0.1F;
            this.leftArm.zRot = -0.35F - flap * 0.1F;
            this.rightArm.xRot = 0.0F;
            this.leftArm.xRot = 0.0F;
            this.earL.zRot = this.earLBase - 0.15F + flap * 0.45F;
            this.earR.zRot = this.earRBase + 0.15F - flap * 0.45F;
        } else {
            float swing = limbSwingAmount;
            this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.3F * swing;
            this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.3F * swing;
            this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.0F * swing;
            this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 1.0F * swing;
            this.rightArm.zRot = 0.05F + Mth.sin(ageInTicks * 0.09F) * 0.03F;
            this.leftArm.zRot = -0.05F - Mth.sin(ageInTicks * 0.09F) * 0.03F;
            this.earL.zRot = this.earLBase + Mth.sin(ageInTicks * 0.11F) * 0.05F;
            this.earR.zRot = this.earRBase - Mth.sin(ageInTicks * 0.11F + 1.0F) * 0.05F;
        }
        this.tail.yRot = Mth.sin(ageInTicks * 0.25F) * 0.45F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
