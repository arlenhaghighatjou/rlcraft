package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.block.BlockQuarkWall;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;

public class VanillaWalls extends Feature {
   public static boolean stone;
   public static boolean granite;
   public static boolean diorite;
   public static boolean andesite;
   public static boolean sandstone;
   public static boolean redSandstone;
   public static boolean stoneBricks;
   public static boolean bricks;
   public static boolean quartz;
   public static boolean prismarine;
   public static boolean prismarineBricks;
   public static boolean darkPrismarine;
   public static boolean purpurBlock;
   public static boolean endBricks;
   public static boolean mossBricks;

   public VanillaWalls() {
   }

   public void setupConfig() {
      stone = this.loadPropBool("Stone", "", true);
      granite = this.loadPropBool("Granite", "", true);
      diorite = this.loadPropBool("Diorite", "", true);
      andesite = this.loadPropBool("Andesite", "", true);
      sandstone = this.loadPropBool("Sandstone", "", true);
      redSandstone = this.loadPropBool("Red Sandstone", "", true);
      stoneBricks = this.loadPropBool("Stone Bricks", "", true);
      bricks = this.loadPropBool("Bricks", "", true);
      quartz = this.loadPropBool("Quartz", "", true);
      prismarine = this.loadPropBool("Prismarine", "", true);
      prismarineBricks = this.loadPropBool("Prismarine Bricks", "", true);
      darkPrismarine = this.loadPropBool("Dark Prismarine", "", true);
      purpurBlock = this.loadPropBool("Purpur", "", true);
      endBricks = this.loadPropBool("End Bricks", "", true);
      mossBricks = this.loadPropBool("Enable Mossy Bricks", "", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      if (GlobalConfig.enableVariants) {
         add("stone", Blocks.STONE, 0, stone);
         add("stone_granite", Blocks.STONE, 1, granite);
         add("stone_diorite", Blocks.STONE, 3, diorite);
         add("stone_andesite", Blocks.STONE, 5, andesite);
         add("sandstone", Blocks.SANDSTONE, 0, sandstone);
         add("red_sandstone", Blocks.RED_SANDSTONE, 0, redSandstone);
         add("stonebrick", Blocks.STONEBRICK, 0, stoneBricks);
         add("brick", Blocks.BRICK_BLOCK, 0, bricks);
         add("quartz", Blocks.QUARTZ_BLOCK, 0, quartz);
         add("prismarine_rough", Blocks.PRISMARINE, 0, prismarine);
         add("prismarine_bricks", Blocks.PRISMARINE, 1, prismarineBricks);
         add("dark_prismarine", Blocks.PRISMARINE, 2, darkPrismarine);
         add("purpur_block", Blocks.PURPUR_BLOCK, 0, purpurBlock);
         add("end_bricks", Blocks.END_BRICKS, 0, endBricks);
         add("stonebrick_mossy", Blocks.STONEBRICK, 1, mossBricks);
      }
   }

   public static void add(String name, Block block, int meta, boolean doit) {
      add(name, block, meta, doit, BlockQuarkWall::new);
   }

   public static void add(String name, Block block, int meta, boolean doit, VanillaWalls.WallSupplier supplier) {
      if (doit) {
         IBlockState state = block.getStateFromMeta(meta);
         String wallName = name + "_wall";
         BlockQuarkWall.initWall(block, meta, supplier.supply(wallName, state));
      }
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public interface WallSupplier {
      BlockQuarkWall supply(String var1, IBlockState var2);
   }
}
