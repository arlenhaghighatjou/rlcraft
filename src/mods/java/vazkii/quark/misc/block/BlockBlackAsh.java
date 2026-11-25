package vazkii.quark.misc.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkDust;
import vazkii.quark.misc.item.ItemBlackAshBlock;

public class BlockBlackAsh extends BlockQuarkDust {
   public BlockBlackAsh() {
      super("black_ash");
      this.setCreativeTab(CreativeTabs.DECORATIONS);
   }

   public ItemBlock createItemBlock(ResourceLocation res) {
      return new ItemBlackAshBlock(this, res);
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      if ((double)rand.nextFloat() < 0.1D) {
         float x = (float)pos.getX() + 0.3F + rand.nextFloat() * 0.4F;
         float y = (float)pos.getY() + 0.2F;
         float z = (float)pos.getZ() + 0.3F + rand.nextFloat() * 0.4F;
         worldIn.spawnParticle(EnumParticleTypes.SPELL_MOB, (double)x, (double)y, (double)z, 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }

   public int getColor(IBlockAccess world, IBlockState state, BlockPos pos, int tint) {
      return 2236962;
   }
}
