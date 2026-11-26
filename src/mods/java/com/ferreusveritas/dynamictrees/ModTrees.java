package com.ferreusveritas.dynamictrees;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.systems.DirtHelper;
import com.ferreusveritas.dynamictrees.trees.Mushroom;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeAcacia;
import com.ferreusveritas.dynamictrees.trees.TreeBirch;
import com.ferreusveritas.dynamictrees.trees.TreeCactus;
import com.ferreusveritas.dynamictrees.trees.TreeDarkOak;
import com.ferreusveritas.dynamictrees.trees.TreeFamilyVanilla;
import com.ferreusveritas.dynamictrees.trees.TreeJungle;
import com.ferreusveritas.dynamictrees.trees.TreeOak;
import com.ferreusveritas.dynamictrees.trees.TreeSpruce;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.NewRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class ModTrees {
   public static final String NULL = "null";
   public static final String OAK = "oak";
   public static final String BIRCH = "birch";
   public static final String SPRUCE = "spruce";
   public static final String JUNGLE = "jungle";
   public static final String DARKOAK = "darkoak";
   public static final String ACACIA = "acacia";
   public static final String CONIFER = "conifer";
   public static ArrayList<TreeFamilyVanilla> baseFamilies = new ArrayList();
   public static TreeCactus dynamicCactus;

   public ModTrees() {
   }

   public static void preInit() {
      Species.REGISTRY.register(Species.NULLSPECIES.setRegistryName(new ResourceLocation("dynamictrees", "null")));
      Collections.addAll(baseFamilies, new TreeFamilyVanilla[]{new TreeOak(), new TreeSpruce(), new TreeBirch(), new TreeJungle(), new TreeAcacia(), new TreeDarkOak()});
      baseFamilies.forEach((tree) -> {
         tree.registerSpecies(Species.REGISTRY);
      });
      dynamicCactus = new TreeCactus();
      dynamicCactus.registerSpecies(Species.REGISTRY);
      Species.REGISTRY.register(new Mushroom(true));
      Species.REGISTRY.register(new Mushroom(false));

      for(TreeFamilyVanilla vanillaFamily : baseFamilies) {
         IBlockState defaultSaplingState = Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, vanillaFamily.woodType);
         TreeRegistry.registerSaplingReplacer(defaultSaplingState.withProperty(BlockSapling.STAGE, Integer.valueOf(0)), vanillaFamily.getCommonSpecies());
         TreeRegistry.registerSaplingReplacer(defaultSaplingState.withProperty(BlockSapling.STAGE, Integer.valueOf(1)), vanillaFamily.getCommonSpecies());
      }

   }

   public static void register(EventBus bus) {
      bus.register(NewRegistry.class, ModTrees::newRegistry);
   }

   public static void newRegistry(NewRegistry event) {
      Species.newRegistry(event);
   }

   public static void setupExtraSoils() {
      for(ItemStack entry : OreDictionary.getOres("sand")) {
         Item item = entry.getItem();
         if (item instanceof ItemBlock) {
            DirtHelper.registerSoil(((ItemBlock)item).getBlock(), "sandlike");
         }
      }

   }
}
