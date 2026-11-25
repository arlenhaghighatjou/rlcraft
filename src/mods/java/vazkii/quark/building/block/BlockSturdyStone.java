package vazkii.quark.building.block;

import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockSturdyStone extends BlockMod implements IQuarkBlock {
   public BlockSturdyStone() {
      super("sturdy_stone", Material.ROCK, new String[0]);
      this.setHardness(4.0F);
      this.setResistance(10.0F);
      this.setSoundType(SoundType.STONE);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   @Nonnull
   public EnumPushReaction getMobilityFlag(IBlockState state) {
      return EnumPushReaction.BLOCK;
   }
}
