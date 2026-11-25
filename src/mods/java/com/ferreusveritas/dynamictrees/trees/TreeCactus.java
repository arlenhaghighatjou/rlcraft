package com.ferreusveritas.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.network.INodeInspector;
import com.ferreusveritas.dynamictrees.api.network.MapSignal;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockBranchCactus;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.event.SpeciesPostGenerationEvent;
import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreator;
import com.ferreusveritas.dynamictrees.systems.nodemappers.NodeFindEnds;
import com.ferreusveritas.dynamictrees.util.CoordUtils;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.registries.IForgeRegistry;

public class TreeCactus extends TreeFamily {
   public TreeCactus() {
      super(new ResourceLocation("dynamictrees", "cactus"));
      this.setPrimitiveLog(Blocks.CACTUS.getDefaultState(), new ItemStack(Blocks.CACTUS));
      this.setStick(ItemStack.EMPTY);
   }

   public ILeavesProperties getCommonLeaves() {
      return (ILeavesProperties)ModBlocks.leaves.get(this.getName().getResourcePath());
   }

   public BlockBranch createBranch() {
      String branchName = this.getName() + "branch";
      return new BlockBranchCactus(branchName);
   }

   public float getPrimaryThickness() {
      return 5.0F;
   }

   public float getSecondaryThickness() {
      return 4.0F;
   }

   public void createSpecies() {
      this.setCommonSpecies(new TreeCactus.SpeciesCactus(this));
   }

   public void registerSpecies(IForgeRegistry<Species> speciesRegistry) {
      super.registerSpecies(speciesRegistry);
      this.getCommonSpecies().generateSeed();
   }

   protected class JoCodeCactus extends JoCode {
      public JoCodeCactus(String code) {
         super(code);
      }

      public void generate(World world, Species species, BlockPos rootPos, Biome biome, EnumFacing facing, int radius, SafeChunkBounds safeBounds) {
         IBlockState initialDirtState = world.getBlockState(rootPos);
         species.placeRootyDirtBlock(world, rootPos, 0);
         radius = MathHelper.clamp(radius, 2, 8);
         BlockPos treePos = rootPos.up();
         this.setFacing(facing);
         this.generateFork(world, species, 0, rootPos, false);
         BlockBranch branch = TreeHelper.getBranch(world.getBlockState(treePos));
         if (branch != null) {
            NodeFindEnds endFinder = new NodeFindEnds();
            MapSignal signal = new MapSignal(new INodeInspector[]{endFinder});
            branch.analyse(world.getBlockState(treePos), world, treePos, EnumFacing.DOWN, signal);
            List<BlockPos> endPoints = endFinder.getEnds();
            species.postGeneration(world, rootPos, biome, radius, endPoints, safeBounds, initialDirtState);
            MinecraftForge.EVENT_BUS.post(new SpeciesPostGenerationEvent(world, species, rootPos, endPoints, safeBounds, initialDirtState));
         } else {
            world.setBlockState(rootPos, initialDirtState, this.careful ? 3 : 2);
         }

      }

      public boolean setBlockForGeneration(World world, Species species, BlockPos pos, EnumFacing dir, boolean careful) {
         IBlockState defaultBranchState = species.getFamily().getDynamicBranch().getDefaultState();
         if (!world.getBlockState(pos).getBlock().isReplaceable(world, pos)) {
            return true;
         } else {
            boolean trunk = false;
            if (dir == EnumFacing.UP) {
               IBlockState downState = world.getBlockState(pos.down());
               if (TreeHelper.isRooty(downState) || downState.getBlock() == defaultBranchState.getBlock() && ((Boolean)downState.getValue(BlockBranchCactus.TRUNK)).booleanValue() && downState.getValue(BlockBranchCactus.ORIGIN) == EnumFacing.DOWN) {
                  trunk = true;
               }
            }

            return !world.setBlockState(pos, defaultBranchState.withProperty(BlockBranchCactus.TRUNK, Boolean.valueOf(trunk)).withProperty(BlockBranchCactus.ORIGIN, dir.getOpposite()), careful ? 3 : 2);
         }
      }
   }

