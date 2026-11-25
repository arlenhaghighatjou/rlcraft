package shadows.placebo.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import shadows.placebo.Placebo;
import shadows.placebo.loot.PlaceboLootEntry;
import shadows.placebo.loot.PlaceboLootPool;
import shadows.placebo.loot.PlaceboLootSystem;

public class PlaceboDebug {
   public PlaceboDebug() {
   }

   public static void debug() {
      PlaceboLootPool.PoolBuilder build = new PlaceboLootPool.PoolBuilder(2.0F, 5.0F, 1.0F, 4.0F);
      build.addEntries(new PlaceboLootEntry(new ItemStack(Items.COAL), 2, 5, 1, 2));
      PlaceboLootSystem.registerLootTable(new ResourceLocation("placebo", "debug"), new LootTable(new LootPool[]{build.build()}));
   }

   public static void enableFastShapeless() {
      Placebo.LOG.info("Beginning replacement of all shapeless recipes...");
      Placebo.LOG.info("Expect log spam from FML!");
      List<IRecipe> fastRecipes = new ArrayList();

      for(IRecipe r : ForgeRegistries.RECIPES) {
         if (r.getClass() == ShapelessRecipes.class || r.getClass() == ShapelessOreRecipe.class) {
            FastShapelessRecipe res = new FastShapelessRecipe(r.getGroup(), r.getRecipeOutput(), r.getIngredients());
            res.setRegistryName(r.getRegistryName());
            fastRecipes.add(res);
         }
      }

      for(IRecipe r : fastRecipes) {
         ForgeRegistries.RECIPES.register(r);
      }

      Placebo.LOG.info("Successfully replaced {} recipes with fast recipes.", Integer.valueOf(fastRecipes.size()));
   }
}
