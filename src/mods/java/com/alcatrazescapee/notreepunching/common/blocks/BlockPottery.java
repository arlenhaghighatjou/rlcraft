package com.alcatrazescapee.notreepunching.common.blocks;

import com.alcatrazescapee.alcatrazcore.block.BlockCore;
import com.alcatrazescapee.notreepunching.common.items.ModItems;
import com.alcatrazescapee.notreepunching.util.types.Pottery;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ParametersAreNonnullByDefault
public class BlockPottery extends BlockCore {
   private static final Map<Pottery, BlockPottery> MAP = new HashMap();
   private static final AxisAlignedBB[] AABB = new AxisAlignedBB[]{new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.9375D, 0.9375D), new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.875D, 0.875D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.625D, 0.75D), new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.625D, 0.6875D), new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D)};
   private final Pottery type;

   public static BlockPottery get(Pottery type) {
      return (BlockPottery)MAP.get(type);
   }

   public BlockPottery(Pottery type) {
      super(Material.CLAY);
      this.type = type;
      MAP.put(type, this);
      this.setHardness(0.8F - 0.1F * (float)type.ordinal());
      this.setHarvestLevel("shovel", 0);
      this.setSoundType(SoundType.GROUND);
   }

   @SideOnly(Side.CLIENT)
   public void registerModel() {
      if (this.type == Pottery.LARGE_VESSEL) {
         super.registerModel();
      } else if (this.type != Pottery.WORKED) {
         ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
      }

   }

   @Nonnull
   public Pottery getType() {
      return this.type;
   }

   @Nonnull
   public ItemStack getFiredType() {
      switch(this.type) {
      case LARGE_VESSEL:
         return new ItemStack(ModBlocks.CERAMIC_LARGE_VESSEL);
      case BUCKET:
         return new ItemStack(ModItems.CERAMIC_BUCKET);
      case SMALL_VESSEL:
         return new ItemStack(ModItems.CERAMIC_SMALL_VESSEL);
      case FLOWER_POT:
         return new ItemStack(Items.FLOWER_POT);
      default:
         return ItemStack.EMPTY;
      }
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @Nonnull
   public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
      return AABB[this.type.ordinal()];
   }

   @Nonnull
   public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   @Nullable
   public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
      return AABB[this.type.ordinal()];
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
      if (this.type == Pottery.WORKED) {
         drops.clear();
         drops.add(new ItemStack(Items.CLAY_BALL, 3));
      } else {
         super.getDrops(drops, world, pos, state, fortune);
      }

   }

   @Nonnull
   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return this.type == Pottery.WORKED ? new ItemStack(Blocks.CLAY) : super.getPickBlock(state, target, world, pos, player);
   }
}
