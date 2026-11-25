package com.yungnickyoung.minecraft.bettermineshafts.world.generator;

import com.yungnickyoung.minecraft.bettermineshafts.config.Configuration;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.BigTunnel;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.LayeredIntersection4;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.LayeredIntersection5;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.MineshaftPiece;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.OreDeposit;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SideRoom;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SideRoomDungeon;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SmallTunnel;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SmallTunnelStairs;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.SmallTunnelTurn;
import com.yungnickyoung.minecraft.bettermineshafts.world.generator.pieces.ZombieVillagerRoom;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class BetterMineshaftGenerator {
   public BetterMineshaftGenerator() {
   }

   public static MineshaftPiece generateAndAddBigTunnelPiece(StructureComponent structurePiece, List<StructureComponent> list, EaglercraftRandom random, int x, int y, int z, EnumFacing direction, int l, int pieceChainLen) {
      if (pieceChainLen > 3) {
         return null;
      } else {
         int rand = random.nextInt(100);
         MineshaftVariantSettings settings = ((MineshaftPiece)structurePiece).getSettings();
         if (rand < 10 && pieceChainLen >= 1) {
            return null;
         } else {
            StructureBoundingBox blockBox = BigTunnel.determineBoxPosition(x, y, z, direction);
            MineshaftPiece newPiece = new BigTunnel(l + 1, pieceChainLen + 1, random, blockBox, direction, settings);
            list.add(newPiece);
            newPiece.buildComponent(structurePiece, list, random);
            return newPiece;
         }
      }
   }

   public static MineshaftPiece generateAndAddSmallTunnelPiece(StructureComponent structurePiece, List<StructureComponent> list, EaglercraftRandom random, int x, int y, int z, EnumFacing direction, int l, int pieceChainLen) {
      int rand = random.nextInt(100);
      MineshaftVariantSettings settings = ((MineshaftPiece)structurePiece).getSettings();
      if (pieceChainLen > Configuration.spawnRates.smallShaftPieceChainLength - 2) {
         if (rand < Configuration.spawnRates.zombieVillagerRoomSpawnRate) {
            if (direction == EnumFacing.NORTH) {
               --x;
            } else if (direction == EnumFacing.EAST) {
               --z;
            } else if (direction == EnumFacing.SOUTH) {
               ++x;
            } else if (direction == EnumFacing.WEST) {
               ++z;
            }

            StructureBoundingBox blockBox = ZombieVillagerRoom.determineBoxPosition(list, random, x, y, z, direction);
            if (blockBox != null) {
               MineshaftPiece newPiece = new ZombieVillagerRoom(l + 1, pieceChainLen + 1, random, blockBox, direction, settings);
               list.add(newPiece);
               newPiece.buildComponent(structurePiece, list, random);
               return newPiece;
            }
         } else {
            if (!Configuration.ores.enabled) {
               return null;
            }

            StructureBoundingBox blockBox = OreDeposit.determineBoxPosition(list, random, x, y, z, direction);
            if (blockBox != null) {
               MineshaftPiece newPiece = new OreDeposit(l + 1, pieceChainLen + 1, random, blockBox, direction, settings);
               list.add(newPiece);
               newPiece.buildComponent(structurePiece, list, random);
               return newPiece;
            }
         }

         return null;
      } else {
         if (rand >= 90 && pieceChainLen > 2 && pieceChainLen < Configuration.spawnRates.smallShaftPieceChainLength - 2) {
            StructureBoundingBox blockBox = LayeredIntersection4.determineBoxPosition(list, random, x, y, z, direction);
            if (blockBox != null) {
               MineshaftPiece newPiece = new LayeredIntersection4(l + 1, pieceChainLen + 1, random, blockBox, direction, settings);
               list.add(newPiece);
               newPiece.buildComponent(structurePiece, list, random);
               return newPiece;
            }
         } else if (rand >= 80 && pieceChainLen < Configuration.spawnRates.smallShaftPieceChainLength - 2) {
            StructureBoundingBox blockBox = SmallTunnelStairs.determineBoxPosition(list, random, x, y, z, direction);
            if (blockBox != null) {
               MineshaftPiece newPiece = new SmallTunnelStairs(l + 1, pieceChainLen + 1, random, blockBox, direction, settings);
               list.add(newPiece);
               newPiece.buildComponent(structurePiece, list, random);
               return newPiece;
            }
         } else if (rand >= 70 && pieceChainLen > 2) {
            StructureBoundingBox blockBox = SmallTunnelTurn.determineBoxPosition(list, random, x, y, z, direction);
            if (blockBox != null) {
               MineshaftPiece newPiece = new SmallTunnelTurn(l + 1, pieceChainLen + 1, random, blockBox, direction, settings);
               list.add(newPiece);
               newPiece.buildComponent(structurePiece, list, random);
               return newPiece;
            }
         } else if (rand >= 60 && pieceChainLen > 2 && pieceChainLen < Configuration.spawnRates.smallShaftPieceChainLength - 2) {
            StructureBoundingBox blockBox = LayeredIntersection5.determineBoxPosition(list, random, x, y, z, direction);
            if (blockBox != null) {
               MineshaftPiece newPiece = new LayeredIntersection5(l + 1, pieceChainLen + 1, random, blockBox, direction, settings);
               list.add(newPiece);
               newPiece.buildComponent(structurePiece, list, random);
               return newPiece;
            }
         } else {
            StructureBoundingBox blockBox = SmallTunnel.determineBoxPosition(list, random, x, y, z, direction);
            if (blockBox != null) {
               MineshaftPiece newPiece = new SmallTunnel(l + 1, pieceChainLen + 1, random, blockBox, direction, settings);
               list.add(newPiece);
               newPiece.buildComponent(structurePiece, list, random);
               return newPiece;
            }
         }

         return null;
      }
   }

   public static MineshaftPiece generateAndAddSideRoomPiece(StructureComponent structurePiece, List<StructureComponent> list, EaglercraftRandom random, int x, int y, int z, EnumFacing direction, int l, int pieceChainLen) {
      MineshaftVariantSettings settings = ((MineshaftPiece)structurePiece).getSettings();
      StructureBoundingBox blockBox = SideRoom.determineBoxPosition(list, random, x, y, z, direction);
      if (blockBox != null) {
         MineshaftPiece newPiece = new SideRoom(l + 1, pieceChainLen + 1, random, blockBox, direction, settings);
         list.add(newPiece);
         newPiece.buildComponent(structurePiece, list, random);
         return newPiece;
      } else {
         return null;
      }
   }

   public static MineshaftPiece generateAndAddSideRoomDungeonPiece(StructureComponent structurePiece, List<StructureComponent> list, EaglercraftRandom random, int x, int y, int z, EnumFacing direction, int l, int pieceChainLen) {
      MineshaftVariantSettings settings = ((MineshaftPiece)structurePiece).getSettings();
      StructureBoundingBox blockBox = SideRoomDungeon.determineBoxPosition(list, random, x, y, z, direction);
      if (blockBox != null) {
         MineshaftPiece newPiece = new SideRoomDungeon(l + 1, pieceChainLen + 1, random, blockBox, direction, settings);
         list.add(newPiece);
         newPiece.buildComponent(structurePiece, list, random);
         return newPiece;
      } else {
         return null;
      }
   }
}
