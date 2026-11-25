package com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces;

import com.yungnickyoung.minecraft.bettermineshafts.world.generator.MineshaftVariantSettings;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.storage.loot.LootTableList;

public class SideRoomDungeon extends MineshaftPiece {
   private static final int SECONDARY_AXIS_LEN = 9;
   private static final int Y_AXIS_LEN = 4;
   private static final int MAIN_AXIS_LEN = 9;
   private static final int LOCAL_X_END = 8;
   private static final int LOCAL_Y_END = 3;
   private static final int LOCAL_Z_END = 8;

   public SideRoomDungeon() {
   }

   public SideRoomDungeon(int i, int pieceChainLen, EaglercraftRandom random, StructureBoundingBox blockBox, EnumFacing direction, MineshaftVariantSettings settings) {
      super(i, pieceChainLen, settings);
      this.setCoordBaseMode(direction);
      this.boundingBox = blockBox;
   }

   public static StructureBoundingBox determineBoxPosition(List<StructureComponent> list, EaglercraftRandom random, int x, int y, int z, EnumFacing direction) {
      StructureBoundingBox blockBox = new StructureBoundingBox(x, y, z, x, y + 4 - 1, z);
      switch(direction) {
      case NORTH:
      default:
         blockBox.maxX = x + 4;
         blockBox.minX = x - 4;
         blockBox.minZ = z - 8;
         break;
      case SOUTH:
         blockBox.maxX = x + 4;
         blockBox.minX = x - 4;
         blockBox.maxZ = z + 8;
         break;
      case WEST:
         blockBox.minX = x - 8;
         blockBox.maxZ = z + 4;
         blockBox.minZ = z - 4;
         break;
      case EAST:
         blockBox.maxX = x + 8;
         blockBox.maxZ = z + 4;
         blockBox.minZ = z - 4;
      }

      StructureComponent intersectingPiece = StructureComponent.findIntersecting(list, blockBox);
      return intersectingPiece != null ? null : blockBox;
   }

   public void buildComponent(StructureComponent structurePiece, List<StructureComponent> list, EaglercraftRandom random) {
   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, EaglercraftRandom random, StructureBoundingBox box) {
      if (this.isLiquidInStructureBoundingBox(world, box)) {
         return false;
      } else if (!this.isInOcean(world, 0, 0) && !this.isInOcean(world, 8, 8)) {
         this.fill(world, box, random, 0, 0, 0, 8, 3, 8, this.getBrickSelector());
         this.fill(world, box, 1, 1, 1, 7, 2, 7, AIR);
         this.generateLegs(world, box, random);
         IBlockState LADDER = Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.NORTH);
         this.fill(world, box, 4, 1, 1, 4, 3, 1, LADDER);
         BlockPos spawnerPos = new BlockPos(this.getXWithOffset(4, 5), this.getYWithOffset(1), this.getZWithOffset(4, 5));
         world.setBlockState(spawnerPos, Blocks.MOB_SPAWNER.getDefaultState(), 2);
         TileEntity blockEntity = world.getTileEntity(spawnerPos);
         if (blockEntity instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner)blockEntity).getSpawnerBaseLogic().setEntityId(new ResourceLocation("cave_spider"));
         }

         this.chanceReplaceAir(world, box, random, 0.9F, 3, 1, 4, 5, 2, 6, Blocks.WEB.getDefaultState());
         this.chanceReplaceAir(world, box, random, 0.1F, 1, 1, 1, 7, 2, 8, Blocks.WEB.getDefaultState());
         this.generateChest(world, box, random, 1, 1, 7, LootTableList.CHESTS_ABANDONED_MINESHAFT);
         if (random.nextInt(2) == 0) {
            this.generateChest(world, box, random, 7, 1, 7, LootTableList.CHESTS_ABANDONED_MINESHAFT);
         }

         this.addBiomeDecorations(world, box, random, 0, 0, 0, 8, 2, 8);
         return true;
      } else {
         return false;
      }
   }

   private void generateLegs(World world, StructureBoundingBox box, EaglercraftRandom random) {
      this.generateLeg(world, random, box, 1, 1, this.getBrickSelector());
      this.generateLeg(world, random, box, 1, 7, this.getBrickSelector());
      this.generateLeg(world, random, box, 7, 1, this.getBrickSelector());
      this.generateLeg(world, random, box, 7, 7, this.getBrickSelector());
   }
}
