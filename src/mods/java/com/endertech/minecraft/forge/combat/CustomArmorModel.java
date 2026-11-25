package com.endertech.minecraft.forge.combat;

import com.endertech.minecraft.forge.ForgeLocating;
import com.endertech.minecraft.forge.units.IForgeUnit;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomArmorModel {
   protected final ModelBase baseModel;
   protected final ModelBiped bipedModel;
   protected final EntityEquipmentSlot armorSlot;
   private final String texture;

   protected CustomArmorModel(IForgeUnit unit, ModelBase baseModel, final EntityEquipmentSlot armorSlot) {
      this.baseModel = baseModel;
      this.texture = ForgeLocating.armorTexture(unit.getId().getModId(), unit.getId().getRegName()).toString();
      this.armorSlot = armorSlot;
      this.bipedModel = new ModelBiped() {
         public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
            GlStateManager.pushMatrix();
            if (this.isChild) {
               GlStateManager.scale(0.75F, 0.75F, 0.75F);
               GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
               if (armorSlot == EntityEquipmentSlot.HEAD) {
                  this.bipedHead.render(scale);
               }

               GlStateManager.popMatrix();
               GlStateManager.pushMatrix();
               GlStateManager.scale(0.5F, 0.5F, 0.5F);
               GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
               if (armorSlot == EntityEquipmentSlot.CHEST) {
                  this.bipedBody.render(scale);
               }
            } else {
               if (entityIn.isSneaking()) {
                  GlStateManager.translate(0.0F, 0.2F, 0.0F);
               }

               if (armorSlot == EntityEquipmentSlot.HEAD) {
                  this.bipedHead.render(scale);
               }

               if (armorSlot == EntityEquipmentSlot.CHEST) {
                  this.bipedBody.render(scale / 2.0F);
               }
            }

            GlStateManager.popMatrix();
         }
      };
      ModelRenderer renderedModel = new ModelRenderer(this.bipedModel);
      switch(armorSlot) {
      case HEAD:
         this.bipedModel.bipedHead = renderedModel;
         break;
      case CHEST:
         this.bipedModel.bipedBody = renderedModel;
      }

      if (renderedModel != null) {
         for(ModelRenderer box : baseModel.boxList) {
            renderedModel.addChild(box);
            box.setTextureSize(baseModel.textureWidth, baseModel.textureHeight);
         }
      }

   }

   public static CustomArmorModel of(IForgeUnit unit, ModelBase model, EntityEquipmentSlot slot) {
      return new CustomArmorModel(unit, model, slot);
   }

   public ModelBiped getModelFor(EntityLivingBase living) {
      this.bipedModel.isChild = living.isChild();
      this.bipedModel.isSneak = living.isSneaking();
      return this.bipedModel;
   }

   public String getTexture() {
      return this.texture;
   }
}
