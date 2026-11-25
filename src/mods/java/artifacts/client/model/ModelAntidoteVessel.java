package artifacts.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ModelAntidoteVessel extends ModelBase {
   public ModelRenderer belt;
   public ModelRenderer jar;
   public ModelRenderer lid;

   public ModelAntidoteVessel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.belt = new ModelRenderer(this, 0, 0);
      this.belt.addBox(-4.0F, -1.0F, -2.0F, 8, 12, 4);
      this.jar = new ModelRenderer(this, 0, 16);
      this.jar.addBox(2.0F, 0.0F, 2.0F, 4, 6, 4);
      this.lid = new ModelRenderer(this, 0, 26);
      this.lid.addBox(3.0F, -1.0F, 3.0F, 2, 1, 2);
   }

   public void render(Entity entity, float partialticks, float f1, float f2, float f3, float f4, float scale) {
      boolean hasPants = !((EntityPlayer)entity).getItemStackFromSlot(EntityEquipmentSlot.LEGS).isEmpty();
      GlStateManager.scale(1.1666666F, 1.1666666F, 1.1666666F);
      GlStateManager.scale(1.0F, 1.0F, hasPants ? 1.2F : 1.1F);
      this.belt.render(scale);
      GlStateManager.scale(1.0F, 1.0F, hasPants ? 0.8333333F : 0.9090909F);
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      GlStateManager.translate(0.0F, 1.0F, -0.6666667F);
      GlStateManager.translate(0.2F, 0.0F, 0.0F);
      GlStateManager.rotate(-15.0F, 0.0F, 1.0F, 0.0F);
      this.jar.render(scale);
      this.lid.render(scale);
   }
}
