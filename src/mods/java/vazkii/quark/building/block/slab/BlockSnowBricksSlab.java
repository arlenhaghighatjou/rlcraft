package vazkii.quark.building.block.slab;

import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.quark.base.block.BlockQuarkSlab;

public class BlockSnowBricksSlab extends BlockQuarkSlab {
   public BlockSnowBricksSlab(boolean doubleSlab) {
      super("snow_bricks_slab", Material.CRAFTED_SNOW, doubleSlab);
      this.setHardness(0.2F);
      this.setSoundType(SoundType.SNOW);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public boolean isToolEffective(String type, @Nonnull IBlockState state) {
      return type.equals("shovel");
   }

   public boolean canHarvestBlock(IBlockAccess world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player) {
      return true;
   }
}
