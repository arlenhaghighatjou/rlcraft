package com.fantasticsource.fantasticlib.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;

public class RaytraceConfig {
   @Name("1. Blockstate Filter")
   @LangKey("fantasticlib.config.rayBlockstateFilter")
   @Comment({"Syntax is...", "domain:name:meta, transparent", "", "Eg...", "minecraft:stone:0, false"})
   public String[] rayBlockstateFilter = new String[0];
   @Name("2. Block Filter")
   @LangKey("fantasticlib.config.rayBlockFilter")
   @Comment({"Syntax is...", "domain:name, transparent", "", "Eg...", "minecraft:glass, true"})
   public String[] rayBlockFilter = new String[0];
   @Name("3. Block Superclass Filter (Advanced)")
   @LangKey("fantasticlib.config.rayBlockSuperclassFilter")
   @Comment({"Syntax is...", "package.package.ClassName, transparent", "", "Eg...", "net.minecraft.block.BlockStairs, false"})
   public String[] rayBlockSuperclassFilter = new String[0];
   @Name("4. Material Filter")
   @LangKey("fantasticlib.config.rayMaterialFilter")
   @Comment({"Syntax is...", "material, transparent", "", "Eg...", "air, true", "", "Vanilla types:", "air, grass, ground, wood, rock, iron, anvil, water, lava, leaves, plants, vine, sponge, cloth, fire, sand, circuits, carpet, glass, redstone_light, tnt, coral, ice, packed_ice, snow, crafted_snow, cactus, clay, gourd, dragon_egg, portal, cake, web, piston, barrier, structure_void"})
   public String[] rayMaterialFilter = new String[0];

   public RaytraceConfig() {
   }
}
