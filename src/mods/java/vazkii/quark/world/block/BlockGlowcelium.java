package vazkii.quark.world.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.block.BlockMod;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockGlowcelium extends BlockMod implements IQuarkBlock {
   public BlockGlowcelium() {
      super("glowcelium", Material.GRASS, new String[0]);
      this.setTickRandomly(true);
      this.setHardness(0.2F);
      this.setLightLevel(0.5F);
      this.setSoundType(SoundType.PLANT);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, EaglercraftRandom rand) {
      if (!worldIn.isRemote) {
         if (worldIn.getBlockState(pos.up()).getLightOpacity(worldIn, pos.up()) > 2) {
            worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.DIRT));
         } else {
            for(int i = 0; i < 4; ++i) {
               BlockPos blockpos = pos.add(rand.nextInt(3) - 1, rand.nextInt(5) - 3, rand.nextInt(3) - 1);
               IBlockState stateAt = worldIn.getBlockState(blockpos);
               IBlockState stateAbove = worldIn.getBlockState(blockpos.up());
               if (stateAt.getBlock() == Blocks.DIRT && stateAt.getValue(BlockDirt.VARIANT) == DirtType.DIRT && stateAbove.getLightOpacity(worldIn, blockpos.up()) <= 2) {
                  worldIn.setBlockState(blockpos, this.getDefaultState());
               }
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      super.randomDisplayTick(stateIn, worldIn, pos, rand);
      if (rand.nextInt(40) == 0) {
         worldIn.spawnParticle(EnumParticleTypes.END_ROD, (double)((float)pos.getX() + rand.nextFloat()), (double)((float)pos.getY() + 1.15F), (double)((float)pos.getZ() + rand.nextFloat()), 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   @Nonnull
   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Blocks.DIRT.getItemDropped(Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.DIRT), rand, fortune);
   }
}
