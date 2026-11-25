package artifacts.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ModelBubbleWrap extends ModelBase {
   public ModelRenderer belt;

   public ModelBubbleWrap() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.belt = new ModelRenderer(this, 0, 0);
      this.belt.addBox(-5.0F, 3.0F, -3.0F, 10, 8, 6);
   }

   public void render(Entity entity, float partialticks, float f1, float f2, float f3, float f4, float scale) {
      boolean hasPants = !((EntityPlayer)entity).getItemStackFromSlot(EntityEquipmentSlot.LEGS).isEmpty();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.scale(1.1666666F, 1.1666666F, 1.1666666F);
      GlStateManager.scale(1.0F, 1.0F, hasPants ? 1.2F : 1.1F);
      GlStateManager.pushMatrix();
      this.belt.render(scale);
      GlStateManager.popMatrix();
      GlStateManager.disableBlend();
   }
}
