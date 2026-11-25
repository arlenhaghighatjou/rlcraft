package vazkii.quark.misc.block;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.BlockQuarkDust;

public class BlockGlowstoneDust extends BlockQuarkDust {
   public BlockGlowstoneDust() {
      super("glowstone_dust_block");
      this.setLightLevel(0.875F);
      this.disableStats();
   }

   public ItemBlock createItemBlock(ResourceLocation res) {
      return null;
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      if ((double)rand.nextFloat() < 0.1D) {
         float x = (float)pos.getX() + 0.3F + rand.nextFloat() * 0.4F;
         float y = (float)pos.getY() + 0.2F;
         float z = (float)pos.getZ() + 0.3F + rand.nextFloat() * 0.4F;
         worldIn.spawnParticle(EnumParticleTypes.REDSTONE, (double)x, (double)y, (double)z, 1.0D, 1.0D, 0.0D, new int[0]);
      }

   }

   public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
   }

   public int getColor(IBlockAccess world, IBlockState state, BlockPos pos, int tint) {
      return 16773120;
   }

   @Nonnull
   public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
      return Items.GLOWSTONE_DUST;
   }
}
