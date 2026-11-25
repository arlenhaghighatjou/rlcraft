package dttraverse.trees;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import dttraverse.ModContent;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import prospector.traverse.world.TraverseWorld;

public final class SpeciesAutumnOrange extends Species {
   public SpeciesAutumnOrange(TreeFamily treeFamily) {
      super(new ResourceLocation("dttraverse", "autumn_orange"), treeFamily, ModContent.autumnOrangeLeavesProperties);
      this.setBasicGrowingParameters(0.3F, 12.0F, this.upProbability, this.lowestBranchHeight, 0.8F);
      this.setRequiresTileEntity(true);
      this.envFactor(Type.HOT, 0.5F);
      this.envFactor(Type.DRY, 0.5F);
      this.envFactor(Type.FOREST, 1.05F);
      this.generateSeed();
      this.setupStandardSeedDropping();
      this.leavesProperties.setTree(treeFamily);
   }

   public boolean isBiomePerfect(Biome biome) {
      return isOneOfBiomes(biome, new Biome[]{TraverseWorld.autumnalWoodedHillsBiome, TraverseWorld.autumnalWoodsBiome});
   }

   public boolean rot(World world, BlockPos pos, int neighborCount, int radius, Random random, boolean rapid) {
      if (super.rot(world, pos, neighborCount, radius, random, rapid)) {
         if (radius > 4 && TreeHelper.isRooty(world.getBlockState(pos.down())) && world.getLightFor(EnumSkyBlock.SKY, pos) < 4) {
            world.setBlockState(pos, random.nextInt(3) == 0 ? ModBlocks.blockStates.redMushroom : ModBlocks.blockStates.brownMushroom);
            world.setBlockState(pos.down(), ModBlocks.blockStates.podzol);
         }

         return true;
      } else {
         return false;
      }
   }
}
