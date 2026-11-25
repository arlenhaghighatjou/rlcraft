package com.lycanitesmobs;

import com.lycanitesmobs.core.config.ConfigBase;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public class WorldEventListener implements IWorldEventListener {
   protected GameEventListener gameEventListener;
   protected List<Entity> customBlockProtectionEntities = new ArrayList();
   protected String[] customProtectionEntityEntries;

   public WorldEventListener(GameEventListener gameEventListener) {
      this.gameEventListener = gameEventListener;
   }

   public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
   }

   public void notifyLightSet(BlockPos pos) {
   }

   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
   }

   public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
   }

   public void playRecord(SoundEvent soundIn, BlockPos pos) {
   }

   public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
   }

   public void spawnParticle(int id, boolean ignoreRange, boolean p_190570_3_, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
   }

   public void onEntityAdded(Entity entity) {
      if (this.customProtectionEntityEntries == null) {
         ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
         this.customProtectionEntityEntries = config.getStringList("Block Protection", "Custom Entity Block Protection", new String[0], "A list of entities to prevent players from placing or destroying block near, takes a list of entity ids.");
      }

      if (this.customProtectionEntityEntries.length > 0) {
         ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(entity.getEntityWorld());
         if (extendedWorld != null) {
            ResourceLocation entityResourceLocation = EntityList.getKey(entity);
            if (entityResourceLocation != null) {
               String entityId = entityResourceLocation.toString();

               for(String customProtectedEntityEntry : this.customProtectionEntityEntries) {
                  String[] customProtectedEntityEntryParts = customProtectedEntityEntry.split(";");
                  if (customProtectedEntityEntryParts.length >= 2) {
                     String customProtectedEntityId = customProtectedEntityEntryParts[0];
                     int customProtectedEntityRange = Integer.parseInt(customProtectedEntityEntryParts[1]);
                     if (customProtectedEntityId.equals(entityId)) {
                        extendedWorld.bossUpdate(entity);
                        extendedWorld.overrideBossRange(entity, customProtectedEntityRange);
                        this.customBlockProtectionEntities.add(entity);
                        break;
                     }
                  }
               }
            }
         }
      }

   }

   public void onEntityRemoved(Entity entity) {
   }

   public void broadcastSound(int soundID, BlockPos pos, int data) {
   }

   public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
   }

   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
   }
}
