package paulevs.betternether.blocks;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEyeball extends BlockEyeBase {
   public BlockEyeball() {
      this.setRegistryName("block_eyeball");
      this.setUnlocalizedName("block_eyeball");
   }

   public int quantityDropped(EaglercraftRandom random) {
      return 1 + random.nextInt(4);
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, EaglercraftRandom rand) {
      super.randomDisplayTick(stateIn, worldIn, pos, rand);
      if (rand.nextInt(5) == 0) {
         worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, (double)pos.getX() + rand.nextDouble(), (double)pos.getY(), (double)((float)pos.getZ() + rand.nextFloat()), 0.0D, 0.0D, 0.0D, new int[0]);
      }

   }
}
