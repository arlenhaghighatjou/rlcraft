package org.orecruncher.dsurround.client.fx.particle.system;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.gfx.ParticleHelper;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public abstract class ParticleSystem {
   protected static final Random RANDOM = XorShiftRandom.current();
   protected static final GameSettings SETTINGS;
   protected final World world;
   protected final double posX;
   protected final double posY;
   protected final double posZ;
   protected final BlockPos position;
   private boolean isAlive = true;

   protected ParticleSystem(World worldIn, double posXIn, double posYIn, double posZIn) {
      this.world = worldIn;
      this.posX = posXIn;
      this.posY = posYIn;
      this.posZ = posZIn;
      this.position = new BlockPos(posXIn, posYIn, posZIn);
   }

   @Nonnull
   public BlockPos getPos() {
      return this.position;
   }

   public void addParticle(@Nonnull Particle particle) {
      ParticleHelper.addParticle(particle);
   }

   public boolean isAlive() {
      return this.isAlive;
   }

   public void setExpired() {
      this.isAlive = false;
      this.cleanUp();
   }

   public boolean shouldDie() {
      return false;
   }

   protected void cleanUp() {
   }

   public void onUpdate() {
      if (this.shouldDie()) {
         this.setExpired();
      } else {
         this.think();
         if (this.isAlive()) {
            this.soundUpdate();
         }

      }
   }

   protected void soundUpdate() {
   }

   public abstract void think();

   static {
      SETTINGS = Minecraft.getMinecraft().gameSettings;
   }
}
