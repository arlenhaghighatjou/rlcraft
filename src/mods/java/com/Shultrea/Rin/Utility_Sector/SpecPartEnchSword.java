package com.Shultrea.Rin.Utility_Sector;

import java.awt.Color;
import net.minecraft.client.particle.ParticleCrit;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SpecPartEnchSword extends ParticleCrit {
   public SpecPartEnchSword(World world, double x, double y, double z, double velx, double vely, double velz, Color rgb) {
      super(world, x, y, z, velx, vely, velz);
      this.particleScale = 2.75F;
      this.setParticleTextureIndex(66);
      this.setRBGColorF((float)rgb.getRed() / 255.0F, (float)rgb.getGreen() / 255.0F, (float)rgb.getBlue() / 255.0F);
   }
}
