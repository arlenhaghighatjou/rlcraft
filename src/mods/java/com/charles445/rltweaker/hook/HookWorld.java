package com.charles445.rltweaker.hook;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CollisionUtil;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.WorldRadiusUtil;
import com.google.common.base.Predicate;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class HookWorld {
   public static boolean warnItemHook = false;
   public static boolean warnSearchHook = false;
   public static boolean chunkTickPatchEnabled = false;
   public static HookWorld.IChunkTickPost sereneSeasonsPost = null;

   public HookWorld() {
   }

   public static List<Entity> getEntitiesWithinAABBExcludingEntity(World world, @Nullable Entity entity, AxisAlignedBB bb) {
      if (!ModConfig.server.minecraft.lessCollisions) {
         return world.getEntitiesWithinAABBExcludingEntity(entity, bb);
      } else {
         if (entity != null) {
            try {
               return WorldRadiusUtil.instance.getEntitiesWithinAABBExcludingEntity(world, entity, bb, CollisionUtil.instance.getRadiusForEntity(entity));
            } catch (Exception var4) {
               if (!warnItemHook) {
                  warnItemHook = true;
                  RLTweaker.logger.error("Error running CollisionUtil!", var4);
                  ErrorUtil.logSilent("CollisionUtil Critical Failure");
               }
            }
         }

         return world.getEntitiesWithinAABBExcludingEntity(entity, bb);
      }
   }

   public static List<Entity> getEntitiesInAABBexcluding(World world, @Nullable Entity entity, AxisAlignedBB bb, @Nullable Predicate<? super Entity> predicate) {
      if (!ModConfig.server.minecraft.lessCollisions) {
         return world.getEntitiesInAABBexcluding(entity, bb, predicate);
      } else {
         if (entity != null) {
            try {
               return WorldRadiusUtil.instance.getEntitiesInAABBexcluding(world, entity, bb, predicate, CollisionUtil.instance.getRadiusForEntity(entity));
            } catch (Exception var5) {
               if (!warnItemHook) {
                  warnItemHook = true;
                  RLTweaker.logger.error("Error running CollisionUtil!", var5);
                  ErrorUtil.logSilent("CollisionUtil Critical Failure");
               }
            }
         }

         return world.getEntitiesInAABBexcluding(entity, bb, predicate);
      }
   }

   public static List<Entity> getEntitiesWithinAABB(World world, Class<Entity> clazz, AxisAlignedBB bb, @Nullable Predicate<? super Entity> predicate) {
      try {
         if (clazz.equals(EntityItem.class) || clazz.equals(EntityPlayer.class)) {
            return WorldRadiusUtil.instance.<Entity>getEntitiesWithinAABB(world, clazz, bb, predicate, 2.0D);
         }
      } catch (Exception var5) {
         if (!warnSearchHook) {
            warnSearchHook = true;
            RLTweaker.logger.error("Error running ReducedSearchSize!", var5);
            ErrorUtil.logSilent("ReducedSearchSize Critical Failure");
         }
      }

      return world.getEntitiesWithinAABB(clazz, bb, predicate);
   }

   public static HookWorld.ChunkTickContainer onPreUpdateBlocks(WorldServer world) {
      if (!chunkTickPatchEnabled) {
         chunkTickPatchEnabled = true;
      }

      return new HookWorld.ChunkTickContainer(world);
   }

   public static void postBlockTickChunk(Chunk chunk, HookWorld.ChunkTickContainer container) {
      if (container.sereneSeasonsCompanionPost != null) {
         sereneSeasonsPost.invoke(chunk, container.sereneSeasonsCompanionPost);
      }

   }

   public static class ChunkTickContainer {
      public Object sereneSeasonsCompanionPost;

      public ChunkTickContainer(WorldServer world) {
         this.sereneSeasonsCompanionPost = HookWorld.sereneSeasonsPost == null ? null : HookWorld.sereneSeasonsPost.preUpdate(world);
      }
   }

   public interface IChunkTickPost<T> {
      @Nullable
      T preUpdate(WorldServer var1);

      void invoke(Chunk var1, T var2);
   }
}
