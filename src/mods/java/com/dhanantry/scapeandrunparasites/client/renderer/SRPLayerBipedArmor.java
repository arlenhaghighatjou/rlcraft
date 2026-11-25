package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.client.model.entity.SRPModelBiped;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SRPLayerBipedArmor extends LayerArmorBase<ModelBiped> {
   private final RenderLivingBase<?> renderer;
   private float alpha = 1.0F;
   private float colorR = 1.0F;
   private float colorG = 1.0F;
   private float colorB = 1.0F;
   private boolean skipRenderGlint;
   private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

   public SRPLayerBipedArmor(RenderLivingBase<?> rendererIn) {
      super(rendererIn);
      this.renderer = rendererIn;
   }

   protected void initArmor() {
      this.modelLeggings = new SRPModelBiped(0.5F);
      this.modelArmor = new SRPModelBiped(1.0F);
   }

   public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      byte type = 0;
      if (entitylivingbaseIn instanceof EntityInfPlayer) {
         type = 1;
      }

      this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS, type);
      this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET, type);
      this.renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD, type);
   }

   private void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, byte in) {
      ItemStack itemstack = entityLivingBaseIn.getItemStackFromSlot(slotIn);
      if (itemstack.getItem() instanceof ItemArmor) {
         ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
         if (itemarmor.getEquipmentSlot() == slotIn) {
            if (!(this.getModelFromSlot(slotIn) instanceof ModelBiped)) {
               return;
            }

            SRPModelBiped t = (SRPModelBiped)this.getModelFromSlot(slotIn);
            if (this.getArmorModelHook(entityLivingBaseIn, itemstack, slotIn, t) instanceof ModelBiped) {
               t = (SRPModelBiped)this.getArmorModelHook(entityLivingBaseIn, itemstack, slotIn, t);
            }

            t.setParent(in);
            t.setModelAttributes(this.renderer.getMainModel());
            t.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.setModelSlotVisible2(t, slotIn);
            this.renderer.bindTexture(this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, (String)null));
            if (itemarmor.hasOverlay(itemstack)) {
               int i = itemarmor.getColor(itemstack);
               float f = (float)(i >> 16 & 255) / 255.0F;
               float f1 = (float)(i >> 8 & 255) / 255.0F;
               float f2 = (float)(i & 255) / 255.0F;
               GlStateManager.color(this.colorR * f, this.colorG * f1, this.colorB * f2, this.alpha);
               t.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
               this.renderer.bindTexture(this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, "overlay"));
            }

            GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
            t.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            if (!this.skipRenderGlint && itemstack.hasEffect()) {
               renderEnchantedGlint(this.renderer, entityLivingBaseIn, t, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            }
         }
      }

   }

   protected void setModelSlotVisible2(SRPModelBiped model, EntityEquipmentSlot slotIn) {
      this.setModelVisible(model);
      switch(slotIn) {
      case HEAD:
         model.jointH.showModel = true;
         model.jointHW.showModel = true;
         break;
      case CHEST:
         model.body.showModel = true;
         model.rightA.showModel = true;
         model.leftA.showModel = true;
         break;
      case LEGS:
         model.body.showModel = true;
         model.rightL.showModel = true;
         model.leftL.showModel = true;
         break;
      case FEET:
         model.rightL.showModel = true;
         model.leftL.showModel = true;
      }

   }

   protected void setModelVisible(ModelBiped model) {
      model.setVisible(false);
   }

   protected ModelBiped getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model) {
      return ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
   }

   protected void setModelSlotVisible(ModelBiped model, EntityEquipmentSlot slotIn) {
      if (model instanceof SRPModelBiped) {
         this.setModelSlotVisible2((SRPModelBiped)model, slotIn);
      }

   }
}
