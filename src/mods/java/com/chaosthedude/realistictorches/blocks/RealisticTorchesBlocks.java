package com.chaosthedude.realistictorches.blocks;

import com.chaosthedude.realistictorches.blocks.te.TEMovingLightSource;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RealisticTorchesBlocks {
   public static final List<Block> REGISTRY = new ArrayList();
   public static BlockTorchUnlit torchUnlit;
   public static BlockTorchLit torchLit;
   public static BlockTorchSmoldering torchSmoldering;
   public static BlockMovingLightSource movingLightSource;

   public RealisticTorchesBlocks() {
   }

   public static void register() {
      torchUnlit = (BlockTorchUnlit)registerBlock(new BlockTorchUnlit(), "torch_unlit");
      torchLit = (BlockTorchLit)registerBlock(new BlockTorchLit(), "torch_lit");
      torchSmoldering = (BlockTorchSmoldering)registerBlock(new BlockTorchSmoldering(), "torch_smoldering");
      movingLightSource = (BlockMovingLightSource)registerBlock(new BlockMovingLightSource(), "moving_light_source");
      GameRegistry.registerTileEntity(TEMovingLightSource.class, "te_moving_light_source");
   }

   protected static <T extends Block> T registerBlock(T blockType, String name) {
      blockType.setRegistryName(name);
      ForgeRegistries.BLOCKS.register(blockType);
      ForgeRegistries.ITEMS.register((new ItemBlock(blockType)).setRegistryName(name));
      REGISTRY.add(blockType);
      return blockType;
   }
}
