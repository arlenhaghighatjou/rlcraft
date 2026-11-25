package com.alcatrazescapee.notreepunching.common.blocks;

import com.alcatrazescapee.alcatrazcore.block.BlockCore;
import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.notreepunching.ModConfig;
import com.alcatrazescapee.notreepunching.common.items.ItemRock;
import com.alcatrazescapee.notreepunching.util.types.Stone;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@ParametersAreNonnullByDefault
public class BlockRock extends BlockCore {
   private static final Map<Stone, BlockRock> MAP = new HashMap();
   private final Stone type;

   public static BlockRock get(Stone type) {
      return (BlockRock)MAP.get(type);
   }

   public BlockRock(Stone type) {
      super(Material.GROUND);
      this.type = type;
      MAP.put(type, this);
      this.setHardness(0.15F);
      this.setSoundType(SoundType.STONE);
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.615D, 0.0625D, 0.625D);
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return NULL_AABB;
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote) {
         IBlockState stateUnder = worldIn.getBlockState(pos.down());
         if (!stateUnder.isNormalCube()) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         }
      }

   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (ModConfig.BALANCE.canPickUpRocks) {
         ItemStack stack = this.getPickBlock(state, (RayTraceResult)null, worldIn, pos, playerIn);
         CoreHelpers.giveItemToPlayer(worldIn, playerIn, stack);
         worldIn.setBlockToAir(pos);
         return true;
      } else {
         return false;
      }
   }

   public boolean canSpawnInBlock() {
      return true;
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      drops.clear();
      drops.add(new ItemStack(ItemRock.get(this.type)));
   }

   @Nonnull
   public ItemStack getPickBlock(IBlockState state, @Nullable RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(ItemRock.get(this.type));
   }

   @Nullable
   public String getHarvestTool(IBlockState state) {
      return null;
   }

   public int getHarvestLevel(IBlockState state) {
      return 0;
   }

   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return true;
   }
}
