package org.orecruncher.dsurround.client.fx.particle.mote;

import javax.annotation.Nonnull;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.gfx.OpenGlUtil;

@SideOnly(Side.CLIENT)
public class ParticleCollectionRipples extends ParticleCollection {
   public static final ParticleCollection.ICollectionFactory FACTORY = ParticleCollectionRipples::new;

   public ParticleCollectionRipples(@Nonnull World world, @Nonnull ResourceLocation tex) {
      super(world, tex);
   }

   protected void bindTexture(@Nonnull ResourceLocation resource) {
      ResourceLocation res = RippleStyle.get().getTexture();
      super.bindTexture(res);
   }

   protected void preRender() {
      super.preRender();
      GlStateManager.enableDepth();
      OpenGlUtil.setStandardBlend();
      GlStateManager.depthMask(false);
   }
}
