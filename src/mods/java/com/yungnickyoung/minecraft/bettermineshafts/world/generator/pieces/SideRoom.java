package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.bettermineshafts.config.Configuration;
import com.yungnickyoung.minecraft.bettermineshafts.util.BoxUtil;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.BetterMineshaftGenerator;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariantSettings;
import java.util.List;
import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTableList;

public class SideRoom extends MineshaftPiece {
   private boolean hasDownstairs;
   private static final int SECONDARY_AXIS_LEN = 10;
   private static final int Y_AXIS_LEN = 5;
   private static final int MAIN_AXIS_LEN = 5;
   private static final int LOCAL_X_END = 9;
   private static final int LOCAL_Y_END = 4;
   private static final int LOCAL_Z_END = 4;

   public SideRoom() {
   }

   public SideRoom(int i, int pieceChainLen, Random random, StructureBoundingBox blockBox, EnumFacing direction, MineshaftVariantSettings settings) {
      super(i, pieceChainLen, settings);
      this.setCoordBaseMode(direction);
      this.boundingBox = blockBox;
   }

   @ParametersAreNonnullByDefault
   protected void writeStructureToNBT(NBTTagCompound tag) {
      super.writeStructureToNBT(tag);
      tag.setBoolean("hasDownstairs", this.hasDownstairs);
   }

   @ParametersAreNonnullByDefault
   protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager templateManager) {
      super.readStructureFromNBT(tag, templateManager);
      this.hasDownstairs = tag.getBoolean("hasDownstairs");
   }

   public static StructureBoundingBox determineBoxPosition(List<StructureComponent> list, Random random, int x, int y, int z, EnumFacing direction) {
      StructureBoundingBox blockBox = BoxUtil.boxFromCoordsWithRotation(x, y, z, 10, 5, 5, direction);
      StructureComponent intersectingPiece = StructureComponent.findIntersecting(list, blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, Random random) {
      if (random.nextFloat() < Configuration.spawnRates.workstationDungeonSpawnRate) {
         EnumFacing direction = this.getCoordBaseMode();
         if (direction == null) {
            return;
         }

         StructureComponent newDungeonPiece = null;
         switch(direction) {
         case NORTH:
            newDungeonPiece = BetterMineshaftGenerator.generateAndAddSideRoomDungeonPiece(structurePiece, list, random, this.boundingBox.minX + 6, this.boundingBox.minY - 4, this.boundingBox.maxZ, this.getCoordBaseMode(), this.getComponentType(), 0);
            break;
         case SOUTH:
            newDungeonPiece = BetterMineshaftGenerator.generateAndAddSideRoomDungeonPiece(structurePiece, list, random, this.boundingBox.minX + 6, this.boundingBox.minY - 4, this.boundingBox.minZ, this.getCoordBaseMode(), this.getComponentType(), 0);
            break;
         case WEST:
            newDungeonPiece = BetterMineshaftGenerator.generateAndAddSideRoomDungeonPiece(structurePiece, list, random, this.boundingBox.maxX, this.boundingBox.minY - 4, this.boundingBox.minZ + 6, this.getCoordBaseMode(), this.getComponentType(), 0);
            break;
         case EAST:
            newDungeonPiece = BetterMineshaftGenerator.generateAndAddSideRoomDungeonPiece(structurePiece, list, random, this.boundingBox.minX, this.boundingBox.minY - 4, this.boundingBox.minZ + 6, this.getCoordBaseMode(), this.getComponentType(), 0);
         }

         if (newDungeonPiece != null) {
            this.hasDownstairs = true;
         }
      }

   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, Random random, StructureBoundingBox box) {
      if (this.isLiquidInStructureBoundingBox(world, box)) {
         return false;
      } else if (!this.isInOcean(world, 0, 0) && !this.isInOcean(world, 9, 4)) {
         this.fill(world, box, random, 0, 0, 0, 9, 1, 4, this.getBrickSelector());
         this.replaceNonAir(world, box, random, 0, 2, 0, 9, 3, 4, this.getBrickSelector());
         this.fill(world, box, 1, 1, 1, 8, 3, 4, AIR);
         boolean[][] ceiling = new boolean[10][5];

         for(int x = 0; x <= 9; ++x) {
            for(int z = 0; z <= 4; ++z) {
               IBlockState currState = this.getBlockStateFromPosFixed(world, x, 4, z, box);
               if (currState != null && currState != AIR) {
                  this.setBlockState(world, this.getBrickSelector().get(random), x, 4, z, box);
                  ceiling[x][z] = true;
               }
            }
         }

         if (!this.hasDownstairs) {
            this.generateLegs(world, box, random);
         }

         if (random.nextInt(2) == 0) {
            this.setBlockState(world, Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.NORTH), 2, 1, 1, box);
            TileEntity blockEntity = world.getTileEntity(new BlockPos(this.getXWithOffset(2, 1), this.getYWithOffset(1), this.getZWithOffset(2, 1)));
            if (blockEntity instanceof TileEntityFurnace) {
               ((TileEntityFurnace)blockEntity).setInventorySlotContents(1, new ItemStack(Items.COAL, random.nextInt(33)));
            }
         }

         if (random.nextInt(2) == 0) {
            this.setBlockState(world, Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.NORTH), 1, 1, 1, box);
            TileEntity blockEntity = world.getTileEntity(new BlockPos(this.getXWithOffset(1, 1), this.getYWithOffset(1), this.getZWithOffset(1, 1)));
            if (blockEntity instanceof TileEntityFurnace) {
               ((TileEntityFurnace)blockEntity).setInventorySlotContents(1, new ItemStack(Items.COAL, random.nextInt(33)));
            }
         }

         this.chanceAddBlock(world, random, 0.5F, Blocks.CRAFTING_TABLE.getDefaultState(), 3, 1, 1, box);
         if (random.nextInt(4) == 0) {
            this.generateChest(world, box, random, 8, 1, 1, LootTableList.CHESTS_ABANDONED_MINESHAFT);
         }

         if (this.hasDownstairs) {
            this.setBlockState(world, Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.NORTH), 6, 0, 1, box);
            this.setBlockState(world, this.getTrapdoor().withProperty(BlockTrapDoor.FACING, EnumFacing.NORTH), 6, 1, 1, box);
         }

         this.generateIronBarSupports(world, box, random, ceiling);
         this.addBiomeDecorations(world, box, random, 0, 0, 0, 9, 3, 4);
         this.addVines(world, box, random, 1, 0, 1, 8, 4, 3);
         return true;
      } else {
         return false;
      }
   }

   private void generateLegs(World world, StructureBoundingBox box, Random random) {
      this.generateLeg(world, random, box, 1, 1, this.getBrickSelector());
      this.generateLeg(world, random, box, 1, 3, this.getBrickSelector());
      this.generateLeg(world, random, box, 8, 1, this.getBrickSelector());
      this.generateLeg(world, random, box, 8, 3, this.getBrickSelector());
   }

   private void generateIronBarSupports(World world, StructureBoundingBox box, Random random, boolean[][] ceiling) {
      List<Integer> invalidXs = Lists.newLinkedList();

      for(int z = 2; z <= 3; ++z) {
         for(int x = 2; x <= 7; ++x) {
            if (!invalidXs.contains(Integer.valueOf(x)) && ceiling[x][z] && random.nextInt(5) == 0) {
               this.fill(world, box, x, 1, z, x, 3, z, Blocks.IRON_BARS.getDefaultState());
               invalidXs.add(Integer.valueOf(x));
               ++x;
            }
         }
      }

   }
}
