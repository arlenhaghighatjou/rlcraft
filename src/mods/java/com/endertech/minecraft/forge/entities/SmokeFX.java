package com.endertech.minecraft.forge.entities;

import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.world.World;

public class SmokeFX extends ParticleSmokeNormal {
   public SmokeFX(World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float scale) {
      super(world, x, y, z, xSpeed, ySpeed, zSpeed, scale);
   }
}
