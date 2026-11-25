package knightminer.inspirations.building.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import knightminer.inspirations.building.InspirationsBuilding;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGlassDoor extends BlockDoor {
   public BlockGlassDoor() {
      super(Material.GLASS);
      this.setHardness(0.3F);
      this.setSoundType(SoundType.GLASS);
      this.disableStats();
   }

   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
   }

   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return InspirationsBuilding.glassDoorItem;
   }

   public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
      return new ItemStack(InspirationsBuilding.glassDoorItem);
   }

   public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
      return true;
   }

   protected ItemStack getSilkTouchDrop(IBlockState state) {
      return new ItemStack(InspirationsBuilding.glassDoorItem);
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 0;
   }

   /** @deprecated */
   @Deprecated
   protected boolean canSilkHarvest() {
      return true;
   }
}
