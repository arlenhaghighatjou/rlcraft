package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.util.BoxUtil;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariantSettings;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class SmallTunnelTurn extends MineshaftPiece {
   private SmallTunnelTurn.TurnDirection turnDirection;
   private static final int SECONDARY_AXIS_LEN = 5;
   private static final int Y_AXIS_LEN = 5;
   private static final int MAIN_AXIS_LEN = 5;
   private static final int LOCAL_X_END = 4;
   private static final int LOCAL_Y_END = 4;
   private static final int LOCAL_Z_END = 4;

   public SmallTunnelTurn() {
   }

   public SmallTunnelTurn(int i, int chunkPieceLen, Random random, StructureBoundingBox blockBox, EnumFacing direction, MineshaftVariantSettings settings) {
      super(i, chunkPieceLen, settings);
      this.setCoordBaseMode(direction);
      this.turnDirection = random.nextBoolean() ? SmallTunnelTurn.TurnDirection.LEFT : SmallTunnelTurn.TurnDirection.RIGHT;
      this.boundingBox = blockBox;
   }

   @ParametersAreNonnullByDefault
   protected void writeStructureToNBT(NBTTagCompound tag) {
      super.writeStructureToNBT(tag);
      tag.setInteger("TurnDirection", this.turnDirection.value);
   }

   @ParametersAreNonnullByDefault
   protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager templateManager) {
      super.readStructureFromNBT(tag, templateManager);
      this.turnDirection = SmallTunnelTurn.TurnDirection.valueOf(tag.getInteger("TurnDirection"));
   }

   public static StructureBoundingBox determineBoxPosition(List<StructureComponent> list, Random random, int x, int y, int z, EnumFacing direction) {
      StructureBoundingBox blockBox = BoxUtil.boxFromCoordsWithRotation(x, y, z, 5, 5, 5, direction);
      StructureComponent intersectingPiece = StructureComponent.findIntersecting(list, blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, Random random) {
      EnumFacing direction = this.getCoordBaseMode();
      if (direction != null) {
         EnumFacing nextDirection = this.turnDirection == SmallTunnelTurn.TurnDirection.LEFT ? direction.rotateYCCW() : direction.rotateY();
         switch(nextDirection) {
         case NORTH:
         default:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, nextDirection, this.getComponentType(), this.pieceChainLen);
            break;
         case SOUTH:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ + 1, nextDirection, this.getComponentType(), this.pieceChainLen);
            break;
         case WEST:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.maxZ, nextDirection, this.getComponentType(), this.pieceChainLen);
            break;
         case EAST:
            BetterMineshaftGenerator.generateAndAddSmallTunnelPiece(structurePiece, list, random, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, nextDirection, this.getComponentType(), this.pieceChainLen);
         }

      }
   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
      if (this.isLiquidInStructureBoundingBox(world, box)) {
         return false;
      } else if (!this.isInOcean(world, 0, 0) && !this.isInOcean(world, 4, 4)) {
         EnumFacing direction = this.getCoordBaseMode();
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 1, 0, 4, 4, 4, this.getMainSelector());
         this.chanceReplaceNonAir(world, box, random, this.settings.replacementRate, 0, 0, 0, 4, 0, 4, this.getFloorSelector());
         this.fill(world, box, 1, 1, 0, 3, 3, 3, AIR);
         this.replaceAir(world, box, 1, 0, 0, 3, 0, 4, this.getMainBlock());
         this.fill(world, box, 2, 1, 0, 2, 1, 1, Blocks.RAIL.getDefaultState());
         if (this.turnDirection == SmallTunnelTurn.TurnDirection.LEFT) {
            if (direction != EnumFacing.NORTH && direction != EnumFacing.EAST) {
               this.generateRightTurn(world, box);
            } else {
               this.generateLeftTurn(world, box);
            }
         } else if (direction != EnumFacing.NORTH && direction != EnumFacing.EAST) {
            this.generateLeftTurn(world, box);
         } else {
            this.generateRightTurn(world, box);
         }

         this.addBiomeDecorations(world, box, random, 0, 0, 0, 4, 3, 4);
         this.addVines(world, box, random, 1, 0, 1, 3, 4, 3);
         return true;
      } else {
         return false;
      }
   }

   private void generateLeftTurn(World world, StructureBoundingBox box) {
      this.fill(world, box, 0, 1, 1, 0, 3, 3, AIR);
      this.fill(world, box, 0, 0, 0, 0, 0, 3, this.getMainBlock());
      this.fill(world, box, 2, 1, 2, 2, 1, 2, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.SOUTH_WEST));
      this.fill(world, box, 0, 1, 2, 1, 1, 2, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.EAST_WEST));
   }

   private void generateRightTurn(World world, StructureBoundingBox box) {
      this.fill(world, box, 4, 1, 1, 4, 3, 3, AIR);
      this.fill(world, box, 4, 0, 0, 4, 0, 3, this.getMainBlock());
      this.fill(world, box, 2, 1, 2, 2, 1, 2, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.SOUTH_EAST));
      this.fill(world, box, 3, 1, 2, 4, 1, 2, Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, EnumRailDirection.EAST_WEST));
   }

   public static enum TurnDirection {
      LEFT(0),
      RIGHT(1);

      private final int value;

      private TurnDirection(int value) {
         this.value = value;
      }

      public static SmallTunnelTurn.TurnDirection valueOf(int value) {
         return (SmallTunnelTurn.TurnDirection)Arrays.stream(values()).filter((dir) -> {
            return dir.value == value;
         }).findFirst().get();
      }
   }
}
