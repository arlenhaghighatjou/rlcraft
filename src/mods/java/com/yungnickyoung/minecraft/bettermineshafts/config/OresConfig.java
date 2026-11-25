package com.yungnickyoung.minecraft.bettermineshafts.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public class OresConfig {
   @Name("Enable Ore Deposits")
   @RequiresWorldRestart
   public boolean enabled = true;
   @Name("Cobble Spawn Chance (Empty deposit)")
   @Comment({"Percent chance of an ore deposit being cobblestone only.\nDefault: 50"})
   @RangeInt(
      min = 0,
      max = 100
   )
   @RequiresWorldRestart
   public int cobble = 50;
   @Name("Coal Spawn Chance")
   @Comment({"Percent chance of an ore deposit containing coal.\nDefault: 20"})
   @RangeInt(
      min = 0,
      max = 100
   )
   @RequiresWorldRestart
   public int coal = 20;
   @Name("Iron Spawn Chance")
   @Comment({"Percent chance of an ore deposit containing iron.\nDefault: 9"})
   @RangeInt(
      min = 0,
      max = 100
   )
   @RequiresWorldRestart
   public int iron = 9;
   @Name("Redstone Spawn Chance")
   @Comment({"Percent chance of an ore deposit containing redstone.\nDefault: 7"})
   @RangeInt(
      min = 0,
      max = 100
   )
   @RequiresWorldRestart
   public int redstone = 7;
   @Name("Gold Spawn Chance")
   @Comment({"Percent chance of an ore deposit containing gold.\nDefault: 7"})
   @RangeInt(
      min = 0,
      max = 100
   )
   @RequiresWorldRestart
   public int gold = 7;
   @Name("Lapis Spawn Chance")
   @Comment({"Percent chance of an ore deposit containing lapis.\nDefault: 3"})
   @RangeInt(
      min = 0,
      max = 100
   )
   @RequiresWorldRestart
   public int lapis = 3;
   @Name("Emerald Spawn Chance")
   @Comment({"Percent chance of an ore deposit containing emerald.\nDefault: 3"})
   @RangeInt(
      min = 0,
      max = 100
   )
   @RequiresWorldRestart
   public int emerald = 3;
   @Name("Diamond Spawn Chance")
   @Comment({"Percent chance of an ore deposit containing diamond.\nDefault: 1"})
   @RangeInt(
      min = 0,
      max = 100
   )
   @RequiresWorldRestart
   public int diamond = 1;

   public OresConfig() {
   }
}
