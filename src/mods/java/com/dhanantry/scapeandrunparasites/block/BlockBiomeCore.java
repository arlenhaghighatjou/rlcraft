package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNodeCore;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockBiomeCore extends BlockBase {
   public static final PropertyInteger ACTIVE = PropertyInteger.create("active", 0, 3);

   public BlockBiomeCore(Material material, String name, float hardness, boolean creative, boolean tickRandom, float resistance) {
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
            ParasiteEventWorld.removeHeartInWorld(world, pos);
         }

         return flag;
      }
   }

   public void onBlockExploded(World worldIn, BlockPos pos, Explosion explosionIn) {
      if (!worldIn.isRemote) {
         ParasiteEventWorld.removeHeartInWorld(worldIn, pos);
         super.onBlockExploded(worldIn, pos, explosionIn);
      }
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!worldIn.isRemote) {
         if (worldIn.isAreaLoaded(pos, 3)) {
            int meta = this.getMetaFromState(state);
            if (meta > 0) {
               int age = ParasiteEventWorld.getHeartAgePostion(worldIn, pos);
               if (age > meta) {
                  worldIn.setBlockState(pos, this.getStateFromMeta(age));
                  switch(age) {
                  case 2:
                     WorldGenParasiteNodeCore tree2 = new WorldGenParasiteNodeCore(false, 2);
                     tree2.generate(worldIn, new EaglercraftRandom(), pos.up());
                     break;
                  case 3:
                     WorldGenParasiteNodeCore tree3 = new WorldGenParasiteNodeCore(false, 3);
                     tree3.generate(worldIn, new EaglercraftRandom(), pos.up());
                  }
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
