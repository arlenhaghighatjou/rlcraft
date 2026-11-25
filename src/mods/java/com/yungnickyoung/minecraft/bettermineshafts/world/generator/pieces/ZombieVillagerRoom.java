package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.util.BoxUtil;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariantSettings;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStairs.EnumShape;
import net.minecraft.block.BlockStoneSlab.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.storage.loot.LootTableList;

public class ZombieVillagerRoom extends MineshaftPiece {
   private static final int SECONDARY_AXIS_LEN = 7;
   private static final int Y_AXIS_LEN = 5;
   private static final int MAIN_AXIS_LEN = 7;
   private static final int LOCAL_X_END = 6;
   private static final int LOCAL_Y_END = 4;
   private static final int LOCAL_Z_END = 6;

   public ZombieVillagerRoom() {
   }

   public ZombieVillagerRoom(int i, int chunkPieceLen, EaglercraftRandom random, StructureBoundingBox blockBox, EnumFacing direction, MineshaftVariantSettings settings) {
      super(i, chunkPieceLen, settings);
      this.setCoordBaseMode(direction);
      this.boundingBox = blockBox;
   }

   public static StructureBoundingBox determineBoxPosition(List<StructureComponent> list, EaglercraftRandom random, int x, int y, int z, EnumFacing direction) {
      StructureBoundingBox blockBox = BoxUtil.boxFromCoordsWithRotation(x, y, z, 7, 5, 7, direction);
      StructureComponent intersectingPiece = StructureComponent.findIntersecting(list, blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, EaglercraftRandom random) {
   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, EaglercraftRandom random, StructureBoundingBox box) {
      if (this.isLiquidInStructureBoundingBox(world, box)) {
         return false;
      } else if (!this.isInOcean(world, 0, 0) && !this.isInOcean(world, 6, 6)) {
         this.fill(world, box, 1, 0, 0, 5, 2, 0, Blocks.STONE.getDefaultState());
         this.fill(world, box, 0, 0, 1, 0, 2, 5, Blocks.STONE.getDefaultState());
         this.fill(world, box, 6, 0, 1, 6, 2, 5, Blocks.STONE.getDefaultState());
         this.fill(world, box, 1, 0, 6, 5, 2, 6, Blocks.STONE.getDefaultState());
         this.chanceReplaceNonAir(world, box, random, 0.4F, 1, 0, 0, 5, 2, 0, Blocks.COBBLESTONE.getDefaultState());
         this.chanceReplaceNonAir(world, box, random, 0.4F, 0, 0, 1, 0, 2, 5, Blocks.COBBLESTONE.getDefaultState());
         this.chanceReplaceNonAir(world, box, random, 0.4F, 6, 0, 1, 6, 2, 5, Blocks.COBBLESTONE.getDefaultState());
         this.chanceReplaceNonAir(world, box, random, 0.4F, 1, 0, 6, 5, 2, 6, Blocks.COBBLESTONE.getDefaultState());
         this.chanceReplaceNonAir(world, box, random, 0.1F, 1, 0, 0, 5, 2, 0, Blocks.STONEBRICK.getDefaultState());
         this.chanceReplaceNonAir(world, box, random, 0.1F, 0, 0, 1, 0, 2, 5, Blocks.STONEBRICK.getDefaultState());
         this.chanceReplaceNonAir(world, box, random, 0.1F, 6, 0, 1, 6, 2, 5, Blocks.STONEBRICK.getDefaultState());
         this.chanceReplaceNonAir(world, box, random, 0.1F, 1, 0, 6, 5, 2, 6, Blocks.STONEBRICK.getDefaultState());
         this.fill(world, box, 2, 3, 0, 4, 3, 0, Blocks.STONE_SLAB.getDefaultState());
         this.fill(world, box, 0, 3, 2, 0, 3, 4, Blocks.STONE_SLAB.getDefaultState());
         this.fill(world, box, 6, 3, 2, 6, 3, 4, Blocks.STONE_SLAB.getDefaultState());
         this.fill(world, box, 2, 3, 6, 4, 3, 6, Blocks.STONE_SLAB.getDefaultState());
         this.chanceFill(world, box, random, 0.5F, 2, 3, 0, 4, 3, 0, Blocks.STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT, EnumType.COBBLESTONE));
         this.chanceFill(world, box, random, 0.5F, 0, 3, 2, 0, 3, 4, Blocks.STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT, EnumType.COBBLESTONE));
         this.chanceFill(world, box, random, 0.5F, 6, 3, 2, 6, 3, 4, Blocks.STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT, EnumType.COBBLESTONE));
         this.chanceFill(world, box, random, 0.5F, 2, 3, 6, 4, 3, 6, Blocks.STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT, EnumType.COBBLESTONE));
         this.fill(world, box, 2, 3, 1, 4, 3, 1, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.TOP));
         this.fill(world, box, 1, 3, 2, 1, 3, 4, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP));
         this.fill(world, box, 2, 3, 5, 4, 3, 5, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.TOP));
         this.fill(world, box, 5, 3, 2, 5, 3, 4, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP));
         this.setBlockState(world, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT).withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.FACING, EnumFacing.SOUTH), 1, 3, 1, box);
         this.setBlockState(world, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT).withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.FACING, EnumFacing.NORTH), 1, 3, 5, box);
         this.setBlockState(world, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT).withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.FACING, EnumFacing.SOUTH), 5, 3, 1, box);
         this.setBlockState(world, Blocks.STONE_STAIRS.getDefaultState().withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT).withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.FACING, EnumFacing.NORTH), 5, 3, 5, box);
         this.fill(world, box, 2, 4, 2, 4, 4, 4, Blocks.STONE_SLAB.getDefaultState());
         this.chanceFill(world, box, random, 0.5F, 2, 4, 2, 4, 4, 4, Blocks.STONE_SLAB.getDefaultState().withProperty(BlockStoneSlab.VARIANT, EnumType.COBBLESTONE));
         this.setBlockState(world, AIR, 3, 4, 3, box);
         this.setBlockState(world, Blocks.STONE_SLAB.getDefaultState().withProperty(BlockSlab.HALF, EnumBlockHalf.TOP), 3, 4, 3, box);
         this.fill(world, box, 1, 0, 1, 5, 0, 5, Blocks.STONE.getDefaultState());
         this.chanceFill(world, box, random, 0.4F, 1, 0, 1, 5, 0, 5, Blocks.COBBLESTONE.getDefaultState());
         this.chanceFill(world, box, random, 0.1F, 1, 0, 1, 5, 0, 5, Blocks.STONEBRICK.getDefaultState());
         this.fill(world, box, 1, 1, 1, 5, 2, 5, AIR);
         this.fill(world, box, 2, 3, 2, 4, 3, 4, AIR);
         this.fill(world, box, 3, 1, 0, 3, 2, 0, AIR);
         this.setBlockState(world, Blocks.IRON_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH).withProperty(BlockDoor.HALF, EnumDoorHalf.LOWER), 3, 1, 0, box);
         this.setBlockState(world, Blocks.IRON_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH).withProperty(BlockDoor.HALF, EnumDoorHalf.UPPER), 3, 2, 0, box);
         this.fill(world, box, 6, 2, 2, 6, 2, 4, Blocks.IRON_BARS.getDefaultState());
         this.setBlockState(world, Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockBed.PART, EnumPartType.FOOT), 1, 1, 4, box);
         this.setBlockState(world, Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockBed.PART, EnumPartType.HEAD), 1, 1, 5, box);
         TileEntity blockEntity = world.getTileEntity(new BlockPos(this.getXWithOffset(1, 4), this.getYWithOffset(1), this.getZWithOffset(1, 4)));
         if (blockEntity instanceof TileEntityBed) {
            ((TileEntityBed)blockEntity).setColor(EnumDyeColor.BLACK);
         }

         blockEntity = world.getTileEntity(new BlockPos(this.getXWithOffset(1, 5), this.getYWithOffset(1), this.getZWithOffset(1, 5)));
         if (blockEntity instanceof TileEntityBed) {
            ((TileEntityBed)blockEntity).setColor(EnumDyeColor.BLACK);
         }

         this.setBlockState(world, Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockBed.PART, EnumPartType.FOOT), 5, 1, 4, box);
         this.setBlockState(world, Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockBed.PART, EnumPartType.HEAD), 5, 1, 5, box);
         blockEntity = world.getTileEntity(new BlockPos(this.getXWithOffset(5, 4), this.getYWithOffset(1), this.getZWithOffset(5, 4)));
         if (blockEntity instanceof TileEntityBed) {
            ((TileEntityBed)blockEntity).setColor(EnumDyeColor.BLACK);
         }

         blockEntity = world.getTileEntity(new BlockPos(this.getXWithOffset(5, 5), this.getYWithOffset(1), this.getZWithOffset(5, 5)));
         if (blockEntity instanceof TileEntityBed) {
            ((TileEntityBed)blockEntity).setColor(EnumDyeColor.BLACK);
         }

         BlockPos spawnerPos = new BlockPos(this.getXWithOffset(3, 3), this.getYWithOffset(0), this.getZWithOffset(3, 3));
         world.setBlockState(spawnerPos, Blocks.MOB_SPAWNER.getDefaultState(), 2);
         blockEntity = world.getTileEntity(spawnerPos);
         if (blockEntity instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner)blockEntity).getSpawnerBaseLogic().setEntityId(new ResourceLocation("zombie_villager"));
         }

         this.setBlockState(world, Blocks.COBBLESTONE_WALL.getDefaultState(), 1, 1, 1, box);
         this.setBlockState(world, Blocks.REDSTONE_TORCH.getDefaultState(), 1, 2, 1, box);
         this.generateChest(world, box, random, 5, 1, 1, LootTableList.CHESTS_ABANDONED_MINESHAFT);
         this.setBlockState(world, Blocks.STONE_BUTTON.getDefaultState(), 2, 2, 1, box);
         this.setBlockState(world, Blocks.ANVIL.getDefaultState(), 5, 1, 2, box);
         if (random.nextFloat() < 0.33F) {
            this.setBlockState(world, Blocks.BOOKSHELF.getDefaultState(), 2, 1, 5, box);
         } else if (random.nextFloat() < 0.67F) {
            this.setBlockState(world, Blocks.CRAFTING_TABLE.getDefaultState(), 2, 1, 5, box);
         } else {
            this.setBlockState(world, Blocks.FURNACE.getDefaultState(), 2, 1, 5, box);
         }

         this.chanceFill(world, box, random, 0.3F, 2, 3, 2, 4, 3, 4, Blocks.WEB.getDefaultState());
         return true;
      } else {
         return false;
      }
   }
}
