package com.charles445.simpledifficulty.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SoundUtil {
   public SoundUtil() {
   }

   public static void commonPlayPlayerSound(EntityPlayer player, SoundEvent sound) {
      player.playSound(sound, 0.5F, 1.0F);
   }

   public static void serverPlayBlockSound(World world, BlockPos pos, SoundEvent sound) {
      if (!world.isRemote) {
         float volume = 0.75F;
         if (sound == SoundEvents.ENTITY_GENERIC_DRINK) {
            volume = 0.5F;
         }

         world.playSound((EntityPlayer)null, pos, sound, SoundCategory.BLOCKS, volume, 1.0F);
      }

   }

   public static void serverPlayBlockSound(World world, BlockPos pos, SoundEvent sound, float volume) {
      if (!world.isRemote) {
         world.playSound((EntityPlayer)null, pos, sound, SoundCategory.BLOCKS, volume, 1.0F);
      }

   }

   public static void serverPlayBlockSound(World world, BlockPos pos, SoundEvent sound, float volume, float pitch) {
      if (!world.isRemote) {
         world.playSound((EntityPlayer)null, pos, sound, SoundCategory.BLOCKS, volume, pitch);
      }

   }
}
