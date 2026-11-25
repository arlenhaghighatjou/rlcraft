package org.orecruncher.dsurround.client.fx.particle;

import javax.annotation.Nonnull;
import net.minecraft.client.particle.Particle;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.mote.IParticleMote;

@SideOnly(Side.CLIENT)
public final class ParticleMoteAdapter implements IParticleMote {
   private final Particle particle;

   public ParticleMoteAdapter(@Nonnull Particle particle) {
      this.particle = particle;
   }

   public boolean isAlive() {
      return !this.particle.isAlive();
   }

   public void kill() {
      this.particle.setExpired();
   }

   public void onUpdate() {
      this.particle.onUpdate();
   }

   public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
      this.particle.renderParticle(buffer, entityIn, partialTicks, rotX, rotZ, rotYZ, rotXY, rotXZ);
   }
}
