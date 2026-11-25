package vazkii.quark.decoration.block;

import net.minecraft.block.BlockColored;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.experimental.features.ColoredLights;
import vazkii.quark.experimental.lighting.IColoredLightSource;

public class BlockLitLamp extends BlockMod implements IQuarkBlock, IColoredLightSource {
   public BlockLitLamp() {
      super("lit_lamp", Material.GLASS, new String[0]);
      this.setHardness(0.3F);
      this.setLightLevel(1.0F);
      this.setSoundType(SoundType.GLASS);
      this.setCreativeTab(CreativeTabs.REDSTONE);
   }

   public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
      ColoredLights.addLightSource(world, pos, state);
      return super.getLightOpacity(state, world, pos);
   }

   public float[] getColoredLight(IBlockAccess world, BlockPos pos) {
      int index = 0;
      BlockPos down = pos.down();
      IBlockState state = world.getBlockState(down);
      if (state.getBlock() == Blocks.CONCRETE) {
         index = ((EnumDyeColor)state.getValue(BlockColored.COLOR)).ordinal();
      }

      return VANILLA_SPECTRUM_COLORS[index];
   }
}
