package svenhjol.meson;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class MesonBlockTE<TE extends TileEntity> extends MesonBlock {
   public MesonBlockTE(Material material, String name) {
      super(material, name);
   }

   public abstract Class<TE> getTileEntityClass();

   public TE getTileEntity(IBlockAccess world, BlockPos pos) {
      return (TE)world.getTileEntity(pos);
   }

   public boolean validTileEntity(TE tile) {
      return tile != null && tile.getClass() == this.getTileEntityClass();
   }

   public void dropsInventory(TE tile, int inventorySize, World world, BlockPos pos) {
      IItemHandler itemHandler = (IItemHandler)tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
      if (tile instanceof MesonTileInventory) {
         ((MesonTileInventory)tile).generateLoot((EntityPlayer)null);
      }

      for(int i = 0; i < inventorySize; ++i) {
         ItemStack stack = ((IItemHandler)Objects.requireNonNull(itemHandler)).getStackInSlot(i);
         if (!stack.isEmpty()) {
            this.doItemSpawn(stack, world, pos);
         }
      }

   }

   public void doItemSpawn(ItemStack stack, World world, BlockPos pos) {
      EntityItem item = new EntityItem(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), stack);
      world.spawnEntity(item);
   }

   public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
      return willHarvest ? true : super.removedByPlayer(state, world, pos, player, false);
   }

   public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      super.harvestBlock(world, player, pos, state, te, stack);
      world.setBlockToAir(pos);
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   public abstract TE createTileEntity(World var1, IBlockState var2);

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      int out = 0;
      TileEntity tile = worldIn.getTileEntity(pos);
      if (tile instanceof MesonTileInventory) {
         out = ((MesonTileInventory)tile).getComparatorOutput();
      }

      return out;
   }
}
