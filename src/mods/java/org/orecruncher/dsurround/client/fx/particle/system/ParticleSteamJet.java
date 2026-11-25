package org.orecruncher.dsurround.client.fx.particle.system;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.SteamJetEffect;
import org.orecruncher.dsurround.client.fx.particle.ParticleSteamCloud;
import org.orecruncher.lib.WorldUtils;

@SideOnly(Side.CLIENT)
public class ParticleSteamJet extends ParticleJet {
   public ParticleSteamJet(int strength, World world, double x, double y, double z) {
      super(strength, world, x, y, z);
   }

   public boolean shouldDie() {
      return !SteamJetEffect.isValidSpawnBlock(WorldUtils.getDefaultBlockStateProvider(), this.getPos());
   }

   protected void spawnJetParticle() {
      Particle particle = new ParticleSteamCloud(this.world, this.posX, this.posY, this.posZ, 0.1D);
      this.addParticle(particle);
   }
}
