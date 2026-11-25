package dttraverse.trees;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import dttraverse.ModContent;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import prospector.traverse.world.TraverseWorld;

public final class SpeciesAutumnYellow extends Species {
   public SpeciesAutumnYellow(TreeFamily treeFamily) {
      super(new ResourceLocation("dttraverse", "autumn_yellow"), treeFamily, ModContent.autumnYellowLeavesProperties);
      this.setBasicGrowingParameters(0.1F, 14.0F, 4, 4, 1.25F);
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

   public boolean rot(World world, BlockPos pos, int neighborCount, int radius, EaglercraftRandom random, boolean rapid) {
      if (super.rot(world, pos, neighborCount, radius, random, rapid)) {
         if (radius > 4 && TreeHelper.isRooty(world.getBlockState(pos.down())) && world.getLightFor(EnumSkyBlock.SKY, pos) < 4) {
            world.setBlockState(pos, Blocks.BROWN_MUSHROOM.getDefaultState());
            world.setBlockState(pos.down(), Blocks.DIRT.getDefaultState(), 3);
         }

         return true;
      } else {
         return false;
      }
   }
}
