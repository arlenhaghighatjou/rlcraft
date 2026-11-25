package com.alcatrazescapee.notreepunching.client.particle;

import com.alcatrazescapee.notreepunching.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleManager {
   public ParticleManager() {
   }

   public static void generateFirePitFlame(World world, BlockPos pos) {
      if ((double)Util.RNG.nextFloat() < 0.3D) {
         generateFirePitSmoke(world, pos);
      }

      Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleFire(world, (double)pos.getX() + 0.5D + 0.15D * fastGaussian(), (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D + 0.15D * fastGaussian(), 0.0D, 0.008D, 0.0D));
   }

   public static void generateFirePitSmoke(World world, BlockPos pos) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSmoke(world, (double)pos.getX() + 0.5D + 0.1D * fastGaussian(), (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D + 0.1D * fastGaussian(), 0.01D * fastGaussian(), 0.05D, 0.01D * fastGaussian()));
   }

   public static void generateFireStarterSmoke(World world, Vec3d vec) {
      Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSmoke(world, vec.x + 0.05D * fastGaussian(), vec.y, vec.z + 0.05D * fastGaussian(), 0.01D * fastGaussian(), 0.03D, 0.01D * fastGaussian()));
   }

   private static double fastGaussian() {
      return (double)(Util.RNG.nextFloat() - Util.RNG.nextFloat());
   }
}
