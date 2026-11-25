package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariantSettings;
import java.util.List;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class LayeredIntersection5 extends MineshaftPiece {
   private static final int SECONDARY_AXIS_LEN = 5;
   private static final int Y_AXIS_LEN = 10;
   private static final int MAIN_AXIS_LEN = 5;
   private static final int LOCAL_X_END = 4;
   private static final int LOCAL_Y_END = 9;
   private static final int LOCAL_Z_END = 4;

   public LayeredIntersection5() {
   }

   public LayeredIntersection5(int i, int pieceChainLen, Random random, StructureBoundingBox blockBox, EnumFacing direction, MineshaftVariantSettings settings) {
      super(i, pieceChainLen, settings);
      this.setCoordBaseMode(direction);
      this.boundingBox = blockBox;
   }

   public static StructureBoundingBox determineBoxPosition(List<StructureComponent> list, Random random, int x, int y, int z, EnumFacing direction) {
      StructureBoundingBox blockBox = new StructureBoundingBox(x, y, z, x, y + 10 - 1, z);
      switch(direction) {
      case NORTH:
      default:
         blockBox.maxX = x + 4;
         blockBox.minZ = z - 4;
         break;
      case SOUTH:
         blockBox.minX = x - 4;
         blockBox.maxZ = z + 4;
         break;
      case WEST:
         blockBox.minX = x - 4;
         blockBox.minZ = z - 4;
         break;
      case EAST:
         blockBox.maxX = x + 4;
         blockBox.maxZ = z + 4;
      }

      StructureComponent intersectingPiece = StructureComponent.findIntersecting(list, blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, Random random) {
      EnumFacing direction = this.getCoordBaseMode();
      if (direction != null) {
         switch(direction) {
         case NORTH:
         case SOUTH:
         default:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.maxZ, EnumFacing.WEST, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 5, this.boundingBox.minZ, EnumFacing.EAST, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + 5, this.boundingBox.maxZ, EnumFacing.WEST, this.getComponentType(), this.pieceChainLen);
            break;
         case WEST:
         case EAST:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX, this.boundingBox.minY + 5, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, this.getComponentType(), this.pieceChainLen);
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY + 5, this.boundingBox.minZ - 1, EnumFacing.NORTH, this.getComponentType(), this.pieceChainLen);
         }

      }
   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
      if (this.isLiquidInStructureBoundingBox(world, box)) {
         return false;
      } else if (!this.isInOcean(world, 0, 0) && !this.isInOcean(world, 4, 4)) {
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 1, 0, 4, 9, 4, this.getMainSelector());
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 0, 0, 4, 0, 4, this.getFloorSelector());
         this.fill(world, box, 0, 1, 0, 4, 8, 3, AIR);
         this.fill(world, box, 0, 5, 4, 4, 8, 4, AIR);
         this.replaceAir(world, box, 0, 0, 0, 4, 0, 3, this.getMainBlock());
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.NORTH_SOUTH), 2, 1, 0, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.NORTH_SOUTH), 2, 1, 1, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.EAST_WEST), 0, 1, 2, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.EAST_WEST), 1, 1, 2, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.EAST_WEST), 3, 1, 2, box);
         this.chanceAddBlock(world, random, 0.5F, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.EAST_WEST), 4, 1, 2, box);
         this.fill(world, box, 0, 5, 0, 4, 5, 4, this.getMainBlock());
         this.chanceReplaceNonAir(world, box, random, 0.5F, 0, 5, 0, 4, 5, 4, this.getMainSelector());
         this.fill(world, box, 1, 5, 1, 3, 5, 3, AIR);
         this.fill(world, box, 1, 1, 1, 1, 8, 1, this.getSupportBlock());
         this.fill(world, box, 3, 1, 1, 3, 8, 1, this.getSupportBlock());
         this.fill(world, box, 1, 1, 3, 1, 8, 3, this.getSupportBlock());
         this.fill(world, box, 3, 1, 3, 3, 8, 3, this.getSupportBlock());
         IBlockState LADDER = Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.SOUTH);
         this.fill(world, box, 2, 1, 3, 2, 5, 3, LADDER);
         this.addBiomeDecorations(world, box, random, 0, 0, 0, 4, 8, 4);
         this.addVines(world, box, random, 1, 0, 1, 3, 9, 3);
         return true;
      } else {
         return false;
      }
   }
}
