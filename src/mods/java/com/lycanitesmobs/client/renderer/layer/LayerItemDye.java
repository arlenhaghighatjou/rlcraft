package com.lycanitesmobs.client.renderer.layer;

import net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums;
import com.lycanitesmobs.client.renderer.IItemModelRenderer;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import javax.vecmath.Vector4f;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerItemDye extends LayerItem {
   public LayerItemDye(IItemModelRenderer renderer, String name) {
      super(renderer, name);
   }

   public Vector4f getPartColor(String partName, ItemStack itemStack, float loop) {
      if (!(itemStack.getItem() instanceof ItemEquipmentPart)) {
         return super.getPartColor(partName, itemStack, loop);
      } else {
         ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)itemStack.getItem();
         return itemEquipmentPart.getColor(itemStack);
      }
   }

   public void onRenderStart(ItemStack itemStack) {
      GlStateManager.blendFunc(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA);
   }

   public void onRenderFinish(ItemStack itemStack) {
   }
}
