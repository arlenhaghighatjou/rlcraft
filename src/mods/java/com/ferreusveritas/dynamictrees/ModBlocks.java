package com.ferreusveritas.dynamictrees;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.BlockBonsaiPot;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling;
import com.ferreusveritas.dynamictrees.blocks.BlockFruit;
import com.ferreusveritas.dynamictrees.blocks.BlockFruitCocoa;
import com.ferreusveritas.dynamictrees.blocks.BlockLeavesSnow;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.blocks.BlockRootyDirt;
import com.ferreusveritas.dynamictrees.blocks.BlockRootyDirtFake;
import com.ferreusveritas.dynamictrees.blocks.BlockRootySand;
import com.ferreusveritas.dynamictrees.blocks.BlockTrunkShell;
import com.ferreusveritas.dynamictrees.blocks.LeavesPaging;
import com.ferreusveritas.dynamictrees.blocks.LeavesProperties;
import com.ferreusveritas.dynamictrees.systems.DirtHelper;
import com.ferreusveritas.dynamictrees.util.LazyValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@EventBusSubscriber(
   modid = "dynamictrees"
)
public class ModBlocks {
   public static BlockRooty blockRootyDirt;
   public static BlockRooty blockRootySand;
   public static BlockRooty blockRootyDirtSpecies;
   public static Block blockRootyDirtFake;
   public static BlockDynamicSapling blockDynamicSapling;
   public static BlockFruit blockApple;
   public static BlockFruitCocoa blockFruitCocoa;
   public static BlockBonsaiPot blockBonsaiPot;
   public static BlockTrunkShell blockTrunkShell;
   public static LazyValue<BlockSnow> blockLeavesSnow;
   public static Map<String, ILeavesProperties> leaves = new HashMap();
   public static ModBlocks.CommonBlockStates blockStates;

   public ModBlocks() {
   }

   public static void preInit() {
      BlockDynamicLeaves.passableLeavesModLoaded = Loader.isModLoaded("passableleaves");
      blockRootyDirt = new BlockRootyDirt(false);
      blockRootySand = new BlockRootySand(false);
      blockRootyDirtSpecies = new BlockRootyDirt(true);
      blockRootyDirtFake = new BlockRootyDirtFake("rootydirtfake");
      blockDynamicSapling = new BlockDynamicSapling("sapling");
      blockBonsaiPot = new BlockBonsaiPot();
      blockFruitCocoa = new BlockFruitCocoa();
      blockApple = (new BlockFruit()).setDroppedItem(new ItemStack(Items.APPLE));
      blockTrunkShell = new BlockTrunkShell();
      blockLeavesSnow = ModConfigs.enableAltLeavesSnow ? LazyValue.of(new BlockLeavesSnow()) : LazyValue.supplied(() -> {
         return (BlockSnow)Block.getBlockFromName("snow_layer");
      });
      blockStates = new ModBlocks.CommonBlockStates();
      setupLeavesProperties();
   }

   public static void setupLeavesProperties() {
      leaves = LeavesPaging.build(new ResourceLocation("dynamictrees", "leaves/common.json"));
      leaves.put("cactus", new LeavesProperties((IBlockState)null, ItemStack.EMPTY, TreeRegistry.findCellKit("bare")));
   }

   @SubscribeEvent
   public static void register(Register<Block> event) {
      IForgeRegistry<Block> registry = event.getRegistry();
      ArrayList<Block> treeBlocks = new ArrayList();
      ModTrees.baseFamilies.forEach((tree) -> {
         tree.getRegisterableBlocks(treeBlocks);
      });
      ModTrees.dynamicCactus.getRegisterableBlocks(treeBlocks);
      treeBlocks.addAll(LeavesPaging.getLeavesMapForModId("dynamictrees").values());
      registry.registerAll(new Block[]{blockRootyDirt, blockRootySand, blockRootyDirtSpecies, blockRootyDirtFake, blockDynamicSapling, blockBonsaiPot, blockFruitCocoa, blockApple, blockTrunkShell});
      if (ModConfigs.enableAltLeavesSnow) {
         registry.register((IForgeRegistryEntry)blockLeavesSnow.get());
      }

      registry.registerAll((IForgeRegistryEntry[])treeBlocks.toArray(new Block[0]));
      DirtHelper.registerSoil(Blocks.DIRT, "dirtlike");
      DirtHelper.registerSoil(Blocks.GRASS, "dirtlike");
      DirtHelper.registerSoil(Blocks.MYCELIUM, "dirtlike");
      DirtHelper.registerSoil(blockRootyDirt, "dirtlike");
      DirtHelper.registerSoil(blockRootyDirtFake, "dirtlike");
      DirtHelper.registerSoil(blockRootyDirtSpecies, "dirtlike");
      DirtHelper.registerSoil(Blocks.SAND, "sandlike");
      DirtHelper.registerSoil(blockRootySand, "sandlike");
      DirtHelper.registerSoil(Blocks.GRAVEL, "gravellike");
      DirtHelper.registerSoil(Blocks.WATER, "waterlike");
      DirtHelper.registerSoil(Blocks.HARDENED_CLAY, "hardclaylike");
      DirtHelper.registerSoil(Blocks.STAINED_HARDENED_CLAY, "hardclaylike");
      DirtHelper.registerSoil(Blocks.MYCELIUM, "funguslike");
      DirtHelper.registerSoil(Blocks.NETHERRACK, "netherlike");
      DirtHelper.registerSoil(Blocks.SOUL_SAND, "netherlike");
   }

   public static class CommonBlockStates {
      public final IBlockState air = Blocks.AIR.getDefaultState();
      public final IBlockState dirt = Blocks.DIRT.getDefaultState();
      public final IBlockState sand = Blocks.SAND.getDefaultState();
      public final IBlockState grass = Blocks.GRASS.getDefaultState();
      public final IBlockState podzol;
      public final IBlockState redMushroom;
      public final IBlockState brownMushroom;
      public final LazyValue<IBlockState> snowLayer;

      public CommonBlockStates() {
         this.podzol = this.dirt.withProperty(BlockDirt.VARIANT, DirtType.PODZOL);
         this.redMushroom = Blocks.RED_MUSHROOM.getDefaultState();
         this.brownMushroom = Blocks.BROWN_MUSHROOM.getDefaultState();
         this.snowLayer = LazyValue.<IBlockState>supplied(() -> {
            return ((BlockSnow)ModBlocks.blockLeavesSnow.get()).getDefaultState();
         });
      }
   }
}
