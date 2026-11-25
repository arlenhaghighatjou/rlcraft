package svenhjol.meson.helper;

import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class SoundHelper {
   public SoundHelper() {
   }

   public static void playSoundAtPos(World world, BlockPos pos, SoundEvent sound, float volume, float pitch) {
      playSoundAtPos(world, pos, sound, SoundCategory.AMBIENT, volume, pitch);
   }

   public static void playSoundAtPos(World world, BlockPos pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
      world.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), sound, category, volume, pitch, false);
   }

   public static void playerSound(EntityPlayer player, SoundEvent sound, float volume, float pitch) {
      playerSound(player, sound, volume, pitch, SoundCategory.AMBIENT);
   }

   public static void playerSound(EntityPlayer player, SoundEvent sound, float volume, float pitch, SoundCategory category) {
      if (category == null) {
         category = SoundCategory.AMBIENT;
      }

      player.world.playSound(player, player.posX, player.posY, player.posZ, sound, category, volume, pitch);
   }

   public static void playerSound(EntityPlayer player, SoundEvent sound, float volume, float pitch, float pitchVariation, SoundCategory category) {
      pitch = pitch + (pitchVariation - pitchVariation * (new EaglercraftRandom()).nextFloat() * 2.0F);
      playerSound(player, sound, volume, pitch, category);
   }

   public static Map<String, ISound> getPlayingSounds() {
      return (Map)ReflectionHelper.getPrivateValue(SoundManager.class, getSoundManager(), ObfuscationHelper.Fields.PLAYING_SOUNDS);
   }

   public static SoundManager getSoundManager() {
      return (SoundManager)ReflectionHelper.getPrivateValue(SoundHandler.class, Minecraft.getMinecraft().getSoundHandler(), ObfuscationHelper.Fields.SNDMANAGER);
   }
}
