package org.orecruncher.dsurround.client.fx.particle.mote;

import javax.annotation.Nonnull;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.gfx.OpenGlUtil;

@SideOnly(Side.CLIENT)
public class ParticleCollectionFireFly extends ParticleCollection {
   public static final ParticleCollection.ICollectionFactory FACTORY = (world, texture) -> {
      return new ParticleCollectionFireFly(world, texture);
   };

   public ParticleCollectionFireFly(@Nonnull World world, @Nonnull ResourceLocation tex) {
      super(world, tex);
   }

   protected void preRender() {
      super.preRender();
      GlStateManager.depthMask(false);
      OpenGlUtil.setStandardBlend();
   }
}
