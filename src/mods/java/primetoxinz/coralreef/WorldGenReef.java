package primetoxinz.coralreef;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenReef extends WorldGenerator {
   private final IBlockState state;

   public WorldGenReef(IBlockState state) {
      this.state = state;
   }

   public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos pos) {
      if (isOceanFloor(worldIn, pos) && rand.nextDouble() <= CoralReef.ConfigHandler.reef.reefSparsity) {
         worldIn.setBlockState(pos, this.state);
         if (rand.nextDouble() <= CoralReef.ConfigHandler.reef.coralSparsity) {
            CoralReef.CORAL.placeAt(worldIn, pos.up());
         }
      }

      return true;
   }

   public static boolean isMaterial(Material material, World world, BlockPos pos) {
      return world.getBlockState(pos).getMaterial() == material;
   }

   public static boolean isOceanFloor(World world, BlockPos pos) {
      return isMaterial(Material.WATER, world, pos.up(1)) && isMaterial(Material.WATER, world, pos.up(2)) && !isMaterial(Material.WATER, world, pos);
   }
}
