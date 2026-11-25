package artifacts.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ModelObsidianSkull extends ModelBase {
   public ModelRenderer belt;
   public ModelRenderer skull1;
   public ModelRenderer skull2;
   public ModelRenderer skull3;
   public ModelRenderer skull4;
   public ModelRenderer skull5;
   public ModelRenderer skull6;
   public ModelRenderer skull7;
   public ModelRenderer skull8;
   public ModelRenderer skull9;

   public ModelObsidianSkull() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.belt = new ModelRenderer(this, 0, 0);
      this.skull1 = new ModelRenderer(this, 0, 16);
      this.skull2 = new ModelRenderer(this, 0, 28);
      this.skull3 = new ModelRenderer(this, 24, 0);
      this.skull4 = new ModelRenderer(this, 24, 3);
      this.skull5 = new ModelRenderer(this, 24, 6);
      this.skull6 = new ModelRenderer(this, 16, 28);
      this.skull7 = new ModelRenderer(this, 24, 9);
      this.skull8 = new ModelRenderer(this, 24, 13);
      this.skull9 = new ModelRenderer(this, 24, 17);
      this.belt.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4);
      this.skull1.addBox(0.0F, 0.0F, 1.0F, 7, 6, 6);
      this.skull2.addBox(0.0F, 0.0F, 0.0F, 7, 2, 1);
      this.skull3.addBox(0.0F, 2.0F, 0.0F, 1, 2, 1);
      this.skull4.addBox(3.0F, 2.0F, 0.0F, 1, 2, 1);
      this.skull5.addBox(6.0F, 2.0F, 0.0F, 1, 2, 1);
      this.skull6.addBox(0.0F, 4.0F, 0.0F, 7, 2, 1);
      this.skull7.addBox(1.0F, 6.0F, 0.0F, 1, 1, 3);
      this.skull8.addBox(3.0F, 6.0F, 0.0F, 1, 1, 3);
      this.skull9.addBox(5.0F, 6.0F, 0.0F, 1, 1, 3);
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
      boolean hasPants = !((EntityPlayer)entity).getItemStackFromSlot(EntityEquipmentSlot.LEGS).isEmpty();
      GlStateManager.scale(1.1666666F, 1.1666666F, 1.1666666F);
      GlStateManager.scale(1.0F, 1.0F, hasPants ? 1.2F : 1.1F);
      this.belt.render(scale);
      GlStateManager.scale(1.0F, 1.0F, hasPants ? 0.8333333F : 0.9090909F);
      GlStateManager.scale(0.5F, 0.5F, 0.5F);
      GlStateManager.translate(0.0F, 1.0F, -0.5F);
      GlStateManager.translate(0.2F, 0.0F, 0.0F);
      GlStateManager.rotate(-25.0F, 0.0F, 1.0F, 0.0F);
      this.skull1.render(scale);
      this.skull2.render(scale);
      this.skull3.render(scale);
      this.skull4.render(scale);
      this.skull5.render(scale);
      this.skull6.render(scale);
      this.skull7.render(scale);
      this.skull8.render(scale);
      this.skull9.render(scale);
   }
}
