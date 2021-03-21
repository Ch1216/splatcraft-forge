package com.cibernet.splatcraft.client.model;// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import net.minecraft.client.renderer.model.ModelRenderer;

import java.util.ArrayList;

public class InkTankModel extends AbstractInkTankModel
{
    private final ModelRenderer Head;
    private final ModelRenderer Torso;
    private final ModelRenderer Ink_Tank;

    public InkTankModel()
    {
        textureWidth = 128;
        textureHeight = 128;

        Head = new ModelRenderer(this);
        Head.setRotationPoint(0.0F, 0.0F, 0.0F);
        Head.setTextureOffset(0, 112).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        Torso = new ModelRenderer(this);
        Torso.setRotationPoint(0.0F, -0.25F, 0.0F);
        Torso.setTextureOffset(0, 0).addBox(-4.75F, -0.25F, -2.5F, 9.0F, 12.0F, 5.0F, 0.0F, false);
        Torso.setTextureOffset(31, 0).addBox(-1.0F, 3.0F, 2.5F, 2.0F, 1.0F, 1.0F, 0.0F, false);

        Ink_Tank = new ModelRenderer(this);
        Ink_Tank.setRotationPoint(0.0F, 0.75F, 0.75F);
        Torso.addChild(Ink_Tank);
        Ink_Tank.setTextureOffset(31, 2).addBox(-0.5F, 1.75F, 2.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(0, 19).addBox(-2.0F, 3.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(16, 19).addBox(-2.0F, 11.25F, 3.25F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(0, 24).addBox(1.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(6, 24).addBox(1.0F, 4.25F, 6.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(10, 24).addBox(-2.0F, 4.25F, 6.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(14, 24).addBox(-2.0F, 4.25F, 3.25F, 1.0F, 7.0F, 1.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(12, 39).addBox(0.0F, 9.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(12, 39).addBox(0.0F, 7.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(12, 39).addBox(0.0F, 5.25F, 6.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(0, 33).addBox(-1.0F, 2.25F, 4.25F, 2.0F, 1.0F, 2.0F, 0.0F, false);
        Ink_Tank.setTextureOffset(8, 33).addBox(-3.5F, 2.5F, 4.25F, 2.0F, 1.0F, 2.0F, 0.0F, false);

        inkPieces = new ArrayList<>();
        inkBarY = 23.25F;

        for (int i = 0; i < 7; i++)
        {
            ModelRenderer ink = new ModelRenderer(this);
            ink.setRotationPoint(0.0F, inkBarY, -0.75F);
            Ink_Tank.addChild(ink);

            ink.setTextureOffset(116, 30).addBox(-1.5F, -12F, 4.5F, 3, 1, 3, 0);

            inkPieces.add(ink);
        }

        bipedBody = new ModelRenderer(this);
        bipedBody.addChild(Torso);
    }
}
