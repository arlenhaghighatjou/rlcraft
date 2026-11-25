package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockColonyCore extends BlockBase {
   public static final PropertyInteger ACTIVE = PropertyInteger.create("active", 0, 3);

   public BlockColonyCore(Material material, String name, float hardness, boolean creative, boolean tickRandom, float resistance) {
      super(material, name, hardness, creative, tickRandom, resistance);
      this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, Integer.valueOf(0)));
   }

   public EnumPushReaction getMobilityFlag(IBlockState state) {
      return EnumPushReaction.BLOCK;
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      boolean flag = super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
      if (worldIn.isRemote) {
         return flag;
      } else {
         int meta = this.getMetaFromState(state);
         System.out.println("meta " + meta);
         System.out.println(" ");
         return flag;
      }
   }

   public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
      boolean flag = super.removedByPlayer(state, world, pos, player, willHarvest);
      if (world.isRemote) {
         return flag;
      } else {
         if (this.getMetaFromState(state) > 0) {
            ParasiteEventWorld.removeColonyInWorld(world, pos);
         }

         return flag;
      }
   }

   public void onBlockExploded(World worldIn, BlockPos pos, Explosion explosionIn) {
      if (!worldIn.isRemote) {
         ParasiteEventWorld.removeColonyInWorld(worldIn, pos);
         super.onBlockExploded(worldIn, pos, explosionIn);
      }
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         if (worldIn.isAreaLoaded(pos, 3)) {
            int meta = this.getMetaFromState(state);
            if (meta > 0) {
               AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).grow(20.0D, 5.0D, 20.0D);
               List<EntityKol> moblist = worldIn.getEntitiesWithinAABB(EntityKol.class, axisalignedbb);
               if (moblist.isEmpty()) {
                  ParasiteEventEntity.spawnFromBlock(worldIn, new String[]{"srparasites:worker;1"}, 5, pos.up());
                  ParasiteEventEntity.spawnFromBlock(worldIn, new String[]{"srparasites:worker;1"}, 5, pos.up());
               }
            }

         }
      }
   }

   protected BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{ACTIVE});
   }

   public int getMetaFromState(IBlockState state) {
      return ((Integer)state.getValue(ACTIVE)).intValue();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(ACTIVE, Integer.valueOf(meta & 3));
   }
}
