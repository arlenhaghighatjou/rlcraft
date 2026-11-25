package com.yungnickyoung.minecraft.bettermineshafts.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public class SpawnRatesConfig {
   @Name("Lantern Spawn Rate")
   @RequiresWorldRestart
   @Comment({"The spawn rate for lanterns in the main shaft.\nDefault: .0067"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public float lanternSpawnRate = 0.0067F;
   @Name("Torch Spawn Rate")
   @RequiresWorldRestart
   @Comment({"The spawn rate for torches in small shafts.\nDefault: .02"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public float torchSpawnRate = 0.02F;
   @Name("Workstation Spawn Rate")
   @RequiresWorldRestart
   @Comment({"The spawn rate for workstation side rooms along the main shaft.\nDefault: .025"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public float workstationSpawnRate = 0.025F;
   @Name("Workstation Cellar Spawn Rate")
   @RequiresWorldRestart
   @Comment({"The spawn rate for workstation cellars below workstations along the main shaft.\nDefault: .25"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public float workstationDungeonSpawnRate = 0.25F;
   @Name("Small Shaft Spawn Rate")
   @RequiresWorldRestart
   @Comment({"The spawn rate for smaller tunnels that generate along the main shaft.\nDefault: .07"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public float smallShaftSpawnRate = 0.07F;
   @Name("Cobweb Spawn Rate")
   @RequiresWorldRestart
   @Comment({"The spawn rate for cobwebs.\nDefault: .15"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public float cobwebSpawnRate = 0.15F;
   @Name("Small Shaft Chest Minecart Spawn Rate")
   @RequiresWorldRestart
   @Comment({"The spawn rate for minecarts holding chests in small shafts.\nDefault: .00125"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public float smallShaftChestMinecartSpawnRate = 0.00125F;
   @Name("Main Shaft Chest Minecart Spawn Rate")
   @RequiresWorldRestart
   @Comment({"The spawn rate for minecarts holding chests in the main shaft.\nDefault: .01"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public float mainShaftChestMinecartSpawnRate = 0.01F;
   @Name("Small Shaft TNT Minecart Spawn Rate")
   @RequiresWorldRestart
   @Comment({"The spawn rate for minecarts holding TNT in small shafts.\nDefault: .0025"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public float smallShaftTntMinecartSpawnRate = 0.0025F;
   @Name("Main Shaft TNT Minecart Spawn Rate")
   @RequiresWorldRestart
   @Comment({"The spawn rate for minecarts holding TNT in the main shaft.\nDefault: .0025"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public float mainShaftTntMinecartSpawnRate = 0.0025F;
   @Name("Abandoned Miners' Outpost Spawn Chance")
   @Comment({"Percent chance of an Abandoned Miners' Outpost to spawn at the end of a small mineshaft tunnel.\nDefault: 2"})
   @RequiresWorldRestart
   @RangeInt(
      min = 0,
      max = 100
   )
   public int zombieVillagerRoomSpawnRate = 2;
   @Name("Small Shaft Piece Chain Length")
   @Comment({"The number of \"pieces\" (e.g. straight, turn, ladder, intersection, etc.) in a single small shaft.\nThis determines the overall length of small shafts.\nDefault: 9"})
   @RequiresWorldRestart
   @RangeInt(
      min = 0
   )
   public int smallShaftPieceChainLength = 9;

   public SpawnRatesConfig() {
   }
}
