package net.theawesomegem.fishingmadebetter.client.entity;

import net.minecraft.client.renderer.entity.RenderFish;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLavaHook extends RenderFish {
   private static final ResourceLocation PARTICLES_LAVAHOOK = new ResourceLocation("fishingmadebetter:textures/particle/particles_lavahook.png");

   public RenderLavaHook(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected ResourceLocation getEntityTexture(EntityFishHook entity) {
      return PARTICLES_LAVAHOOK;
   }
}
