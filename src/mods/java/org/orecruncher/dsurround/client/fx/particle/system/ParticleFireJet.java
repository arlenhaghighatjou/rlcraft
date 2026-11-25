package org.orecruncher.dsurround.client.fx.particle.system;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleLava.Factory;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.SoundEffectHandler;
import org.orecruncher.dsurround.client.sound.Sounds;
import org.orecruncher.lib.ReflectedField.FloatField;

@SideOnly(Side.CLIENT)
public class ParticleFireJet extends ParticleJet {
   private static final FloatField<ParticleFlame> flameScale = new FloatField(ParticleFlame.class, "flameScale", "flameScale");
   protected final boolean isLava;
   protected final IParticleFactory factory;
   protected final int particleId;
   protected boolean soundFired;

   public ParticleFireJet(int strength, World world, double x, double y, double z) {
      super(strength, world, x, y, z);
      this.isLava = RANDOM.nextInt(3) == 0;
      this.particleId = this.isLava ? EnumParticleTypes.LAVA.getParticleID() : EnumParticleTypes.FLAME.getParticleID();
      if (this.isLava) {
         this.factory = new Factory();
      } else {
         this.factory = new net.minecraft.client.particle.ParticleFlame.Factory();
      }

   }

   protected void soundUpdate() {
      if (!this.soundFired) {
         this.soundFired = true;
         SoundEffectHandler.INSTANCE.playSoundAt(this.getPos(), Sounds.FIRE, 0);
      }

   }

   protected void spawnJetParticle() {
      double speedY = this.isLava ? 0.0D : (double)this.jetStrength / 10.0D;
      Particle particle = this.factory.createParticle(this.particleId, this.world, this.posX, this.posY, this.posZ, 0.0D, speedY, 0.0D, new int[0]);
      if (particle != null) {
         if (!this.isLava) {
            ParticleFlame flame = (ParticleFlame)particle;
            flameScale.set(flame, flameScale.get(flame) * (float)this.jetStrength);
         }

         this.addParticle(particle);
      }

   }
}
