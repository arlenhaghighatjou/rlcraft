package svenhjol.charm.crafting.block;

import java.util.Random;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.charm.crafting.feature.EnderPearlBlock;
import svenhjol.meson.MesonBlock;

public class BlockEnderPearl extends MesonBlock {
   public BlockEnderPearl() {
      super(Material.GLASS, "ender_pearl_block");
      this.setSoundType(SoundType.GLASS);
      this.setHardness(EnderPearlBlock.hardness);
      this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
      this.setHarvestLevel("pickaxe", 0);
   }

   public int quantityDropped(Random random) {
      return 9;
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.ENDER_PEARL;
   }

   public String getModId() {
      return "charm";
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      super.randomDisplayTick(stateIn, worldIn, pos, rand);
      if (EnderPearlBlock.showParticles && rand.nextInt(10) == 0) {
         worldIn.spawnParticle(EnumParticleTypes.PORTAL, (double)((float)pos.getX() + rand.nextFloat()), (double)((float)pos.getY() + 1.1F), (double)((float)pos.getZ() + rand.nextFloat()), 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }
}