   public class SpeciesCactus extends Species {
      public SpeciesCactus(TreeFamily treeFamily) {
         super(treeFamily.getName(), treeFamily);
         this.setBasicGrowingParameters(0.875F, 4.0F, 4, 2, 1.0F);
         this.setSoilLongevity(1);
         this.addDropCreator(new DropCreator(new ResourceLocation("dynamictrees", "cactusseeds")) {
            public List<ItemStack> getLogsDrop(World world, Species species, BlockPos breakPos, EaglercraftRandom random, List<ItemStack> dropList, float volume) {
               for(int numLogs = (int)(volume / 2.0F); numLogs > 0; numLogs -= 64) {
                  dropList.add(species.getSeedStack(Math.min(numLogs, 64)));
               }

               return dropList;
            }
         });
         this.envFactor(Type.SNOWY, 0.25F);
         this.envFactor(Type.COLD, 0.5F);
         this.envFactor(Type.SANDY, 1.05F);
      }

      protected void setStandardSoils() {
         this.addAcceptableSoils(new String[]{"sandlike"});
      }

      public boolean isTransformable() {
         return false;
      }

      public JoCode getJoCode(String joCodeString) {
         return TreeCactus.this.new JoCodeCactus(joCodeString);
      }

      public float getEnergy(World world, BlockPos pos) {
         long day = world.getTotalWorldTime() / 24000L;
         int month = (int)day / 30;
         return super.getEnergy(world, pos) * this.biomeSuitability(world, pos) + (float)(CoordUtils.coordHashCode(pos.up(month), 2) % 3);
      }

      public BlockRooty getRootyBlock(World world, BlockPos rootPos) {
         return ModBlocks.blockRootySand;
      }

      public boolean isBiomePerfect(Biome biome) {
         return isOneOfBiomes(biome, new Biome[]{Biomes.DESERT, Biomes.DESERT_HILLS, Biomes.MUTATED_DESERT});
      }

      public boolean handleRot(World world, List<BlockPos> ends, BlockPos rootPos, BlockPos treePos, int soilLife, SafeChunkBounds safeBounds) {
         return false;
      }

      protected int[] customDirectionManipulation(World world, BlockPos pos, int radius, GrowSignal signal, int[] probMap) {
         EnumFacing originDir = signal.dir.getOpposite();
         probMap[0] = 0;
         probMap[1] = signal.delta.getX() % 2 != 0 && signal.delta.getZ() % 2 != 0 ? 0 : this.getUpProbability();
         probMap[2] = probMap[3] = probMap[4] = probMap[5] = signal.isInTrunk() && signal.energy > 1.0F ? 1 : 0;
         if (signal.dir != EnumFacing.UP) {
            probMap[signal.dir.ordinal()] = 0;
         }

         probMap[originDir.ordinal()] = 0;
         return probMap;
      }

      protected EnumFacing newDirectionSelected(EnumFacing newDir, GrowSignal signal) {
         if (signal.isInTrunk() && newDir != EnumFacing.UP) {
            signal.energy += 0.0F;
         }

         return newDir;
      }

      public boolean canBoneMeal() {
         return false;
      }

      public boolean transitionToTree(World world, BlockPos pos) {
         TreeFamily tree = this.getFamily();
         if (world.isAirBlock(pos.up()) && this.isAcceptableSoil(world, pos.down(), world.getBlockState(pos.down()))) {
            world.setBlockState(pos, tree.getDynamicBranch().getDefaultState());
            this.placeRootyDirtBlock(world, pos.down(), 15);
            return true;
         } else {
            return false;
         }
      }

      public AxisAlignedBB getSaplingBoundingBox() {
         return new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.5D, 0.625D);
      }

      public SoundType getSaplingSound() {
         return SoundType.CLOTH;
      }
   }
}
