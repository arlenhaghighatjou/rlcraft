package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.model.ModelItemBase;
import com.lycanitesmobs.client.model.ModelObjPart;
import com.lycanitesmobs.client.renderer.layer.LayerItem;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentPart;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

public class EquipmentPartRenderer extends TileEntitySpecialRenderer<TileEntityEquipmentPart> implements IItemModelRenderer {
   protected List<LayerItem> renderLayers = new ArrayList();

   public EquipmentPartRenderer() {
   }

   public void render(TileEntityEquipmentPart tileEntityEquipmentPart, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      ItemStack itemStack = ItemEquipmentPart.ITEMSTACK_TO_RENDER;
      ItemEquipmentPart.ITEMSTACK_TO_RENDER = null;
      if (itemStack.getItem() instanceof ItemEquipmentPart) {
         EnumHand hand = null;
         ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();
         ModelItemBase modelItemBase = AssetManager.getItemModel(itemEquipmentPart.itemName);
         this.renderLayers.clear();
         modelItemBase.addCustomLayers(this);
         float loop = 0.0F;
         if (Minecraft.getMinecraft().player != null) {
            loop = (float)Minecraft.getMinecraft().player.ticksExisted;
         }

         GlStateManager.translate(0.5F, 0.35F, 0.5F);
         GlStateManager.rotate(190.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.translate(0.0F, -1.7F, 0.0F);
         if ("head".equalsIgnoreCase(itemEquipmentPart.slotType)) {
            GlStateManager.translate(0.0F, 0.0F, 0.5F);
         } else if ("blade".equalsIgnoreCase(itemEquipmentPart.slotType) || "pike".equalsIgnoreCase(itemEquipmentPart.slotType) || "axe".equalsIgnoreCase(itemEquipmentPart.slotType) || "jewel".equalsIgnoreCase(itemEquipmentPart.slotType)) {
            GlStateManager.translate(0.0F, 0.0F, 1.0F);
         }

         GlStateManager.pushMatrix();
         modelItemBase.generateAnimationFrames(itemStack, (LayerItem)null, loop, (ModelObjPart)null);
         modelItemBase.render(itemStack, hand, this, (ModelObjPart)null, (LayerItem)null, loop, false);

         for(LayerItem renderLayer : this.renderLayers) {
            modelItemBase.render(itemStack, hand, this, (ModelObjPart)null, renderLayer, loop, false);
         }

         modelItemBase.clearAnimationFrames();
         GlStateManager.popMatrix();
         if (tileEntityEquipmentPart != null) {
            super.render(tileEntityEquipmentPart, x, y, z, partialTicks, destroyStage, alpha);
         }

      }
   }

   public void bindItemTexture(ResourceLocation location) {
      if (location != null) {
         this.bindTexture(location);
      }
   }

   public List<LayerItem> addLayer(LayerItem renderLayer) {
      if (!this.renderLayers.contains(renderLayer)) {
         this.renderLayers.add(renderLayer);
      }

      return this.renderLayers;
   }
}
