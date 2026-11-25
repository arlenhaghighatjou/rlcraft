package com.charles445.rltweaker.hook;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.debug.DebugUtil;
import com.charles445.rltweaker.util.ErrorUtil;
import java.util.HashSet;
import java.util.Set;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

public class HookMinecraft {
   public static Set<EaglercraftUUID> rotationErrors = new HashSet();
   public static Set<EaglercraftUUID> motionErrors = new HashSet();

   public HookMinecraft() {
   }

   public static <E> ConcurrentLinkedDeque<E> newConcurrentLinkedDeque() {
      return new ConcurrentLinkedDeque();
   }

   public static PathNodeType verifyPathNodeType(PathNodeType type, IBlockAccess access, int x, int y, int z) {
      boolean isBaseWood = type == PathNodeType.DOOR_WOOD_CLOSED;
      boolean isBaseIron = type == PathNodeType.DOOR_IRON_CLOSED;
      if (isBaseWood || isBaseIron) {
         BlockPos basePos = new BlockPos(x, y, z);
         IBlockState baseState = access.getBlockState(basePos);
         Block baseBlock = baseState.getBlock();
         if (baseBlock instanceof BlockDoor && baseState.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER) {
            Material refMaterial = isBaseWood ? Material.WOOD : Material.IRON;
            BlockPos downPos = basePos.down();
            IBlockState downState = access.getBlockState(downPos);
            Block downBlock = downState.getBlock();
            Material downMaterial = downState.getMaterial();
            if (downBlock instanceof BlockDoor && downMaterial == refMaterial && ((Boolean)downState.getValue(BlockDoor.OPEN)).booleanValue()) {
               return PathNodeType.DOOR_OPEN;
            }
         }
      }

      return type;
   }

   public static void playLimitedBroadcastSound(PlayerList playerList, SPacketEffect packet, BlockPos pos) {
      double maxDist = ModConfig.server.minecraft.broadcastedSoundsDistanceLimit;
      maxDist = maxDist * maxDist;

      for(EntityPlayerMP player : playerList.getPlayers()) {
         if (player.getDistanceSq(pos) < maxDist) {
            player.connection.sendPacket(packet);
         }
      }

   }

   public static void aggressiveMotionCheck(Entity entity) {
      if (ModConfig.server.minecraft.motionChecker) {
         if (entity != null && !entity.world.isRemote) {
            if (testMotion(entity.motionX, entity)) {
               entity.motionX = 0.0D;
               entity.velocityChanged = true;
            }

            if (testMotion(entity.motionY, entity)) {
               entity.motionY = 0.0D;
               entity.velocityChanged = true;
            }

            if (testMotion(entity.motionZ, entity)) {
               entity.motionZ = 0.0D;
               entity.velocityChanged = true;
            }

            if (testAngle(entity.rotationPitch, entity)) {
               entity.rotationPitch = 0.0F;
               entity.rotationYaw = 0.0F;
            } else if (testAngle(entity.rotationYaw, entity)) {
               entity.rotationPitch = 0.0F;
               entity.rotationYaw = 0.0F;
            }

         }
      }
   }

