package vazkii.quark.building.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.block.slab.BlockVanillaSlab;

public class VanillaStairsAndSlabs extends Feature {
   public static boolean stone;
   public static boolean granite;
   public static boolean diorite;
   public static boolean andesite;
   public static boolean endBricks;
   public static boolean prismarine;
   public static boolean prismarineBricks;
   public static boolean darkPrismarine;
   public static boolean redNetherBricks;
   public static boolean mossStone;
   public static boolean mossBricks;

   public VanillaStairsAndSlabs() {
   }

   public void setupConfig() {
      stone = this.loadPropBool("Stone", "", true);
      granite = this.loadPropBool("Granite", "", true);
      diorite = this.loadPropBool("Diorite", "", true);
      andesite = this.loadPropBool("Andesite", "", true);
      endBricks = this.loadPropBool("End Bricks", "", true);
      prismarine = this.loadPropBool("Prismarine", "", true);
      prismarineBricks = this.loadPropBool("Prismarine Bricks", "", true);
      darkPrismarine = this.loadPropBool("Dark Prismarine", "", true);
      redNetherBricks = this.loadPropBool("Red Nether Brick", "", true);
      mossStone = this.loadPropBool("Enable Mossy Stone", "", true);
      mossBricks = this.loadPropBool("Enable Mossy Bricks", "", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      if (GlobalConfig.enableVariants) {
         this.add("stone", Blocks.STONE, 0, false, true, stone);
         this.add("stone_granite", Blocks.STONE, 1, granite);
         this.add("stone_diorite", Blocks.STONE, 3, diorite);
         this.add("stone_andesite", Blocks.STONE, 5, andesite);
         this.add("end_bricks", Blocks.END_BRICKS, 0, endBricks);
         this.add("prismarine", Blocks.PRISMARINE, 0, prismarine);
         this.add("prismarine_bricks", Blocks.PRISMARINE, 1, prismarineBricks);
         this.add("prismarine_dark", Blocks.PRISMARINE, 2, darkPrismarine);
         this.add("red_nether_brick", Blocks.RED_NETHER_BRICK, 0, redNetherBricks);
         this.add("cobblestone_mossy", Blocks.MOSSY_COBBLESTONE, 0, mossStone);
         this.add("stonebrick_mossy", Blocks.STONEBRICK, 1, mossBricks);
      }
   }

   public void add(String name, Block block, int meta, boolean doit) {
      this.add(name, block, meta, true, true, doit);
   }

   public void add(String name, Block block, int meta, boolean slab, boolean stairs, boolean doit) {
      if (doit) {
         IBlockState state = block.getStateFromMeta(meta);
         String stairsName = name + "_stairs";
         String slabName = name + "_slab";
         if (stairs) {
            BlockModStairs.initStairs(block, meta, new BlockQuarkStairs(stairsName, state));
         }

         if (slab) {
            BlockModSlab.initSlab(block, meta, new BlockVanillaSlab(slabName, state, false), new BlockVanillaSlab(slabName, state, true));
         }

      }
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
