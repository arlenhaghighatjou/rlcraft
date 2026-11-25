package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshafts;
import com.yungnickyoung.minecraft.bettermineshafts.util.ColPos;
import com.yungnickyoung.minecraft.bettermineshafts.util.SurfaceUtil;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariantSettings;
import java.util.List;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class VerticalEntrance extends MineshaftPiece {
   private BlockPos centerPos;
   private int yAxisLen = 0;
   private int localYEnd = 0;
   private int tunnelLength = 0;
   private int tunnelFloorAltitude = 0;
   private EnumFacing tunnelEnumFacing = EnumFacing.NORTH;
   private boolean hasTunnel = false;

   public VerticalEntrance() {
   }

   public VerticalEntrance(int i, int pieceChainLen, Random random, MutableBlockPos centerPos, EnumFacing direction, MineshaftVariantSettings settings) {
      super(i, pieceChainLen, settings);
      this.setCoordBaseMode(direction);
      this.centerPos = new BlockPos(centerPos);
      this.boundingBox = getInitialBoundingBox(centerPos);
   }

   @ParametersAreNonnullByDefault
   protected void writeStructureToNBT(NBTTagCompound tag) {
      super.writeStructureToNBT(tag);
      tag.setIntArray("centerPos", new int[]{this.centerPos.getX(), this.centerPos.getY(), this.centerPos.getZ()});
      tag.setInteger("yAxisLen", this.yAxisLen);
      tag.setInteger("tunnelLen", this.tunnelLength);
      tag.setInteger("floorAltitude", this.tunnelFloorAltitude);
      tag.setInteger("tunnelDir", this.tunnelEnumFacing.getHorizontalIndex());
      tag.setBoolean("hasTunnel", this.hasTunnel);
   }

   @ParametersAreNonnullByDefault
   protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager templateManager) {
      super.readStructureFromNBT(tag, templateManager);
      int centerPosX = tag.getIntArray("centerPos")[0];
      int centerPosY = tag.getIntArray("centerPos")[1];
      int centerPosZ = tag.getIntArray("centerPos")[2];
      this.centerPos = new BlockPos(centerPosX, centerPosY, centerPosZ);
      this.yAxisLen = tag.getInteger("yAxisLen");
      this.localYEnd = this.yAxisLen - 1;
      this.tunnelLength = tag.getInteger("tunnelLen");
      this.tunnelFloorAltitude = tag.getInteger("floorAltitude");
      int tunnelDirInt = tag.getInteger("tunnelDir");
      this.tunnelEnumFacing = tunnelDirInt == -1 ? null : EnumFacing.getHorizontal(tunnelDirInt);
      this.hasTunnel = tag.getBoolean("hasTunnel");
   }

   private static StructureBoundingBox getInitialBoundingBox(BlockPos centerPos) {
      return new StructureBoundingBox(centerPos.getX() - 2, centerPos.getY(), centerPos.getZ() - 2, centerPos.getX() + 12, 256, centerPos.getZ() + 12);
   }

   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, Random random) {
      EnumFacing direction = this.getCoordBaseMode();
      if (direction != null) {
         switch(direction) {
         case NORTH:
         default:
            BetterMineshaftGenerator.generateAndAddBigTunnelPiece(structurePiece, list, random, this.centerPos.getX() - 4, this.centerPos.getY(), this.centerPos.getZ() - 3, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case SOUTH:
            BetterMineshaftGenerator.generateAndAddBigTunnelPiece(structurePiece, list, random, this.centerPos.getX() + 4, this.centerPos.getY(), this.centerPos.getZ() + 3, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case WEST:
            BetterMineshaftGenerator.generateAndAddBigTunnelPiece(structurePiece, list, random, this.centerPos.getX() - 3, this.centerPos.getY(), this.centerPos.getZ() + 4, direction, this.getComponentType(), this.pieceChainLen);
            break;
         case EAST:
            BetterMineshaftGenerator.generateAndAddBigTunnelPiece(structurePiece, list, random, this.centerPos.getX() + 3, this.centerPos.getY(), this.centerPos.getZ() - 4, direction, this.getComponentType(), this.pieceChainLen);
         }

      }
   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
      if (!this.hasTunnel) {
         this.determineEnumFacing(world);
      }

      if (this.hasTunnel) {
         this.generateVerticalShaft(world, random, box);
         this.generateSurfaceTunnel(world, random, box);
         return true;
      } else {
         return false;
      }
   }

   private void generateVerticalShaft(World world, Random random, StructureBoundingBox box) {
      int shaftStartX = 0;
      int shaftStartZ = 0;
      int shaftEndX = 4;
      int shaftEndZ = 4;
      EnumFacing facing = this.getCoordBaseMode();
      if (facing == EnumFacing.NORTH || facing == EnumFacing.WEST) {
         shaftStartZ = 10;
         shaftEndZ = 14;
      }

      this.fill(world, box, random, shaftStartX, 0, shaftStartZ, shaftEndX, this.localYEnd, shaftEndZ, this.getMainSelector());
      this.fill(world, box, shaftStartX + 1, 1, shaftStartZ + 1, shaftEndX - 1, this.localYEnd - 1, shaftEndZ - 1, AIR);
      this.replaceAir(world, box, shaftStartX + 1, 0, shaftStartZ + 1, shaftEndX - 1, 0, shaftEndZ - 1, this.getMainBlock());
      this.replaceAir(world, box, shaftStartX + 2, 1, shaftStartZ, shaftStartX + 2, this.localYEnd - 4, shaftStartZ, this.getMainBlock());
      this.fill(world, box, shaftStartX + 2, 1, shaftStartZ + 1, shaftStartX + 2, this.localYEnd - 4, shaftStartZ + 1, Blocks.LADDER.getDefaultState());
      this.fill(world, box, shaftStartX + 1, 1, shaftStartZ + 4, shaftStartX + 3, 2, shaftStartZ + 4, this.getMainDoorwayWall());
      this.fill(world, box, shaftStartX + 2, 3, shaftStartZ + 4, shaftStartX + 2, 3, shaftStartZ + 4, this.getMainDoorwaySlab());
      this.fill(world, box, shaftStartX + 2, 1, shaftStartZ + 4, shaftStartX + 2, 2, shaftStartZ + 4, AIR);
      this.addBiomeDecorations(world, box, random, shaftStartX + 1, 0, shaftStartZ + 1, shaftEndX - 1, 1, shaftEndZ - 1);
      this.addVines(world, box, random, shaftStartX + 1, 0, shaftStartZ + 1, shaftEndX - 1, this.localYEnd - 4, shaftEndZ - 1);
   }

   private void generateSurfaceTunnel(World world, Random random, StructureBoundingBox box) {
      int tunnelStartX = 0;
      int tunnelStartZ = 0;
      int tunnelEndX = 0;
      int tunnelEndZ = 0;
      EnumFacing facing = this.getCoordBaseMode();
      float rotationDifference = facing.getHorizontalAngle() - this.tunnelEnumFacing.getHorizontalAngle();
      EnumFacing relativeTunnelDir = EnumFacing.fromAngle((double)(EnumFacing.NORTH.getHorizontalAngle() - rotationDifference));
      if (relativeTunnelDir == EnumFacing.NORTH) {
         tunnelStartX = 0;
         tunnelStartZ = 4;
         tunnelEndX = 4;
         tunnelEndZ = 4 + this.tunnelLength;
      } else if ((relativeTunnelDir != EnumFacing.WEST || facing == EnumFacing.SOUTH || facing == EnumFacing.WEST) && (relativeTunnelDir != EnumFacing.EAST || facing != EnumFacing.SOUTH && facing != EnumFacing.WEST)) {
         if (relativeTunnelDir == EnumFacing.SOUTH) {
            tunnelStartX = 0;
            tunnelStartZ = 10 - this.tunnelLength;
            tunnelEndX = 4;
            tunnelEndZ = 10;
         } else if (relativeTunnelDir == EnumFacing.EAST || relativeTunnelDir == EnumFacing.WEST) {
            tunnelStartX = 4;
            tunnelStartZ = 0;
            tunnelEndX = 4 + this.tunnelLength;
            tunnelEndZ = 4;
            if (facing == EnumFacing.NORTH || facing == EnumFacing.WEST) {
               tunnelStartZ = 10;
               tunnelEndZ = 14;
            }
         }
      } else {
         tunnelStartX = 10 - this.tunnelLength;
         tunnelStartZ = 0;
         tunnelEndX = 10;
         tunnelEndZ = 4;
      }

      this.chanceReplaceNonAir(world, box, random, 0.6F, tunnelStartX, this.tunnelFloorAltitude, tunnelStartZ, tunnelEndX, this.tunnelFloorAltitude + 4, tunnelEndZ, this.getMainSelector());
      if (facing.getAxis() == this.tunnelEnumFacing.getAxis()) {
         this.replaceAir(world, box, tunnelStartX + 1, this.tunnelFloorAltitude, tunnelStartZ, tunnelEndX - 1, this.tunnelFloorAltitude, tunnelEndZ, this.getMainBlock());
         this.fill(world, box, tunnelStartX + 1, this.tunnelFloorAltitude + 1, tunnelStartZ, tunnelEndX - 1, this.tunnelFloorAltitude + 3, tunnelEndZ, AIR);
      } else {
         this.replaceAir(world, box, tunnelStartX, this.tunnelFloorAltitude, tunnelStartZ + 1, tunnelEndX, this.tunnelFloorAltitude, tunnelEndZ - 1, this.getMainBlock());
         this.fill(world, box, tunnelStartX, this.tunnelFloorAltitude + 1, tunnelStartZ + 1, tunnelEndX, this.tunnelFloorAltitude + 3, tunnelEndZ - 1, AIR);
      }

      this.addVines(world, box, random, tunnelStartX + 1, this.tunnelFloorAltitude, tunnelStartZ + 1, tunnelEndX - 1, this.tunnelFloorAltitude + 4, tunnelEndZ - 1);
      boolean[] validPositions;
      if (facing.getAxis() == this.tunnelEnumFacing.getAxis()) {
         validPositions = new boolean[tunnelEndZ - tunnelStartZ + 1];

         for(int z = 0; z < validPositions.length; ++z) {
            IBlockState floorBlock = this.getBlockStateFromPos(world, tunnelStartX + 2, this.tunnelFloorAltitude, tunnelStartZ + z, box);
            if (floorBlock.getMaterial().isSolid()) {
               validPositions[z] = true;
            }
         }
      } else {
         validPositions = new boolean[tunnelEndX - tunnelStartX + 1];

         for(int x = 0; x < validPositions.length; ++x) {
            IBlockState floorBlock = this.getBlockStateFromPos(world, tunnelStartX + x, this.tunnelFloorAltitude, tunnelStartZ + 2, box);
            if (floorBlock.getMaterial().isSolid()) {
               validPositions[x] = true;
            }
         }
      }

      if (facing.getAxis() == this.tunnelEnumFacing.getAxis()) {
         for(int z = tunnelStartZ; z <= tunnelEndZ; ++z) {
            int r = random.nextInt(4);
            if (r == 0 && validPositions[z - tunnelStartZ]) {
               this.fill(world, box, tunnelStartX + 1, this.tunnelFloorAltitude + 1, z, tunnelStartX + 1, this.tunnelFloorAltitude + 2, z, this.getSupportBlock());
               this.fill(world, box, tunnelStartX + 3, this.tunnelFloorAltitude + 1, z, tunnelStartX + 3, this.tunnelFloorAltitude + 2, z, this.getSupportBlock());
               this.fill(world, box, tunnelStartX + 1, this.tunnelFloorAltitude + 3, z, tunnelStartX + 3, this.tunnelFloorAltitude + 3, z, this.getMainBlock());
               this.chanceReplaceNonAir(world, box, random, 0.25F, tunnelStartX + 1, this.tunnelFloorAltitude + 3, z, tunnelStartX + 3, this.tunnelFloorAltitude + 3, z, this.getSupportBlock());
               this.chanceReplaceAir(world, box, random, 0.15F, tunnelStartX + 1, this.tunnelFloorAltitude + 3, z - 1, tunnelStartX + 1, this.tunnelFloorAltitude + 3, z + 1, Blocks.WEB.getDefaultState());
               this.chanceReplaceAir(world, box, random, 0.15F, tunnelStartX + 3, this.tunnelFloorAltitude + 3, z - 1, tunnelStartX + 3, this.tunnelFloorAltitude + 3, z + 1, Blocks.WEB.getDefaultState());
               z += 3;
            }
         }
      } else {
         for(int x = tunnelStartX; x <= tunnelEndX; ++x) {
            int r = random.nextInt(4);
            if (r == 0 && validPositions[x - tunnelStartX]) {
               this.fill(world, box, x, this.tunnelFloorAltitude + 1, tunnelStartZ + 1, x, this.tunnelFloorAltitude + 2, tunnelStartZ + 1, this.getSupportBlock());
               this.fill(world, box, x, this.tunnelFloorAltitude + 1, tunnelStartZ + 3, x, this.tunnelFloorAltitude + 2, tunnelStartZ + 3, this.getSupportBlock());
               this.fill(world, box, x, this.tunnelFloorAltitude + 3, tunnelStartZ + 1, x, this.tunnelFloorAltitude + 3, tunnelStartZ + 3, this.getMainBlock());
               this.chanceReplaceNonAir(world, box, random, 0.25F, x, this.tunnelFloorAltitude + 3, tunnelStartZ + 1, x, this.tunnelFloorAltitude + 3, tunnelStartZ + 3, this.getSupportBlock());
               this.chanceReplaceAir(world, box, random, 0.15F, x - 1, this.tunnelFloorAltitude + 3, tunnelStartZ + 1, x + 1, this.tunnelFloorAltitude + 3, tunnelStartZ + 1, Blocks.WEB.getDefaultState());
               this.chanceReplaceAir(world, box, random, 0.15F, x - 1, this.tunnelFloorAltitude + 3, tunnelStartZ + 3, x + 1, this.tunnelFloorAltitude + 3, tunnelStartZ + 3, Blocks.WEB.getDefaultState());
               x += 3;
            }
         }
      }

      if (facing.getAxis() == this.tunnelEnumFacing.getAxis()) {
         for(int z = 0; z < tunnelEndZ - tunnelStartZ + 1; ++z) {
            if (validPositions[z] && random.nextFloat() < 0.3F) {
               this.fill(world, box, tunnelStartX + 2, this.tunnelFloorAltitude + 1, tunnelStartZ + z, tunnelStartX + 2, this.tunnelFloorAltitude + 1, tunnelStartZ + z + 1, Blocks.RAIL.getDefaultState());
               ++z;
            }
         }
      } else {
         for(int x = 0; x < tunnelEndX - tunnelStartX + 1; ++x) {
            if (validPositions[x] && random.nextFloat() < 0.3F) {
               this.fill(world, box, tunnelStartX + x, this.tunnelFloorAltitude + 1, tunnelStartZ + 2, tunnelStartX + x + 1, this.tunnelFloorAltitude + 1, tunnelStartZ + 2, Blocks.RAIL.getDefaultState());
               ++x;
            }
         }
      }

   }

   private void determineEnumFacing(World world) {
      int minSurfaceHeight = 255;

      for(int xOffset = -2; xOffset <= 2; ++xOffset) {
         for(int zOffset = -2; zOffset <= 2; ++zOffset) {
            try {
               int realX = this.centerPos.getX() + xOffset;
               int realZ = this.centerPos.getZ() + zOffset;
               int surfaceHeight = SurfaceUtil.getSurfaceHeight(world, new ColPos(realX, realZ));
               if (surfaceHeight > 1) {
                  minSurfaceHeight = Math.min(minSurfaceHeight, surfaceHeight);
               }
            } catch (NullPointerException var15) {
               BetterMineshafts.LOGGER.error("Unexpected YUNG's Better Mineshafts error. Please report this!");
               BetterMineshafts.LOGGER.error(var15.toString());
               BetterMineshafts.LOGGER.error(var15.getMessage());
            }
         }
      }

      if (minSurfaceHeight >= 60 && minSurfaceHeight != 255) {
         int ceilingHeight = minSurfaceHeight - 2;
         int floorHeight = ceilingHeight - 4;
         this.yAxisLen = ceilingHeight - this.centerPos.getY() + 1;
         this.localYEnd = this.yAxisLen - 1;
         MutableBlockPos mutable = new MutableBlockPos(this.centerPos);
         int radius = 5;
         int maxRadialDist = 2;

         for(int radialDist = 0; radialDist < maxRadialDist; ++radialDist) {
            for(EnumFacing direction : EnumFacing.values()) {
               if (direction == EnumFacing.EAST || direction == EnumFacing.SOUTH) {
                  mutable.setPos(this.centerPos.offset(direction, radius * radialDist + 2));

                  for(int i = radialDist * radius; i < radialDist * radius + radius; ++i) {
                     int surfaceHeight = SurfaceUtil.getSurfaceHeight(world, new ColPos(mutable.getX(), mutable.getZ()));
                     if (surfaceHeight <= floorHeight && surfaceHeight > 1) {
                        this.hasTunnel = true;
                        this.tunnelEnumFacing = direction;
                        this.tunnelFloorAltitude = ceilingHeight - 4 - this.boundingBox.minY;
                        this.tunnelLength = i;
                        return;
                     }

                     mutable.move(direction);
                  }
               }
            }
         }

      }
   }
}
