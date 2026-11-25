package org.orecruncher.dsurround.client.fx.particle;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public abstract class ParticleBase extends Particle {
   protected final RenderManager manager = Minecraft.getMinecraft().getRenderManager();
   protected final FontRenderer font;

   protected ParticleBase(@Nonnull World worldIn, double posXIn, double posYIn, double posZIn) {
      super(worldIn, posXIn, posYIn, posZIn);
      this.font = Minecraft.getMinecraft().fontRenderer;
      this.rand = XorShiftRandom.current();
   }

   public ParticleBase(@Nonnull World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.font = Minecraft.getMinecraft().fontRenderer;
      this.rand = XorShiftRandom.current();
   }

   protected double interpX() {
      return this.manager.viewerPosX;
   }

   protected double interpY() {
      return this.manager.viewerPosY;
   }

   protected double interpZ() {
      return this.manager.viewerPosZ;
   }

   protected void bindTexture(@Nonnull ResourceLocation resource) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
   }

   protected boolean isThirdPersonView() {
      GameSettings settings = this.manager.options;
      return settings != null && settings.thirdPersonView == 2;
   }
}
