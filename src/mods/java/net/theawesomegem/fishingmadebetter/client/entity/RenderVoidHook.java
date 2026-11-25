package net.theawesomegem.fishingmadebetter.client.entity;

import net.minecraft.client.renderer.entity.RenderFish;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderVoidHook extends RenderFish {
   private static final ResourceLocation PARTICLES_VOIDHOOK = new ResourceLocation("fishingmadebetter:textures/particle/particles_voidhook.png");

   public RenderVoidHook(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected ResourceLocation getEntityTexture(EntityFishHook entity) {
      return PARTICLES_VOIDHOOK;
   }
}
