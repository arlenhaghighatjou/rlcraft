package lykrast.defiledlands.common.init;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import lykrast.defiledlands.common.block.BlockStoneDefiledDecoration;
import lykrast.defiledlands.common.util.CorruptionRecipes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class ModRecipes {
   public ModRecipes() {
   }

   public static void initOreDict() {
      OreDictionary.registerOre("stoneDefiled", ModBlocks.stoneDefiled);
      OreDictionary.registerOre("stoneDefiled", new ItemStack(ModBlocks.stoneDefiledDecoration, 1, 32767));
      OreDictionary.registerOre("dirtDefiled", ModBlocks.dirtDefiled);
      OreDictionary.registerOre("sandDefiled", ModBlocks.sandDefiled);
      OreDictionary.registerOre("sandstoneDefiled", ModBlocks.sandstoneDefiled);
      OreDictionary.registerOre("gravelDefiled", ModBlocks.gravelDefiled);
      OreDictionary.registerOre("logWood", ModBlocks.tenebraLog);
      OreDictionary.registerOre("plankWood", ModBlocks.tenebraPlanks);
      OreDictionary.registerOre("treeLeaves", ModBlocks.tenebraLeaves);
      OreDictionary.registerOre("treeSapling", ModBlocks.tenebraSapling);
      OreDictionary.registerOre("oreHephaestite", ModBlocks.hephaestiteOre);
      OreDictionary.registerOre("blockHephaestite", ModBlocks.hephaestiteBlock);
      OreDictionary.registerOre("oreUmbrium", ModBlocks.umbriumOre);
      OreDictionary.registerOre("blockUmbrium", ModBlocks.umbriumBlock);
      OreDictionary.registerOre("oreScarlite", ModBlocks.scarliteOre);
      OreDictionary.registerOre("blockScarlite", ModBlocks.scarliteBlock);
      OreDictionary.registerOre("gemHephaestite", ModItems.hephaestite);
      OreDictionary.registerOre("ingotUmbrium", ModItems.umbriumIngot);
      OreDictionary.registerOre("nuggetUmbrium", ModItems.umbriumNugget);
      OreDictionary.registerOre("gemScarlite", ModItems.scarlite);
      OreDictionary.registerOre("essenceDestroyer", ModItems.essenceDestroyer);
      OreDictionary.registerOre("ingotRavaging", ModItems.ravagingIngot);
      OreDictionary.registerOre("essenceMourner", ModItems.essenceMourner);
      OreDictionary.registerOre("gemRemorseful", ModItems.remorsefulGem);
      OreDictionary.registerOre("listAllmeatraw", ModItems.bookWyrmRaw);
      OreDictionary.registerOre("listAllmeatcooked", ModItems.bookWyrmCooked);
      OreDictionary.registerOre("listAllmushroom", ModBlocks.scuronotte);
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, IRecipe.class, ModRecipes::registerRecipes);
   }

   public static void registerRecipes(Register<IRecipe> event) {
      CorruptionRecipes.init();
      GameRegistry.addSmelting(ModBlocks.hephaestiteOre, new ItemStack(ModItems.hephaestite, 2), 0.1F);
      GameRegistry.addSmelting(ModBlocks.umbriumOre, new ItemStack(ModItems.umbriumIngot), 0.7F);
      GameRegistry.addSmelting(ModBlocks.scarliteOre, new ItemStack(ModItems.scarlite), 1.0F);
      GameRegistry.addSmelting(ModItems.bookWyrmRaw, new ItemStack(ModItems.bookWyrmCooked), 0.35F);
      GameRegistry.addSmelting(ModItems.bookWyrmScaleGolden, new ItemStack(Items.GOLD_NUGGET, 3), 0.7F);
      GameRegistry.addSmelting(ModItems.scaleGoldenHelmet, new ItemStack(Items.GOLD_NUGGET, 15), 0.7F);
      GameRegistry.addSmelting(ModItems.scaleGoldenChestplate, new ItemStack(Items.GOLD_NUGGET, 24), 0.7F);
      GameRegistry.addSmelting(ModItems.scaleGoldenLeggings, new ItemStack(Items.GOLD_NUGGET, 21), 0.7F);
      GameRegistry.addSmelting(ModItems.scaleGoldenBoots, new ItemStack(Items.GOLD_NUGGET, 12), 0.7F);
      GameRegistry.addSmelting(ModItems.axeUmbrium, new ItemStack(ModItems.umbriumNugget), 0.1F);
      GameRegistry.addSmelting(ModItems.hoeUmbrium, new ItemStack(ModItems.umbriumNugget), 0.1F);
      GameRegistry.addSmelting(ModItems.pickaxeUmbrium, new ItemStack(ModItems.umbriumNugget), 0.1F);
      GameRegistry.addSmelting(ModItems.shovelUmbrium, new ItemStack(ModItems.umbriumNugget), 0.1F);
      GameRegistry.addSmelting(ModItems.swordUmbrium, new ItemStack(ModItems.umbriumNugget), 0.1F);
      GameRegistry.addSmelting(ModItems.umbriumHelmet, new ItemStack(ModItems.umbriumNugget), 0.1F);
      GameRegistry.addSmelting(ModItems.umbriumChestplate, new ItemStack(ModItems.umbriumNugget), 0.1F);
      GameRegistry.addSmelting(ModItems.umbriumLeggings, new ItemStack(ModItems.umbriumNugget), 0.1F);
      GameRegistry.addSmelting(ModItems.umbriumBoots, new ItemStack(ModItems.umbriumNugget), 0.1F);
      GameRegistry.addSmelting(new ItemStack(ModBlocks.stoneDefiledDecoration, 1, BlockStoneDefiledDecoration.Variants.BRICKS.ordinal()), new ItemStack(ModBlocks.stoneDefiledDecoration, 1, BlockStoneDefiledDecoration.Variants.BRICKS_CRACKED.ordinal()), 0.1F);
      GameRegistry.addSmelting(ModBlocks.sandDefiled, new ItemStack(ModBlocks.glassObscure), 0.1F);
   }
}