   private static boolean testAngle(float angle, Entity entity) {
      if (!Float.isFinite(angle)) {
         if (!rotationErrors.contains(entity.getUniqueID())) {
            rotationErrors.add(entity.getUniqueID());
            RLTweaker.logger.error("Entity has bad rotation! (aggressive) " + angle + " " + dumpEntity(entity));
            ErrorUtil.logSilent("Motion Checker Angle (aggressive)");
            if (ModConfig.server.minecraft.debug) {
               DebugUtil.messageAll("Entity has bad angles! (aggressive) " + angle);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean testMotion(double motion, Entity entity) {
      if (motion <= ModConfig.server.minecraft.motionCheckerSpeedCap && motion >= -ModConfig.server.minecraft.motionCheckerSpeedCap && Double.isFinite(motion)) {
         return false;
      } else {
         if (!motionErrors.contains(entity.getUniqueID())) {
            motionErrors.add(entity.getUniqueID());
            RLTweaker.logger.error("Entity moving too fast! (aggressive) " + motion + " " + dumpEntity(entity));
            ErrorUtil.logSilent("Motion Checker Speed (aggressive)");
            if (ModConfig.server.minecraft.debug) {
               DebugUtil.messageAll("Entity moving too fast! (aggressive) " + motion);
            }
         }

         return true;
      }
   }

   private static String dumpEntity(Entity entity) {
      StringBuilder sb = new StringBuilder();
      sb.append(entity.getClass().getName());
      sb.append(" - ");
      sb.append(entity.posX);
      sb.append(" ");
      sb.append(entity.posY);
      sb.append(" ");
      sb.append(entity.posZ);
      sb.append(" ");
      sb.append(entity.motionX);
      sb.append(" ");
      sb.append(entity.motionY);
      sb.append(" ");
      sb.append(entity.motionZ);
      if (entity instanceof EntityLivingBase) {
         sb.append(" hurt time:");
         sb.append(((EntityLivingBase)entity).hurtTime);
      }

      sb.append(" dead:");
      sb.append(entity.isDead);
      sb.append(" pitch:");
      sb.append(entity.rotationPitch);
      sb.append(" yaw:");
      sb.append(entity.rotationYaw);
      if (entity instanceof EntityLiving) {
         EntityLiving living = (EntityLiving)entity;
         DamageSource damagesource = living.getLastDamageSource();
         if (damagesource != null) {
            sb.append(" damagesource class:");
            sb.append(damagesource.getClass().getName());
         }

         EntityLivingBase revenge = living.getRevengeTarget();
         if (revenge != null) {
            sb.append(" revenge target class:");
            sb.append(revenge.getClass().getName());
         }

         EntityLivingBase attack = living.getAttackTarget();
         if (attack != null) {
            sb.append(" attack target class:");
            sb.append(attack.getClass().getName());
         }
      }

      return sb.toString();
   }

   public static boolean shouldEntityDestroyBlock(Block block, IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
      String regname = block.getRegistryName().toString();

      for(String protectedBlock : ModConfig.server.minecraft.entityBlockDestroyBlacklist) {
         if (regname.equals(protectedBlock)) {
            return false;
         }
      }

      return true;
   }

   public static void clearItemFrame(EntityItemFrame frame) {
      frame.setDisplayedItem(ItemStack.EMPTY);
   }

   public static EnumPushReaction hookPushReaction(Entity entity) {
      if (entity == null) {
         return EnumPushReaction.NORMAL;
      } else {
         ResourceLocation rl = EntityList.getKey(entity);
         if (rl == null) {
            return EnumPushReaction.NORMAL;
         } else {
            String rls = rl.toString();

            for(String protectedEntity : ModConfig.server.minecraft.entityPushPrevention) {
               if (rls.equals(protectedEntity)) {
                  return EnumPushReaction.IGNORE;
               }
            }

            return EnumPushReaction.NORMAL;
         }
      }
   }

   public static int overlayTextYOffset(int old) {
      return old + ModConfig.client.minecraft.overlayTextOffset;
   }

   public static boolean overlayTextDropShadow() {
      return ModConfig.client.minecraft.overlayTextDropShadow;
   }

   public static void clearAnvilResult(ContainerRepair container, IInventory outputSlot) {
      if (outputSlot != null) {
         outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
      }

      if (container != null) {
         container.maximumCost = 0;
      }

   }

   public static IInventory hopperInventoryAtPosition(World world, BlockPos pos, Block block, IBlockState state, IInventory inventory) {
      if (inventory == null) {
         return null;
      } else {
         String regname = block.getRegistryName().toString();

         for(String protectedBlock : ModConfig.server.minecraft.hopperBlockBlacklist) {
            if (regname.equals(protectedBlock)) {
               return null;
            }
         }

         return inventory;
      }
   }

   public static Pair<IItemHandler, Object> hopperItemHandler(World world, BlockPos pos, Block block, IBlockState state, Pair<IItemHandler, Object> destination) {
      if (destination == null) {
         return null;
      } else {
         String regname = block.getRegistryName().toString();

         for(String protectedBlock : ModConfig.server.minecraft.hopperBlockBlacklist) {
            if (regname.equals(protectedBlock)) {
               return null;
            }
         }

         return destination;
      }
   }

   @Nullable
   public static Chunk cacheGetChunkFromChunkCoords(World world, int chunkX, int chunkZ, ChunkCache chunkCache) {
      if (chunkCache instanceof NullableChunkCache) {
         return world.isBlockLoaded(new BlockPos(chunkX << 4, 64, chunkZ << 4)) ? world.getChunkFromChunkCoords(chunkX, chunkZ) : null;
      } else {
         return world.getChunkFromChunkCoords(chunkX, chunkZ);
      }
   }
}
