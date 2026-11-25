package vazkii.quark.building.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.building.feature.Thatch;

public class BlockThatch extends BlockMod implements IQuarkBlock {
   public BlockThatch() {
      super("thatch", Material.GRASS, new String[0]);
      this.setHardness(0.5F);
      this.setSoundType(SoundType.PLANT);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      entityIn.fall(fallDistance, Thatch.fallDamageMultiplier);
   }
}
