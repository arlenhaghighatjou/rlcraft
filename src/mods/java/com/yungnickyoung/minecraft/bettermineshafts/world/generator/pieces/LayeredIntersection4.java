package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariantSettings;
import java.util.List;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class LayeredIntersection4 extends MineshaftPiece {
   private static final int SECONDARY_AXIS_LEN = 7;
   private static final int Y_AXIS_LEN = 9;
   private static final int MAIN_AXIS_LEN = 7;
   private static final int LOCAL_X_END = 6;
   private static final int LOCAL_Y_END = 8;
   private static final int LOCAL_Z_END = 6;

   public LayeredIntersection4() {
   }

   public LayeredIntersection4(int i, int pieceChainLen, Random random, StructureBoundingBox blockBox, EnumFacing direction, MineshaftVariantSettings settings) {
      super(i, pieceChainLen, settings);
      this.setCoordBaseMode(direction);
      this.boundingBox = blockBox;
   }

   public static StructureBoundingBox determineBoxPosition(List<StructureComponent> list, Random random, int x, int y, int z, EnumFacing direction) {
      StructureBoundingBox blockBox = new StructureBoundingBox(x, y - 3, z, x, y - 3 + 9 - 1, z);
      switch(direction) {
      case NORTH:
      default:
         blockBox.maxX = x + 5;
         blockBox.minX = x - 1;
         blockBox.minZ = z - 6;
         break;
      case SOUTH:
         blockBox.maxX = x + 1;
         blockBox.minX = x - 5;
         blockBox.maxZ = z + 6;
         break;
      case WEST:
         blockBox.minX = x - 6;
         blockBox.maxZ = z;
         blockBox.minZ = z - 5;
         break;
      case EAST:
         blockBox.maxX = x + 6;
         blockBox.maxZ = z + 4;
         blockBox.minZ = z - 1;
      }

      StructureComponent intersectingPiece = StructureComponent.findIntersecting(list, blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, Random random) {
      EnumFacing direction = this.getCoordBaseMode();
      if (direction != null) {
         switch(direction) {
         case NORTH:
         default:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 5, EnumFacing.EAST, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, EnumFacing.WEST, this.getComponentType(), this.pieceChainLen);
            break;
         case SOUTH:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, EnumFacing.EAST, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 5, EnumFacing.WEST, this.getComponentType(), this.pieceChainLen);
            break;
         case WEST:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ, EnumFacing.WEST, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX - 5, this.boundingBox.minY + 3, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType(), this.pieceChainLen);
            break;
         case EAST:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, EnumFacing.EAST, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX + 5, this.boundingBox.minY + 3, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType(), this.pieceChainLen);
         }

      }
   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
      if (this.isLiquidInStructureBoundingBox(world, box)) {
         return false;
      } else if (!this.isInOcean(world, 0, 0) && !this.isInOcean(world, 6, 6)) {
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 1, 0, 6, 8, 6, this.getMainSelector());
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 0, 0, 6, 0, 6, this.getFloorSelector());
         this.fill(world, box, 2, 1, 2, 4, 1, 4, AIR);
         this.fill(world, box, 2, 2, 1, 4, 2, 5, AIR);
         this.fill(world, box, 2, 3, 0, 4, 6, 6, AIR);
         this.fill(world, box, 0, 3, 2, 6, 6, 4, AIR);
         this.replaceNonAir(world, box, 2, 0, 0, 4, 2, 6, this.getMainBlock());
         this.fill(world, box, 2, 2, 0, 2, 3, 1, this.getMainBlock());
         this.chanceReplaceNonAir(world, box, random, 0.1F, 2, 2, 0, 2, 3, 1, this.getBrickSelector());
         this.fill(world, box, 4, 2, 0, 4, 3, 1, this.getMainBlock());
         this.chanceReplaceNonAir(world, box, random, 0.1F, 4, 2, 0, 4, 3, 1, this.getBrickSelector());
         this.fill(world, box, 2, 2, 5, 2, 3, 6, this.getMainBlock());
         this.chanceReplaceNonAir(world, box, random, 0.1F, 2, 2, 5, 2, 3, 6, this.getBrickSelector());
         this.fill(world, box, 4, 2, 5, 4, 3, 6, this.getMainBlock());
         this.chanceReplaceNonAir(world, box, random, 0.1F, 4, 2, 5, 4, 3, 6, this.getBrickSelector());
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.ASCENDING_SOUTH), 3, 3, 0, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.ASCENDING_SOUTH), 3, 2, 1, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.ASCENDING_SOUTH), 3, 1, 2, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.ASCENDING_NORTH), 3, 1, 4, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.ASCENDING_NORTH), 3, 2, 5, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.ASCENDING_NORTH), 3, 3, 6, box);
         this.setBlockState(world, this.getMainBlock(), 3, 2, 0, box);
         this.setBlockState(world, this.getMainBlock(), 3, 1, 1, box);
         this.setBlockState(world, this.getMainBlock(), 3, 0, 2, box);
         this.setBlockState(world, this.getMainBlock(), 3, 0, 3, box);
         this.setBlockState(world, this.getMainBlock(), 3, 0, 4, box);
         this.setBlockState(world, this.getMainBlock(), 3, 1, 5, box);
         this.setBlockState(world, this.getMainBlock(), 3, 2, 6, box);
         this.fill(world, box, 0, 3, 2, 1, 3, 4, this.getMainBlock());
         this.fill(world, box, 2, 4, 2, 4, 4, 4, this.getMainBlock());
         this.fill(world, box, 5, 3, 2, 6, 3, 4, this.getMainBlock());
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.EAST_WEST), 0, 4, 3, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.ASCENDING_EAST), 1, 4, 3, box);
         this.chanceFill(world, box, random, 0.5F, 2, 5, 3, 4, 5, 3, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.EAST_WEST));
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.ASCENDING_WEST), 5, 4, 3, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.EAST_WEST), 6, 4, 3, box);
         this.addBiomeDecorations(world, box, random, 0, 0, 0, 6, 7, 6);
         this.addVines(world, box, random, 1, 0, 1, 5, 8, 5);
         return true;
      } else {
         return false;
      }
   }
}
