package vazkii.quark.building.block.slab;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.block.BlockQuarkSlab;
import vazkii.quark.building.feature.Thatch;

public class BlockThatchSlab extends BlockQuarkSlab {
   public BlockThatchSlab(boolean doubleSlab) {
      super("thatch_slab", Material.PLANTS, doubleSlab);
      this.setHardness(0.5F);
      this.setSoundType(SoundType.PLANT);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      entityIn.fall(fallDistance, Thatch.fallDamageMultiplier);
   }
}
