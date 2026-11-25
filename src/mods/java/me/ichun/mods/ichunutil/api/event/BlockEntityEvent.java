package me.ichun.mods.ichunutil.api.event;

import java.util.Collection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class BlockEntityEvent extends WorldEvent {
   public BlockEntityEvent(World world) {
      super(world);
   }

   @Cancelable
   public static class Pickup extends BlockEntityEvent {
      private final EntityLivingBase living;
      private final Collection<BlockPos> poses;

      public Pickup(World world, EntityLivingBase living, Collection<BlockPos> poses) {
         super(world);
         this.living = living;
         this.poses = poses;
      }

      public EntityLivingBase getEntityLiving() {
         return this.living;
      }

      public Collection<BlockPos> getPoses() {
         return this.poses;
      }
   }

   @Cancelable
   public static class Place extends BlockEntityEvent {
      private final Entity blockEntity;
      private final EntityLivingBase living;
      private final IBlockState blockState;
      private final BlockPos pos;

      public Place(World world, Entity blockEntity, EntityLivingBase living, IBlockState blockState, BlockPos blockPos) {
         super(world);
         this.blockEntity = blockEntity;
         this.living = living;
         this.blockState = blockState;
         this.pos = blockPos;
      }

      public Entity getBlockEntity() {
         return this.blockEntity;
      }

      public EntityLivingBase getEntityLiving() {
         return this.living;
      }

      public IBlockState getBlockState() {
         return this.blockState;
      }

      public BlockPos getPos() {
         return this.pos;
      }
   }
}
