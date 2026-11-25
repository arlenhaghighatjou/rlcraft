package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.google.common.collect.ImmutableSet;
import com.yungnickyoung.minecraft.bettermineshafts.util.BlockSetSelector;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariantSettings;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariants;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public abstract class MineshaftPiece extends StructureComponent {
   protected MineshaftVariantSettings settings;
   protected int pieceChainLen;
   protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
   private static final Set<Material> LIQUIDS = ImmutableSet.of(Material.LAVA, Material.WATER);

   public MineshaftPiece() {
   }

   public MineshaftPiece(int i, int pieceChainLen, MineshaftVariantSettings settings) {
      super(i);
      this.settings = settings;
      this.pieceChainLen = pieceChainLen;
   }

   protected void writeStructureToNBT(NBTTagCompound tag) {
      int index = MineshaftVariants.get().getVariants().indexOf(this.settings);
      tag.setInteger("MST", index);
   }

   @ParametersAreNonnullByDefault
   protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_) {
      int index = tagCompound.getInteger("MST");
      this.settings = index < MineshaftVariants.get().getVariants().size() && index >= 0 ? (MineshaftVariantSettings)MineshaftVariants.get().getVariants().get(index) : MineshaftVariants.get().getDefault();
   }

   public void setBoundingBox(StructureBoundingBox boundingBox) {
      this.boundingBox = boundingBox;
   }

   public MineshaftVariantSettings getSettings() {
      return this.settings;
   }

   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, Random random) {
   }

   protected BlockSetSelector getMainSelector() {
      return this.settings.mainSelector;
   }

   protected BlockSetSelector getFloorSelector() {
      return this.settings.floorSelector;
   }

   protected BlockSetSelector getBrickSelector() {
      return this.settings.brickSelector;
   }

   protected BlockSetSelector getLegSelector() {
      return this.settings.legSelector;
   }

   protected IBlockState getMainBlock() {
      return this.settings.mainBlock;
   }

   protected IBlockState getSupportBlock() {
      return this.settings.supportBlock;
   }

   protected IBlockState getMainSlab() {
      return this.settings.slabBlock;
   }

   protected IBlockState getGravel() {
      return this.settings.gravelBlock;
   }

   protected IBlockState getMainDoorwayWall() {
      return this.settings.stoneWallBlock;
   }

   protected IBlockState getMainDoorwaySlab() {
      return this.settings.stoneSlabBlock;
   }

   protected IBlockState getTrapdoor() {
      return Blocks.TRAPDOOR.getDefaultState();
   }

   protected void addVines(World world, StructureBoundingBox boundingBox, EnumFacing facing, Random random, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      MutableBlockPos mutable = new MutableBlockPos();

      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               mutable.setPos(x, y, z);
               EnumFacing dir = facing.getAxis() == Axis.X ? facing : facing.getOpposite();
               if (this.getBlockStateFromPos(world, x, y, z, boundingBox).getBlock() == Blocks.AIR && this.getBlockStateFromPos(world, x + facing.getFrontOffsetX(), y, z + facing.getFrontOffsetZ(), boundingBox).getBlockFaceShape(world, new BlockPos(x + dir.getFrontOffsetZ(), y, z + dir.getFrontOffsetZ()), dir.getOpposite()) == BlockFaceShape.SOLID && random.nextFloat() < chance) {
                  this.setBlockState(world, Blocks.VINE.getDefaultState().withProperty(BlockVine.NORTH, Boolean.valueOf(dir == EnumFacing.NORTH)).withProperty(BlockVine.EAST, Boolean.valueOf(dir == EnumFacing.EAST)).withProperty(BlockVine.SOUTH, Boolean.valueOf(dir == EnumFacing.SOUTH)).withProperty(BlockVine.WEST, Boolean.valueOf(dir == EnumFacing.WEST)), x, y, z, boundingBox);
               }
            }
         }
      }

   }

   protected void addVines(World world, StructureBoundingBox boundingBox, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      if (this.settings.vineChance > 0.0F) {
         this.addVines(world, boundingBox, EnumFacing.EAST, random, this.settings.vineChance, minX, minY, minZ, maxX, maxY, maxZ);
         this.addVines(world, boundingBox, EnumFacing.WEST, random, this.settings.vineChance, minX, minY, minZ, maxX, maxY, maxZ);
         this.addVines(world, boundingBox, EnumFacing.NORTH, random, this.settings.vineChance, minX, minY, minZ, maxX, maxY, maxZ);
         this.addVines(world, boundingBox, EnumFacing.SOUTH, random, this.settings.vineChance, minX, minY, minZ, maxX, maxY, maxZ);
      }
   }

   protected void addBiomeDecorations(World world, StructureBoundingBox box, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               BlockPos blockPos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
               IBlockState state = this.getBlockStateFromPos(world, x, y, z, box);
               IBlockState stateBelow = this.getBlockStateFromPos(world, x, y - 1, z, box);
               Block blockBelow = stateBelow.getBlock();
               if (this.settings.snowChance > 0.0F && random.nextFloat() < this.settings.snowChance && state == AIR && blockBelow != Blocks.ICE && blockBelow != Blocks.PACKED_ICE && blockBelow != Blocks.BARRIER && stateBelow.isSideSolid(world, blockPos.down(), EnumFacing.UP)) {
                  this.setBlockState(world, Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS, Integer.valueOf(random.nextInt(2) + 1)), x, y, z, box);
               }

               if (this.settings.cactusChance > 0.0F && random.nextFloat() < this.settings.cactusChance && state == AIR && blockBelow == Blocks.SAND) {
                  this.setBlockState(world, Blocks.CACTUS.getDefaultState().withProperty(BlockCactus.AGE, Integer.valueOf(0)), x, y, z, box);
                  if (random.nextFloat() < 0.5F && this.getBlockStateFromPos(world, x, y + 1, z, box) == AIR) {
                     this.setBlockState(world, Blocks.CACTUS.getDefaultState().withProperty(BlockCactus.AGE, Integer.valueOf(0)), x, y + 1, z, box);
                  }
               }

               if (this.settings.deadBushChance > 0.0F && random.nextFloat() < this.settings.deadBushChance && state == AIR && (blockBelow == Blocks.SAND || blockBelow == Blocks.STAINED_HARDENED_CLAY || blockBelow == Blocks.HARDENED_CLAY || blockBelow == Blocks.DIRT)) {
                  this.setBlockState(world, Blocks.DEADBUSH.getDefaultState(), x, y, z, box);
               }

               if (this.settings.mushroomChance > 0.0F && state == AIR && (blockBelow == Blocks.MYCELIUM || blockBelow == Blocks.DIRT)) {
                  float r = random.nextFloat();
                  if (r < this.settings.mushroomChance / 2.0F) {
                     this.setBlockState(world, Blocks.RED_MUSHROOM.getDefaultState(), x, y, z, box);
                  } else if (r < this.settings.mushroomChance) {
                     this.setBlockState(world, Blocks.BROWN_MUSHROOM.getDefaultState(), x, y, z, box);
                  }
               }
            }
         }
      }

   }

   protected void generateLeg(World world, Random random, StructureBoundingBox box, int x, int z, BlockSetSelector selector) {
      MutableBlockPos mutable = new MutableBlockPos(x, -1, z);

      for(IBlockState state = this.getBlockStateFromPos(world, mutable, box); this.getYWithOffset(mutable.getY()) > 0 && (state == AIR || LIQUIDS.contains(state.getMaterial())); state = this.getBlockStateFromPos(world, mutable, box)) {
         this.setBlockState(world, selector.get(random), x, mutable.getY(), z, box);
         mutable.move(EnumFacing.DOWN);
      }

   }

   protected void fill(World world, StructureBoundingBox blockBox, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState blockState) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               this.setBlockState(world, blockState, x, y, z, blockBox);
            }
         }
      }

   }

   protected void fill(World world, StructureBoundingBox blockBox, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               this.setBlockState(world, selector.get(random), x, y, z, blockBox);
            }
         }
      }

   }

   protected void replaceAir(World world, StructureBoundingBox blockBox, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState blockState) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               IBlockState currState = this.getBlockStateFromPosFixed(world, x, y, z, blockBox);
               if (currState != null && currState == AIR) {
                  this.setBlockState(world, blockState, x, y, z, blockBox);
               }
            }
         }
      }

   }

   protected void replaceAir(World world, StructureBoundingBox blockBox, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               IBlockState currState = this.getBlockStateFromPosFixed(world, x, y, z, blockBox);
               if (currState != null && currState == AIR) {
                  this.setBlockState(world, selector.get(random), x, y, z, blockBox);
               }
            }
         }
      }

   }

   protected void replaceNonAir(World world, StructureBoundingBox blockBox, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState blockState) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               IBlockState currState = this.getBlockStateFromPosFixed(world, x, y, z, blockBox);
               if (currState != null && currState != AIR) {
                  this.setBlockState(world, blockState, x, y, z, blockBox);
               }
            }
         }
      }

   }

   protected void replaceNonAir(World world, StructureBoundingBox blockBox, Random random, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               IBlockState currState = this.getBlockStateFromPosFixed(world, x, y, z, blockBox);
               if (currState != null && currState != AIR) {
                  this.setBlockState(world, selector.get(random), x, y, z, blockBox);
               }
            }
         }
      }

   }

   protected void chanceFill(World world, StructureBoundingBox blockBox, Random random, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState blockState) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               if (random.nextFloat() < chance) {
                  this.setBlockState(world, blockState, x, y, z, blockBox);
               }
            }
         }
      }

   }

   protected void chanceFill(World world, StructureBoundingBox blockBox, Random random, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               if (random.nextFloat() < chance) {
                  this.setBlockState(world, selector.get(random), x, y, z, blockBox);
               }
            }
         }
      }

   }

   protected void chanceReplaceAir(World world, StructureBoundingBox blockBox, Random random, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState blockState) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               if (random.nextFloat() < chance) {
                  IBlockState currState = this.getBlockStateFromPosFixed(world, x, y, z, blockBox);
                  if (currState != null && currState == AIR) {
                     this.setBlockState(world, blockState, x, y, z, blockBox);
                  }
               }
            }
         }
      }

   }

   protected void chanceReplaceAir(World world, StructureBoundingBox blockBox, Random random, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               if (random.nextFloat() < chance) {
                  IBlockState currState = this.getBlockStateFromPosFixed(world, x, y, z, blockBox);
                  if (currState != null && currState == AIR) {
                     this.setBlockState(world, selector.get(random), x, y, z, blockBox);
                  }
               }
            }
         }
      }

   }

   protected void chanceReplaceNonAir(World world, StructureBoundingBox blockBox, Random random, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState blockState) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               if (random.nextFloat() < chance) {
                  IBlockState currState = this.getBlockStateFromPosFixed(world, x, y, z, blockBox);
                  if (currState != null && currState != AIR) {
                     this.setBlockState(world, blockState, x, y, z, blockBox);
                  }
               }
            }
         }
      }

   }

   protected void chanceReplaceNonAir(World world, StructureBoundingBox blockBox, Random random, float chance, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockSetSelector selector) {
      for(int x = minX; x <= maxX; ++x) {
         for(int y = minY; y <= maxY; ++y) {
            for(int z = minZ; z <= maxZ; ++z) {
               if (random.nextFloat() < chance) {
                  IBlockState currState = this.getBlockStateFromPosFixed(world, x, y, z, blockBox);
                  if (currState != null && currState != AIR) {
                     this.setBlockState(world, selector.get(random), x, y, z, blockBox);
                  }
               }
            }
         }
      }

   }

   protected void chanceAddBlock(World world, Random random, float chance, IBlockState block, int x, int y, int z, StructureBoundingBox blockBox) {
      if (random.nextFloat() < chance) {
         this.setBlockState(world, block, x, y, z, blockBox);
      }

   }

   protected IBlockState getBlockStateFromPosFixed(World world, int x, int y, int z, StructureBoundingBox blockBox) {
      int i = this.getXWithOffset(x, z);
      int j = this.getYWithOffset(y);
      int k = this.getZWithOffset(x, z);
      BlockPos blockPos = new BlockPos(i, j, k);
      return !blockBox.isVecInside(blockPos) ? null : world.getBlockState(blockPos);
   }

   protected IBlockState getBlockStateFromPos(World worldIn, BlockPos pos, StructureBoundingBox boundingboxIn) {
      return super.getBlockStateFromPos(worldIn, pos.getX(), pos.getY(), pos.getZ(), boundingboxIn);
   }

   protected boolean isInOcean(World world, int localX, int localZ) {
      BlockPos pos = new MutableBlockPos(this.getXWithOffset(localX, localZ), 1, this.getZWithOffset(localX, localZ));
      return BiomeDictionary.hasType(world.getBiome(pos), Type.OCEAN);
   }
}
