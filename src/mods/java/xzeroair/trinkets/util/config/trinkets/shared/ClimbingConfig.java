package xzeroair.trinkets.util.config.trinkets.shared;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;

public class ClimbingConfig {
   private static final String PREFIX = "xat.config.abilities";
   @Comment({"Climbing Ability. Set to False to Disable. Default True"})
   @Name("Climbing Enabled")
   @LangKey("xat.config.abilities.climbing")
   public boolean climbing = true;
   @Name("Climable Blocks")
   @LangKey("xat.config.abilities.climbing.blocks.list")
   public String[] allowedBlocks = new String[]{"minecraft:dirt", "minecraft:grass", "minecraft:sand", "minecraft:cobblestone", "minecraft:mossy_cobblestone", "minecraft:oak_stairs", "minecraft:stone_stairs", "minecraft:brick_stairs", "minecraft:stone_brick_stairs", "minecraft:nether_brick_stairs", "minecraft:sandstone_stairs", "minecraft:spruce_stairs", "minecraft:birch_stairs", "minecraft:jungle_stairs", "minecraft:arcacia_stairs", "minecraft:quartz_stairs", "minecraft:dark_oak_stairs", "minecraft:red_sandstone_stairs", "minecraft:purpur_stairs", "minecraft:stone_slab", "minecraft:wooden_slab", "minecraft:stone_slab2", "minecraft:purpur_slab"};

   public ClimbingConfig() {
   }
}
