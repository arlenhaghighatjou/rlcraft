package de.Whitedraco.switchbow.renderer.living;

import de.Whitedraco.switchbow.entity.living.EntityLittleIronGolem;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLittleIronGolem extends RenderLiving<EntityLittleIronGolem> {
   private static final ResourceLocation IRON_GOLEM_TEXTURES = new ResourceLocation("textures/entity/iron_golem.png");

   public RenderLittleIronGolem(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelLittleIronGolem(), 0.5F);
   }

   protected ResourceLocation getEntityTexture(EntityLittleIronGolem entity) {
      return IRON_GOLEM_TEXTURES;
   }

   protected void applyRotations(EntityLittleIronGolem entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks) {
      super.applyRotations(entityLiving, p_77043_2_, p_77043_3_, partialTicks);
      if ((double)entityLiving.limbSwingAmount >= 0.01D) {
         float f1 = entityLiving.limbSwing - entityLiving.limbSwingAmount * (1.0F - partialTicks) + 6.0F;
         float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         GlStateManager.rotate(6.5F * f2, 0.0F, 0.0F, 1.0F);
      }

   }
}
