package paulevs.betternether.blocks;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockDoor.EnumHingePosition;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import paulevs.betternether.BetterNether;

public class BlockBNDoor extends BlockDoor {
   protected BlockBNDoor(String name, Material material, SoundType sound) {
      super(material);
      this.setHardness(1.0F);
      this.setResistance(1.0F);
      this.setSoundType(sound);
      this.setRegistryName(name);
      this.setUnlocalizedName(name);
      this.setCreativeTab(BetterNether.BN_TAB);
   }

   public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
      state = state.withProperty(FACING, player.getAdjustedHorizontalFacing());
      if (world.getBlockState(pos.offset(((EnumFacing)state.getValue(FACING)).rotateYCCW())).getBlock() == this) {
         state = state.withProperty(HINGE, EnumHingePosition.RIGHT);
      }

      world.setBlockState(pos, state.withProperty(HALF, EnumDoorHalf.LOWER));
      world.setBlockState(pos.up(), state.withProperty(HALF, EnumDoorHalf.UPPER));
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Item.getItemFromBlock(this);
   }

   public boolean canPlaceBlockAt(World world, BlockPos pos) {
      return world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP) && world.getBlockState(pos).getBlock().isReplaceable(world, pos) && world.getBlockState(pos.up()).getBlock().isReplaceable(world, pos.up());
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(this);
   }
}
