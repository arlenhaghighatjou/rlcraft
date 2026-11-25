package org.orecruncher.dsurround.client.effects;

import javax.annotation.Nonnull;
import net.minecraft.client.particle.Particle;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@FunctionalInterface
public interface IParticleHelper {
   void addParticle(@Nonnull Particle var1);
}
