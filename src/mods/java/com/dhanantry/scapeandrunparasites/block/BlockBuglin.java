package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBuglin extends BlockBase {
   protected static final AxisAlignedBB TALL_GRASS_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);

   public BlockBuglin(String name, float hardness, boolean creative, boolean tickRandom, float resistance) {
      super(Material.SPONGE, name, hardness, creative, tickRandom, resistance);
      this.setSoundType(SoundType.PLANT);
      this.setDefaultState(this.blockState.getBaseState());
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         if (worldIn.isAreaLoaded(pos, 3)) {
            if (worldIn.getDifficulty() != EnumDifficulty.PEACEFUL) {
               if (SRPConfigMobs.lodoEnabled) {
                  AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).grow(1.0D, 1.0D, 1.0D);
                  List<EntityParasiteBase> moblist = worldIn.getEntitiesWithinAABB(EntityLodo.class, axisalignedbb);
                  if (moblist.isEmpty()) {
                     axisalignedbb = (new AxisAlignedBB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1))).grow(16.0D, 16.0D, 16.0D);
                     moblist = worldIn.getEntitiesWithinAABB(EntityParasiteBase.class, axisalignedbb);
                     if (moblist.size() <= 10) {
                        EntityLodo l222 = new EntityLodo(worldIn);
                        l222.setPosition((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D);
                        l222.setFloorTimer();
                        worldIn.spawnEntity(l222);
                        worldIn.setEntityState(l222, (byte)50);
                     }
                  }
               }
            }
         }
      }
   }

   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return TALL_GRASS_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
