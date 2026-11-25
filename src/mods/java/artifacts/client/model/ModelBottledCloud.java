package artifacts.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ModelBottledCloud extends ModelBase {
   public ModelRenderer belt;
   public ModelRenderer jar;
   public ModelRenderer lid;
   public ModelRenderer cloud1;
   public ModelRenderer cloud2;

   public ModelBottledCloud() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.belt = new ModelRenderer(this, 0, 0);
      this.belt.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4);
      this.jar = new ModelRenderer(this, 0, 16);
      this.jar.addBox(0.0F, 0.0F, 0.0F, 7, 9, 7);
      this.lid = new ModelRenderer(this, 44, 0);
      this.lid.addBox(1.0F, -1.0F, 1.0F, 5, 1, 5);
      this.cloud1 = new ModelRenderer(this, 24, 0);
      this.cloud1.addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5);
      this.cloud2 = new ModelRenderer(this, 24, 10);
      this.cloud2.addBox(-1.5F, -1.5F, -1.5F, 3, 3, 3);
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
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.translate(0.21875F, 0.28125F, 0.21875F);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0D, 0.0625D * Math.sin(0.05D * (double)((float)entity.ticksExisted + partialticks)), 0.0D);
      GlStateManager.scale(1.0D + 0.10000000149011612D * Math.sin(0.03D * (double)((float)entity.ticksExisted + partialticks)), 0.800000011920929D + 0.10000000149011612D * Math.sin(0.032D * (double)((float)entity.ticksExisted + partialticks)), 0.800000011920929D + 0.10000000149011612D * Math.sin(0.034D * (double)((float)entity.ticksExisted + partialticks)));
      this.cloud2.render(scale);
      GlStateManager.popMatrix();
      GlStateManager.translate(0.0D, 0.0625D * Math.sin(0.05D * (double)((float)entity.ticksExisted + partialticks) + 0.5D), 0.0D);
      GlStateManager.scale(1.0D + 0.10000000149011612D * Math.sin(0.033D * (double)((float)entity.ticksExisted + partialticks)), 0.800000011920929D + 0.10000000149011612D * Math.sin(0.031D * (double)((float)entity.ticksExisted + partialticks)), 0.800000011920929D + 0.10000000149011612D * Math.sin(0.034D * (double)((float)entity.ticksExisted + partialticks)));
      this.cloud1.render(scale);
      GlStateManager.disableBlend();
   }
}
