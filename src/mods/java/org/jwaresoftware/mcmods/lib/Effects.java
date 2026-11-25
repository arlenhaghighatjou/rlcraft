package org.jwaresoftware.mcmods.lib;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

public final class Effects {
   private static final int[] RAINBOW = new int[]{16711935, 65280, 16766464, 16742144, 16737969, 16711680, 10367436, 4194284, 865008, 12979696, 15732160, 15740941, 15784461, 12185613, 4452365, 913520};

   public static final void play(int eventid, World world, BlockPos at) {
      world.playEvent(eventid, at, 0);
   }

   public static final void play(int eventid, World world, BlockPos at, int data) {
      world.playEvent(eventid, at, data);
   }

   public static final void playThisBlockBreak(World world, BlockPos at, Block block) {
      world.playEvent(SharedGlue.BLOCK_BREAK_SFX(), at, Block.getIdFromBlock(block));
   }

   public static final void playHappySplashed(World world, BlockPos at) {
      play(SharedGlue.BONEMEAL_SPARKLE_SFX(), world, at);
   }

   public static final void playFertilizedSuccessfully(World world, BlockPos at) {
      play(SharedGlue.BONEMEAL_SPARKLE_SFX(), world, at);
   }

   public static final void playUsedHoeSuccessfully(World world, BlockPos at) {
      world.playSound((EntityPlayer)null, at, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   public static final void playPoisonSplashed(World world, BlockPos at) {
      world.playEvent(SharedGlue.POTION_SFX(), at, 32660);
   }

   public static final void playThrowingItemSound(World world, @Nonnull EntityPlayer player, SoundEvent sound, float volume, float pitch) {
      world.playSound((EntityPlayer)null, player.posX, player.posY + 0.5D, player.posZ, sound, SoundCategory.PLAYERS, volume, pitch);
   }

   public static final void playUseItemFailedSound(World world, @Nonnull EntityPlayer player) {
      world.playSound((EntityPlayer)null, player.posX, player.posY + 0.5D, player.posZ, SharedGlue.RANDOM_CLICK_SOUND(), SoundCategory.BLOCKS, 0.9F, player.getRNG().nextFloat() * 0.1F + 0.9F);
   }

   public static final void playUseItemFailedSoundFromServer(World world, @Nonnull EntityPlayer player) {
      world.playEvent(SharedGlue.DISPENSER_FAILED_SFX(), new BlockPos(player.posX, player.posY + 0.5D, player.posZ), 0);
   }

   public static final void playTargetedItemSound(World world, @Nonnull EntityPlayer player) {
      world.playSound((EntityPlayer)null, player.posX, player.posY + 0.5D, player.posZ, SharedGlue.RANDOM_FIZZ_SOUND(), SoundCategory.NEUTRAL, 0.9F, player.getRNG().nextFloat() * 0.1F + 0.9F);
   }

   public static final void playDispenserSound(World world, BlockPos at, boolean successful) {
      world.playEvent(successful ? SharedGlue.DISPENSER_SUCCEEDED_SFX() : SharedGlue.DISPENSER_FAILED_SFX(), at, 0);
   }

   public static final void playExtinguishingHissss(World world, BlockPos at, float volume) {
      world.playSound((EntityPlayer)null, (double)at.getX() + 0.5D, (double)at.getY() + 0.5D, (double)at.getZ() + 0.5D, SharedGlue.RANDOM_FIZZ_SOUND(), SoundCategory.NEUTRAL, volume, world.rand.nextFloat() * 0.1F + 0.9F);
   }

   public static final void playExtinguishingHissssEvent(World world, BlockPos at) {
      play(1009, world, at);
   }

   public static final void playExtinguishedBurning(World world, @Nonnull EntityPlayer player) {
      world.playSound((EntityPlayer)null, player.posX + 0.5D, player.posY + 0.5D, player.posZ + 0.5D, SharedGlue.RANDOM_FIZZ_SOUND(), SoundCategory.PLAYERS, 1.0F, 1.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.4F);
   }

   public static final void playVeryHeavyThingLanded(World world, BlockPos at) {
      play(SharedGlue.ANVIL_LANDED_SFX(), world, at);
   }

   public static final void playSound(World world, @Nonnull EntityPlayer player, SoundEvent sound, SoundCategory category, float volume, float pitch) {
      world.playSound((EntityPlayer)null, player.posX, player.posY + 0.5D, player.posZ, sound, category == null ? SoundCategory.NEUTRAL : category, volume, pitch);
   }

   public static final void playSoundFor(World world, @Nonnull EntityPlayer player, SoundEvent sound, float volume, float pitch) {
      world.playSound(player, player.posX, player.posY + 0.5D, player.posZ, sound, SoundCategory.PLAYERS, volume, pitch);
   }

   public static final void playSound(World world, @Nonnull EntityPlayer player, SoundEvent sound, float volume, float pitch) {
      world.playSound((EntityPlayer)null, player.posX, player.posY + 0.5D, player.posZ, sound, SoundCategory.PLAYERS, volume, pitch);
   }

   public static final void playHostileSound(World world, @Nonnull EntityPlayer player, SoundEvent sound, float volume) {
      world.playSound((EntityPlayer)null, player.posX, player.posY + 0.5D, player.posZ, sound, SoundCategory.HOSTILE, volume, world.rand.nextFloat() * 0.1F + 0.9F);
   }

   public static final void playBlockSound(World world, @Nonnull EntityPlayer player, SoundEvent sound, float volume, float pitch) {
      world.playSound((EntityPlayer)null, player.posX, player.posY + 0.5D, player.posZ, sound, SoundCategory.BLOCKS, volume, pitch);
   }

   public static final void playProjectileHitBlock(World world, @Nonnull BlockPos pos, SoundEvent sound) {
      if (sound == null) {
         sound = SoundEvents.BLOCK_STONE_HIT;
      }

      world.playSound((EntityPlayer)null, pos, sound, SoundCategory.AMBIENT, 0.5F, 0.65F);
   }

   public static final void playMagicalInteraction(World world, @Nonnull EntityPlayer player, float volume, float pitch) {
      world.playSound((EntityPlayer)null, player.posX, player.posY + 0.5D, player.posZ, SharedGlue.SPARKLE_SOUND(), SoundCategory.RECORDS, volume, pitch);
   }

   public static final void playFoodConsumed(World world, EntityPlayer player, float volume, float pitch) {
      playSound(world, player, SharedGlue.PLAYER_BURP_SOUND(), volume, pitch);
   }

   public static final void playFoodConsumed(World world, EntityPlayer player) {
      playSound(world, player, SharedGlue.PLAYER_BURP_SOUND(), 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
   }

   public static final void playFeedMeSeymourChomp(World world, EntityPlayer player, BlockPos plant) {
      world.playSound((EntityPlayer)null, (double)plant.getX() + 0.5D, (double)plant.getY() + 0.5D, (double)plant.getZ() + 0.5D, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.NEUTRAL, 0.9F, world.rand.nextFloat() * 0.1F + 0.9F);
   }

   public static final void playHealthConsumed(World world, @Nonnull EntityPlayer player) {
      playSound(world, player, SoundEvents.ENTITY_PLAYER_BREATH, 0.15F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
   }

   public static final void playXpEaten(World world, @Nonnull EntityPlayer player) {
      playSound(world, player, SharedGlue.PLAYER_BURP_SOUND(), 0.15F, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
   }

   public static final void playXpLevelUp(World world, @Nonnull EntityPlayer player, int loudThreshold) {
      world.playSound((EntityPlayer)null, player.posX, player.posY + 0.5D, player.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, player.getSoundCategory(), player.experienceLevel > loudThreshold ? 0.75F : 0.5F, 1.0F);
   }

   public static final void playXpSiphonedFrom(World world, EntityPlayer player, boolean burp) {
      playSound(world, player, burp ? SharedGlue.PLAYER_BURP_SOUND() : SoundEvents.ENTITY_GENERIC_DRINK, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
   }

   public static final void playXpOffLoaded(World world, EntityPlayer player, float volume) {
      playSound(world, player, SharedGlue.XP_INTERACT_SOUND(), volume, 0.5F * ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
   }

   public static final void playBucketOfLiquidScoopedUp(World world, EntityPlayer player, @Nullable Material liquid) {
      playSound(world, player, SharedGlue.LIQUID_SCOOPED_SOUND(liquid == null ? SharedGlue.Material_water : liquid), 1.0F, 1.0F);
   }

   public static final void playBucketOfLiquidPlacedDown(World world, EntityPlayer player, @Nullable Material liquid) {
      playSound(world, player, SharedGlue.LIQUID_PUTDOWN_SOUND(liquid == null ? SharedGlue.Material_water : liquid), 1.0F, 1.0F);
   }

   public static final void playBottleOfWaterScoopedUp(World world, EntityPlayer player) {
      world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
   }

   public static final void playExplosionFizzled(World world, BlockPos at, float volume) {
      world.playSound((EntityPlayer)null, (double)at.getX() + 0.5D, (double)at.getY() + 0.5D, (double)at.getZ() + 0.5D, SoundEvents.ENTITY_FIREWORK_TWINKLE, SoundCategory.BLOCKS, volume, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
   }

   public static final void playPotionLevelUp(World world, @Nonnull EntityPlayer player) {
      world.playSound((EntityPlayer)null, player.posX, player.posY + 0.5D, player.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, player.getSoundCategory(), 1.0F, 1.0F);
   }

   public static final void playBlockBOOM(World world, @Nullable EntityPlayer player, BlockPos at) {
      world.playSound(player, (double)at.getX(), (double)at.getY(), (double)at.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   public static final void playGodsBOOM(World world, @Nullable EntityPlayer player, BlockPos at, boolean fromOnHigh) {
      double x = (double)at.getX() + 0.5D;
      double y = (double)at.getY() + 0.5D;
      double z = (double)at.getZ() + 0.5D;
      if (fromOnHigh) {
         world.playSound((EntityPlayer)null, x, y, z, SoundEvents.ENTITY_LIGHTNING_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + world.rand.nextFloat() * 0.2F);
      }

      world.playSound((EntityPlayer)null, x, y, z, SoundEvents.ENTITY_LIGHTNING_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + world.rand.nextFloat() * 0.2F);
   }

   public static final void playCreepyGroan(World world, EntityPlayer player, boolean wary) {
      playSound(world, player, wary ? SoundEvents.ENTITY_POLAR_BEAR_WARNING : SoundEvents.ENTITY_POLAR_BEAR_AMBIENT, (SoundCategory)null, MathHelper.nextFloat(world.rand, 0.3F, 0.6F), world.rand.nextFloat() * 0.1F + 0.8F);
   }

   public static final void playAmbientGroan(World world, BlockPos at, boolean wary) {
      double x = (double)at.getX() + 0.5D;
      double y = (double)at.getY() + 0.5D;
      double z = (double)at.getZ() + 0.5D;
      world.playSound((EntityPlayer)null, x, y, z, wary ? SoundEvents.ENTITY_POLAR_BEAR_WARNING : SoundEvents.ENTITY_POLAR_BEAR_AMBIENT, SoundCategory.NEUTRAL, MathHelper.nextFloat(world.rand, 0.3F, 0.6F), world.rand.nextFloat() * 0.1F + 0.8F);
   }

   public static final void playLockableUnlocked(World world, BlockPos at, boolean metal) {
      play(metal ? 1037 : 1007, world, at);
   }

   public static final void playLockableLocked(World world, BlockPos at, boolean metal) {
      play(metal ? 1036 : 1013, world, at);
   }

   public static final void playItemPickedUp(World world, EntityPlayer player, float volume) {
      playSound(world, player, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, volume < 0.0F ? 0.2F : volume, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
   }

   public static final void spawnWarningParticle(World world, BlockPos at, Random rand, @Nonnull EnumParticleTypes particleType) {
      if (rand.nextInt(20) == 0) {
         world.spawnParticle(particleType, (double)((float)at.getX() + rand.nextFloat()), (double)((float)at.getY() + 1.0F), (double)((float)at.getZ() + rand.nextFloat()), 0.0D, 0.0D, 0.0D, ArrayUtils.EMPTY_INT_ARRAY);
      }

   }

   public static final void spawnSmallArrayOfParticles(@Nonnull World world, BlockPos at, Random rand, @Nonnull EnumParticleTypes particleType) {
      if (rand == null) {
         rand = world.rand;
      }

      int n = 0;

      while(true) {
         world.spawnParticle(particleType, (double)((float)at.getX() + rand.nextFloat()), (double)((float)at.getY() + rand.nextFloat()), (double)((float)at.getZ() + rand.nextFloat()), 0.0D, 0.0D, 0.0D, ArrayUtils.EMPTY_INT_ARRAY);
         ++n;
         if (n >= 3) {
            break;
         }
      }

   }

   public static final void spawnPinklyParticles(World world, BlockPos at, @Nullable EnumParticleTypes particleType, Random rand) {
      if (rand == null) {
         rand = world.rand;
      }

      if (rand.nextInt(20) == 0) {
         if (particleType == null) {
            particleType = EnumParticleTypes.VILLAGER_HAPPY;
         }

         spawnSmallArrayOfParticles(world, at, rand, particleType);
      }

   }

   public static final void spawnLoveyDoveyParticles(World world, BlockPos at) {
      spawnSmallArrayOfParticles(world, at, world.rand, EnumParticleTypes.HEART);
   }

   public static final void spawnPiOoedParticles(World world, BlockPos at) {
      spawnSmallArrayOfParticles(world, at, world.rand, EnumParticleTypes.VILLAGER_ANGRY);
   }

   public static final void spawnPinklyParticles(World world, BlockPos at) {
      play(SharedGlue.BONEMEAL_SPARKLE_SFX(), world, at);
   }

   public static final void spawnPinklyExplosion(World world, BlockPos at) {
      play(SharedGlue.BONEMEAL_SPARKLE_SFX(), world, at);
      play(2003, world, at);
   }

   public static final void spawnTpParticles(World world, BlockPos at, Random rand) {
      if (rand == null) {
         rand = world.rand;
      }

      for(int i = 0; i < 32; ++i) {
         world.spawnParticle(EnumParticleTypes.PORTAL, (double)at.getX(), (double)at.getY() + rand.nextDouble() * 2.0D, (double)at.getZ(), rand.nextGaussian(), 0.0D, rand.nextGaussian(), ArrayUtils.EMPTY_INT_ARRAY);
      }

   }

   public static final void playTpArrival(World world, BlockPos at) {
      play(SharedGlue.PORTAL_TRAVEL_SFX(), world, at);
   }

   public static final void spawnParticles(@Nonnull World world, double atX, double atY, double atZ, @Nullable Random rand, @Nullable EnumParticleTypes particleType, int count, boolean still) {
      if (particleType == null) {
         particleType = EnumParticleTypes.VILLAGER_HAPPY;
      }

      if (rand == null) {
         rand = world.rand;
      }

      if (count <= 0) {
         count = 20;
      }

      for(int i = 0; i < count; ++i) {
         double speedx = still ? 0.0D : rand.nextGaussian() * 0.02D;
         double speedy = still ? 0.0D : rand.nextGaussian() * 0.02D;
         double speedz = still ? 0.0D : rand.nextGaussian() * 0.02D;
         world.spawnParticle(particleType, atX + (double)(rand.nextFloat() * 2.0F) - 1.0D - speedx * 10.0D, atY + (double)(rand.nextFloat() * 2.0F) - speedy * 10.0D, atZ + (double)(rand.nextFloat() * 2.0F) - 1.0D - speedz * 10.0D, speedx, speedy, speedz, ArrayUtils.EMPTY_INT_ARRAY);
      }

   }

   public static final void spawnVisualBOOM(@Nonnull World world, double atX, double atY, double atZ, @Nullable Random rand, @Nullable EnumParticleTypes particleType) {
      spawnParticles(world, atX, atY, atZ, rand, particleType, -1, false);
   }

   public static final void spawnVisualWhack(@Nonnull World world, @Nonnull Entity at, @Nullable Random rand, @Nullable EnumParticleTypes particleType) {
      spawnParticles(world, at.posX, at.posY + 0.5D, at.posZ, rand, particleType, -1, false);
   }

   public static final void spawnSmallFlamesAround(@Nonnull World world, @Nonnull Entity at, boolean flat) {
      double y = at.posY + 0.5D;
      if (flat) {
         world.spawnParticle(EnumParticleTypes.FLAME, at.posX + 1.0D, y, at.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
         world.spawnParticle(EnumParticleTypes.FLAME, at.posX - 1.0D, y, at.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
         world.spawnParticle(EnumParticleTypes.FLAME, at.posX, y, at.posZ + 1.0D, 0.0D, 0.0D, 0.0D, new int[0]);
         world.spawnParticle(EnumParticleTypes.FLAME, at.posX, y, at.posZ - 1.0D, 0.0D, 0.0D, 0.0D, new int[0]);
      } else {
         spawnParticles(world, at.posX + 1.0D, y, at.posZ, (Random)null, EnumParticleTypes.FLAME, 1, true);
         spawnParticles(world, at.posX - 1.0D, y, at.posZ, (Random)null, EnumParticleTypes.FLAME, 1, true);
         spawnParticles(world, at.posX, y, at.posZ + 1.0D, (Random)null, EnumParticleTypes.FLAME, 1, true);
         spawnParticles(world, at.posX, y, at.posZ - 1.0D, (Random)null, EnumParticleTypes.FLAME, 1, true);
      }

   }

   public static final void spawnRainbowParticlesAround(@Nonnull World world, BlockPos pos, @Nullable Random rand) {
      if (rand == null) {
         rand = world.rand;
      }

      if (rand.nextInt(20) == 0) {
         int n = 0;

         while(true) {
            EnumFacing face = EnumFacing.random(world.rand);
            BlockPos neighborPos = pos.offset(face);
            IBlockState neighborState = world.getBlockState(neighborPos);
            if (!neighborState.isFullCube()) {
               double xd = face.getFrontOffsetX() == 0 ? (double)rand.nextFloat() : (face.getFrontOffsetX() < 0 ? -0.05D : 1.05D);
               double yd = face.getFrontOffsetY() == 0 ? (double)rand.nextFloat() : (face.getFrontOffsetY() < 0 ? -0.05D : 1.05D);
               double zd = face.getFrontOffsetZ() == 0 ? (double)rand.nextFloat() : (face.getFrontOffsetZ() < 0 ? -0.05D : 1.05D);
               double x = (double)pos.getX() + xd;
               double y = (double)pos.getY() + yd;
               double z = (double)pos.getZ() + zd;
               int rgb = RAINBOW[rand.nextInt(RAINBOW.length)];
               world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, (double)(rgb >> 16 & 255) / 255.0D, (double)(rgb >> 8 & 255) / 255.0D, (double)(rgb & 255) / 255.0D, new int[0]);
            }

            ++n;
            if (n >= 3) {
               break;
            }
         }
      }

   }

   public static boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return slotChanged || !oldStack.isItemEqual(newStack) || oldStack.hasTagCompound() && newStack.hasTagCompound() && !oldStack.getTagCompound().equals(newStack.getTagCompound());
   }

   private Effects() {
   }
}
