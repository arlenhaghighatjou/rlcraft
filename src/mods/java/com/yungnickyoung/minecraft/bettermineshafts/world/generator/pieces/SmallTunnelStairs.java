package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.util.BoxUtil;
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

public class SmallTunnelStairs extends MineshaftPiece {
   private static final int SECONDARY_AXIS_LEN = 5;
   private static final int Y_AXIS_LEN = 9;
   private static final int MAIN_AXIS_LEN = 5;
   private static final int LOCAL_X_END = 4;
   private static final int LOCAL_Y_END = 8;
   private static final int LOCAL_Z_END = 4;

   public SmallTunnelStairs() {
   }

   public SmallTunnelStairs(int i, int chunkPieceLen, Random random, StructureBoundingBox blockBox, EnumFacing direction, MineshaftVariantSettings settings) {
      super(i, chunkPieceLen, settings);
      this.setCoordBaseMode(direction);
      this.boundingBox = blockBox;
   }

   public static StructureBoundingBox determineBoxPosition(List<StructureComponent> list, Random random, int x, int y, int z, EnumFacing direction) {
      StructureBoundingBox blockBox = BoxUtil.boxFromCoordsWithRotation(x, y, z, 5, 9, 5, direction);
      StructureComponent intersectingPiece = StructureComponent.findIntersecting(list, blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, Random random) {
      EnumFacing direction = this.getCoordBaseMode();
      if (direction != null) {
         switch(direction) {
         case NORTH:
         default:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY + 4, this.boundingBox.minZ - 1, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case SOUTH:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX, this.boundingBox.minY + 4, this.boundingBox.maxZ + 1, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case WEST:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY + 4, this.boundingBox.maxZ, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case EAST:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY + 4, this.boundingBox.minZ, direction, this.getComponentType(), this.pieceChainLen);
         }

      }
   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
      if (this.isLiquidInStructureBoundingBox(world, box)) {
         return false;
      } else if (!this.isInOcean(world, 0, 0) && !this.isInOcean(world, 4, 4)) {
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 1, 0, 4, 8, 4, this.getMainSelector());
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 0, 0, 4, 0, 4, this.getFloorSelector());

         for(int i = 0; i < 5; ++i) {
            this.fill(world, box, 1, i + 1, i, 3, i + 1 + 2, i, AIR);
            this.chanceReplaceNonAir(world, box, random, 0.5F, 1, i, i, 3, i, i, this.getMainSelector());
            this.replaceAir(world, box, 1, i, i, 3, i, i, this.getMainBlock());
            if (i == 4) {
               this.setBlockState(world, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.NORTH_SOUTH), 2, i + 1, i, box);
            } else {
               this.setBlockState(world, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.ASCENDING_NORTH), 2, i + 1, i, box);
            }
         }

         this.addBiomeDecorations(world, box, random, 1, 0, 0, 3, 7, 4);
         this.addVines(world, box, random, 1, 0, 1, 3, 8, 3);
         return true;
      } else {
         return false;
      }
   }
}
