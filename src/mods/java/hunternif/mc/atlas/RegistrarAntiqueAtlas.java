package hunternif.mc.atlas;

import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.collect.UnmodifiableIterator;
import hunternif.mc.atlas.item.ItemAtlas;
import hunternif.mc.atlas.item.ItemEmptyAtlas;
import hunternif.mc.atlas.item.RecipeAtlasCloning;
import hunternif.mc.atlas.item.RecipeAtlasCombining;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.MissingMappings;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RegistrarAntiqueAtlas {
   public static ItemEmptyAtlas EMPTY_ATLAS = new ItemEmptyAtlas();
   public static ItemAtlas ATLAS = new ItemAtlas();

   public RegistrarAntiqueAtlas() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Item.class, RegistrarAntiqueAtlas::registerItems);
      bus.registerGeneric(Register.class, IRecipe.class, RegistrarAntiqueAtlas::registerRecipes);
      bus.register(ModelRegistryEvent.class, RegistrarAntiqueAtlas::registerModels);
      bus.registerGeneric(MissingMappings.class, Item.class, RegistrarAntiqueAtlas::handleMissingMapping);
   }

   public static void registerItems(Register<Item> event) {
      if (SettingsConfig.gameplay.itemNeeded) {
         event.getRegistry().register((Item)(new ItemEmptyAtlas()).setRegistryName("empty_antique_atlas"));
         event.getRegistry().register((Item)(new ItemAtlas()).setRegistryName("antique_atlas"));
      }

   }

   public static void registerRecipes(Register<IRecipe> event) {
      if (SettingsConfig.gameplay.itemNeeded) {
         event.getRegistry().register((IRecipe)(new ShapelessOreRecipe(new ResourceLocation("antiqueatlas", "atlas"), new ItemStack(EMPTY_ATLAS), new Object[]{Items.BOOK, Items.COMPASS})).setRegistryName("atlas_blank"));
         event.getRegistry().register((IRecipe)(new RecipeAtlasCloning()).setRegistryName("atlas_clone"));
         event.getRegistry().register((IRecipe)(new RecipeAtlasCombining()).setRegistryName("atlas_combine"));
      }

   }

   public static void registerModels(ModelRegistryEvent event) {
      if (SettingsConfig.gameplay.itemNeeded) {
         ModelLoader.setCustomModelResourceLocation(EMPTY_ATLAS, 0, new ModelResourceLocation(EMPTY_ATLAS.getRegistryName(), "inventory"));
         ModelLoader.setCustomMeshDefinition(ATLAS, (stack) -> {
            return new ModelResourceLocation(ATLAS.getRegistryName(), "inventory");
         });
      }

   }

   public static void handleMissingMapping(MissingMappings<Item> event) {
      UnmodifiableIterator var1 = event.getMappings().iterator();

      while(var1.hasNext()) {
         Mapping<Item> mapping = (Mapping)var1.next();
         if (mapping.key.getResourcePath().equalsIgnoreCase("antiqueatlas")) {
            mapping.remap(ATLAS);
         } else if (mapping.key.getResourcePath().equalsIgnoreCase("emptyantiqueatlas")) {
            mapping.remap(EMPTY_ATLAS);
         }
      }

   }

   public static void registerHolders() {
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("antiqueatlas:empty_antique_atlas"), v -> EMPTY_ATLAS = (ItemEmptyAtlas)v);
      ObjectHolderRegistry.INSTANCE.register(Item.class, new ResourceLocation("antiqueatlas:antique_atlas"), v -> ATLAS = (ItemAtlas)v);
   }
}
