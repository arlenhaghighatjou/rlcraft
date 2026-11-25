package com.endertech.minecraft.forge.items;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.units.UnitId;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ChestWearItem extends EquipmentItem {
   public ChestWearItem(ForgeMod mod, UnitConfig config, String name, CreativeTabs tab, int durability, UnitId repairItem, SoundEvent equipSound) {
      super(mod, config, name, tab, durability, repairItem, EntityEquipmentSlot.CHEST, equipSound);
   }

   @SideOnly(Side.CLIENT)
   public void onInitClient() {
      for(Entry<String, RenderPlayer> e : Minecraft.getMinecraft().getRenderManager().getSkinMap().entrySet()) {
         RenderPlayer render = (RenderPlayer)e.getValue();
         ChestWearItem.LayerChestWear layer = new ChestWearItem.LayerChestWear(render.getMainModel().bipedBodyWear);
         render.addLayer(layer);
      }

      super.onInitClient();
   }

   @SideOnly(Side.CLIENT)
   public static class LayerChestWear implements LayerRenderer<EntityLivingBase> {
      final ModelRenderer modelRenderer;

      public LayerChestWear(ModelRenderer modelRenderer) {
         this.modelRenderer = modelRenderer;
      }

      public void doRenderLayer(EntityLivingBase living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
         Minecraft minecraft = Minecraft.getMinecraft();
         ItemStack stack = living.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
         if (stack.getItem() instanceof ChestWearItem) {
            GlStateManager.pushMatrix();
            if (living.isSneaking()) {
               GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            if (living.isChild() && !(living instanceof EntityVillager)) {
               GlStateManager.translate(0.0F, 0.5F * scale, 0.0F);
               GlStateManager.scale(0.7F, 0.7F, 0.7F);
               GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            }

            this.modelRenderer.postRender(0.0625F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.translate(0.0F, -0.25F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.625F, -0.625F, -0.625F);
            minecraft.getItemRenderer().renderItem(living, stack, TransformType.HEAD);
            GlStateManager.popMatrix();
         }

      }

      public boolean shouldCombineTextures() {
         return false;
      }
   }
}
