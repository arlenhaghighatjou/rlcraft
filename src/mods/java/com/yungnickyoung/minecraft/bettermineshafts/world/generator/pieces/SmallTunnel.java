package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.bettermineshafts.config.Configuration;
import com.yungnickyoung.minecraft.bettermineshafts.integration.Integrations;
import com.yungnickyoung.minecraft.bettermineshafts.util.BoxUtil;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariantSettings;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class SmallTunnel extends MineshaftPiece {
   private final List<Integer> supports = Lists.newLinkedList();
   private static final int SECONDARY_AXIS_LEN = 5;
   private static final int Y_AXIS_LEN = 5;
   private static final int MAIN_AXIS_LEN = 8;
   private static final int LOCAL_X_END = 4;
   private static final int LOCAL_Y_END = 4;
   private static final int LOCAL_Z_END = 7;

   public SmallTunnel() {
   }

   public SmallTunnel(int i, int chunkPieceLen, Random random, StructureBoundingBox blockBox, EnumFacing direction, MineshaftVariantSettings settings) {
      super(i, chunkPieceLen, settings);
      this.setCoordBaseMode(direction);
      this.boundingBox = blockBox;
   }

   @ParametersAreNonnullByDefault
   protected void writeStructureToNBT(NBTTagCompound tag) {
      super.writeStructureToNBT(tag);
      NBTTagList listTag1 = new NBTTagList();
      this.supports.forEach((z) -> {
         listTag1.appendTag(new NBTTagInt(z.intValue()));
      });
      tag.setTag("Supports", listTag1);
   }

   @ParametersAreNonnullByDefault
   protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager templateManager) {
      super.readStructureFromNBT(tag, templateManager);
      NBTTagList listTag1 = tag.getTagList("Supports", 3);

      for(int i = 0; i < listTag1.tagCount(); ++i) {
         this.supports.add(Integer.valueOf(listTag1.getIntAt(i)));
      }

   }

   public static StructureBoundingBox determineBoxPosition(List<StructureComponent> list, Random random, int x, int y, int z, EnumFacing direction) {
      StructureBoundingBox blockBox = BoxUtil.boxFromCoordsWithRotation(x, y, z, 5, 5, 8, direction);
      StructureComponent intersectingPiece = StructureComponent.findIntersecting(list, blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   @ParametersAreNonnullByDefault
   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, Random random) {
      EnumFacing direction = this.getCoordBaseMode();
      if (direction != null) {
         switch(direction) {
         case NORTH:
         default:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case SOUTH:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ + 1, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case WEST:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.maxZ, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case EAST:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, direction, this.getComponentType(), this.pieceChainLen);
         }

         this.buildSupports(random);
      }
   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
      if (this.isLiquidInStructureBoundingBox(world, box)) {
         return false;
      } else if (!this.isInOcean(world, 0, 0) && !this.isInOcean(world, 4, 7)) {
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 1, 0, 4, 4, 7, this.getMainSelector());
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 0, 0, 4, 0, 7, this.getFloorSelector());
         this.fill(world, box, 1, 1, 0, 3, 3, 7, AIR);
         this.replaceAir(world, box, 1, 0, 0, 3, 0, 7, this.getMainBlock());
         this.generateSupports(world, box, random);
         this.generateRails(world, box, random);
         this.generateCobwebs(world, box, random);
         this.generateChestCarts(world, box, random, LootTableList.CHESTS_ABANDONED_MINESHAFT);
         this.generateTntCarts(world, box, random);
         this.addVines(world, box, random, 1, 0, 1, 3, 4, 6);
         this.addBiomeDecorations(world, box, random, 1, 0, 0, 3, 3, 7);
         this.generateTorches(world, box, random);
         return true;
      } else {
         return false;
      }
   }

   private void generateCobwebs(World world, StructureBoundingBox box, Random random) {
      this.supports.forEach((z) -> {
         this.chanceReplaceAir(world, box, random, Configuration.spawnRates.cobwebSpawnRate, 1, 3, z.intValue() - 1, 1, 3, z.intValue() + 1, Blocks.WEB.getDefaultState());
         this.chanceReplaceAir(world, box, random, Configuration.spawnRates.cobwebSpawnRate, 3, 3, z.intValue() - 1, 3, 3, z.intValue() + 1, Blocks.WEB.getDefaultState());
      });
   }

   private void generateChestCarts(World world, StructureBoundingBox box, Random random, ResourceLocation lootTableId) {
      for(int z = 0; z <= 7; ++z) {
         if (random.nextFloat() < Configuration.spawnRates.smallShaftChestMinecartSpawnRate) {
            BlockPos blockPos = new BlockPos(this.getXWithOffset(2, z), this.getYWithOffset(1), this.getZWithOffset(2, z));
            if (box.isVecInside(blockPos) && world.getBlockState(blockPos.down()) != AIR) {
               EntityMinecartChest chestMinecartEntity = new EntityMinecartChest(world, (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F));
               chestMinecartEntity.setLootTable(lootTableId, random.nextLong());
               world.spawnEntity(chestMinecartEntity);
            }
         }
      }

   }

   private void generateSupports(World world, StructureBoundingBox box, Random random) {
      IBlockState supportBlock = this.getSupportBlock();
      if (supportBlock.getBlock() instanceof BlockFence) {
         supportBlock = supportBlock.withProperty(BlockFence.WEST, Boolean.valueOf(true)).withProperty(BlockFence.EAST, Boolean.valueOf(true));
      } else if (supportBlock.getBlock() instanceof BlockWall) {
         supportBlock = supportBlock.withProperty(BlockWall.WEST, Boolean.valueOf(true)).withProperty(BlockWall.EAST, Boolean.valueOf(true));
      }

      Iterator var5 = this.supports.iterator();

      while(var5.hasNext()) {
         int z = ((Integer)var5.next()).intValue();
         this.fill(world, box, 1, 1, z, 1, 2, z, this.getSupportBlock());
         this.fill(world, box, 3, 1, z, 3, 2, z, this.getSupportBlock());
         this.fill(world, box, 1, 3, z, 3, 3, z, this.getMainBlock());
         this.chanceReplaceNonAir(world, box, random, 0.25F, 1, 3, z, 3, 3, z, supportBlock);
      }

   }

   private void generateRails(World world, StructureBoundingBox box, Random random) {
      for(int z = 0; z <= 7; ++z) {
         if (random.nextFloat() < 0.3F) {
            this.fill(world, box, 2, 1, z, 2, 1, z + 1, Blocks.RAIL.getDefaultState());
            ++z;
         }
      }

   }

   private void generateTntCarts(World world, StructureBoundingBox box, Random random) {
      for(int z = 0; z <= 7; ++z) {
         if (random.nextFloat() < Configuration.spawnRates.smallShaftTntMinecartSpawnRate) {
            BlockPos blockPos = new BlockPos(this.getXWithOffset(2, z), this.getYWithOffset(1), this.getZWithOffset(2, z));
            if (box.isVecInside(blockPos) && world.getBlockState(blockPos.down()) != AIR) {
               EntityMinecartTNT tntMinecartEntity = new EntityMinecartTNT(world, (double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F));
               world.spawnEntity(tntMinecartEntity);
            }
         }
      }

   }

   private void generateTorches(World world, StructureBoundingBox box, Random random) {
      IBlockState leftTorch = Integrations.getLeftTorch(random);
      IBlockState rightTorch = Integrations.getRightTorch(random);

      for(int z = 0; z <= 7; ++z) {
         if (!this.supports.contains(Integer.valueOf(z))) {
            float r = random.nextFloat();
            if (r < Configuration.spawnRates.torchSpawnRate / 2.0F) {
               BlockPos pos = new BlockPos(this.getXWithOffset(1, z), this.getYWithOffset(2), this.getZWithOffset(1, z));
               BlockPos adjPos = new BlockPos(this.getXWithOffset(0, z), this.getYWithOffset(2), this.getZWithOffset(0, z));
               boolean canPlace = leftTorch != null && leftTorch.getBlock().canPlaceBlockAt(world, pos) && world.getBlockState(adjPos) != AIR;
               if (canPlace) {
                  this.replaceAir(world, box, 1, 2, z, 1, 2, z, leftTorch);
                  if (Integrations.VARIED_COMMODITIES.isTorchVariedCommoditiesCandle(leftTorch)) {
                     EnumFacing direction = this.getCoordBaseMode();
                     if (direction == null) {
                        direction = EnumFacing.NORTH;
                     }

                     int rotation;
                     switch(direction) {
                     case NORTH:
                     case SOUTH:
                     default:
                        rotation = 2;
                        break;
                     case WEST:
                     case EAST:
                        rotation = 4;
                     }

                     Integrations.VARIED_COMMODITIES.spawnCandleTorchEntity(world, this.getXWithOffset(1, z), this.getYWithOffset(2), this.getZWithOffset(1, z), rotation);
                  }
               }
            } else if (r < Configuration.spawnRates.torchSpawnRate) {
               BlockPos pos = new BlockPos(this.getXWithOffset(3, z), this.getYWithOffset(2), this.getZWithOffset(3, z));
               BlockPos adjPos = new BlockPos(this.getXWithOffset(4, z), this.getYWithOffset(2), this.getZWithOffset(4, z));
               boolean canPlace = rightTorch != null && rightTorch.getBlock().canPlaceBlockAt(world, pos) && world.getBlockState(adjPos) != AIR;
               if (canPlace) {
                  this.replaceAir(world, box, 3, 2, z, 3, 2, z, rightTorch);
                  if (Integrations.VARIED_COMMODITIES.isTorchVariedCommoditiesCandle(rightTorch)) {
                     EnumFacing direction = this.getCoordBaseMode();
                     if (direction == null) {
                        direction = EnumFacing.NORTH;
                     }

                     int rotation;
                     switch(direction) {
                     case NORTH:
                     case SOUTH:
                     default:
                        rotation = 6;
                        break;
                     case WEST:
                     case EAST:
                        rotation = 0;
                     }

                     Integrations.VARIED_COMMODITIES.spawnCandleTorchEntity(world, this.getXWithOffset(3, z), this.getYWithOffset(2), this.getZWithOffset(3, z), rotation);
                  }
               }
            }
         }
      }

   }

   private void buildSupports(Random random) {
      for(int z = 0; z <= 7; ++z) {
         int r = random.nextInt(7);
         if (r == 0) {
            this.supports.add(Integer.valueOf(z));
            z += 5;
         }
      }

   }
}
