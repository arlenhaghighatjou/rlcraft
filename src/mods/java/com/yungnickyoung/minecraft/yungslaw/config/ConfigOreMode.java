package com.yungnickyoung.minecraft.yungslaw.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public class ConfigOreMode {
   @Name("Ore Block Whitelist")
   @Comment({"Whitelist for designating blocks as ore blocks for Ore Deletion Mode. Useful if some modded ores you're using aren't already supported, \nor if you perhaps only want to remove some of a mod's ores, but not all."})
   @RequiresWorldRestart
   public String[] oreWhitelist = new String[]{"minecraft:coal_ore", "minecraft:iron_ore", "minecraft:gold_ore", "minecraft:lapis_ore", "minecraft:redstone_ore", "minecraft:emerald_ore", "minecraft:diamond_ore", "minecraft:quartz_ore"};

   public ConfigOreMode() {
   }
}
