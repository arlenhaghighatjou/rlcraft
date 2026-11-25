package vazkii.quark.base.block;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.item.ItemModBlockSlab;

public class BlockQuarkSlab extends BlockModSlab implements IQuarkBlock {
   public BlockQuarkSlab(String name, Material materialIn, boolean doubleSlab) {
      super(name, materialIn, doubleSlab);
   }

   public ItemBlock createItemBlock(ResourceLocation res) {
      return !this.isDouble() ? new ItemModBlockSlab(this, res) {
         public int getItemBurnTime(ItemStack itemStack) {
            return BlockQuarkSlab.this.blockMaterial == Material.WOOD ? 150 : -1;
         }
      } : null;
   }

   public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return this.blockMaterial == Material.WOOD ? 20 : super.getFlammability(world, pos, face);
   }

   public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
      return this.blockMaterial == Material.WOOD ? 5 : super.getFireSpreadSpeed(world, pos, face);
   }
}
