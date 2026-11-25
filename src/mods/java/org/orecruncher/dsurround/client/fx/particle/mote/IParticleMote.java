package org.orecruncher.dsurround.client.fx.particle.mote;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IParticleMote {
   boolean isAlive();

   void kill();

   void onUpdate();

   void renderParticle(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8);
}
