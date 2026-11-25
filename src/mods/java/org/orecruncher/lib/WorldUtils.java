package org.orecruncher.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.chunk.IBlockAccessEx;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public final class WorldUtils {
   private WorldUtils() {
   }

   @Nonnull
   public static IBlockAccessEx getDefaultBlockStateProvider() {
      return ClientChunkCache.instance();
   }

   @Nullable
   public static Entity locateEntity(@Nonnull World world, int entityId) {
      Entity entity = null;
      if (world != null) {
         try {
            entity = world.getEntityByID(entityId);
         } catch (Throwable var4) {
            ;
         }
      }

      return entity;
   }

   public static boolean isSolidBlock(@Nonnull World world, @Nonnull BlockPos pos) {
      return getBlockState(world, pos).getMaterial().isSolid();
   }

   public static boolean isAirBlock(@Nonnull IBlockState state) {
      return state.getBlock() == Blocks.AIR;
   }

   public static boolean isLeaves(@Nonnull IBlockState state) {
      return state.getMaterial() == Material.LEAVES;
   }

   public static boolean isAirBlock(@Nonnull World world, @Nonnull BlockPos pos) {
      return isAirBlock(getBlockState(world, pos));
   }

   @Nonnull
   public static IBlockState getBlockState(@Nonnull World world, @Nonnull BlockPos pos) {
      return getDefaultBlockStateProvider().getBlockState(pos);
   }

   @Nonnull
   public static IBlockState getBlockState(@Nonnull World world, int x, int y, int z) {
      return getDefaultBlockStateProvider().getBlockState(x, y, z);
   }

   public static boolean isFullWaterBlock(@Nonnull World world, @Nonnull BlockPos pos) {
      return isFullWaterBlock(getDefaultBlockStateProvider().getBlockState(pos));
   }

   public static boolean isFullWaterBlock(@Nonnull IBlockState state) {
      return state.getMaterial() == Material.WATER && state.getBlock().getDefaultState() == state;
   }

   public static boolean hasVoidPartiles(@Nonnull World world) {
      return world.getWorldType() != WorldType.FLAT && world.provider.hasSkyLight();
   }

   @Nonnull
   public static ObjectArray<Entity> gatherEntitiesInView(@Nonnull Entity viewer, int rangeSq, float partialTicks) {
      ObjectArray<Entity> result = new ObjectArray<Entity>();
      if (Minecraft.isGuiEnabled()) {
         Frustum frustum = new Frustum();
         double viewX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * (double)partialTicks;
         double viewY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * (double)partialTicks;
         double viewZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * (double)partialTicks;
         frustum.setPosition(viewX, viewY, viewZ);

         for(Entity entity : viewer.getEntityWorld().getLoadedEntityList()) {
            if (entity != null && entity.isEntityAlive() && viewer.getDistanceSq(entity) <= (double)rangeSq && (entity.ignoreFrustumCheck || frustum.isBoundingBoxInFrustum(entity.getEntityBoundingBox()))) {
               result.add(entity);
            }
         }
      }

      return result;
   }
}
