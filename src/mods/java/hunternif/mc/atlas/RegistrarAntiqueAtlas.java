package hunternif.mc.atlas;

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
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@EventBusSubscriber(
   modid = "antiqueatlas"
)
@ObjectHolder("antiqueatlas")
public class RegistrarAntiqueAtlas {
   @ObjectHolder("empty_antique_atlas")
   public static final ItemEmptyAtlas EMPTY_ATLAS = new ItemEmptyAtlas();
   @ObjectHolder("antique_atlas")
   public static final ItemAtlas ATLAS = new ItemAtlas();

   public RegistrarAntiqueAtlas() {
   }

   @SubscribeEvent
   public static void registerItems(Register<Item> event) {
      if (SettingsConfig.gameplay.itemNeeded) {
         event.getRegistry().register((Item)(new ItemEmptyAtlas()).setRegistryName("empty_antique_atlas"));
         event.getRegistry().register((Item)(new ItemAtlas()).setRegistryName("antique_atlas"));
      }

   }

   @SubscribeEvent
   public static void registerRecipes(Register<IRecipe> event) {
      if (SettingsConfig.gameplay.itemNeeded) {
         event.getRegistry().register((IRecipe)(new ShapelessOreRecipe(new ResourceLocation("antiqueatlas", "atlas"), new ItemStack(EMPTY_ATLAS), new Object[]{Items.BOOK, Items.COMPASS})).setRegistryName("atlas_blank"));
         event.getRegistry().register((IRecipe)(new RecipeAtlasCloning()).setRegistryName("atlas_clone"));
         event.getRegistry().register((IRecipe)(new RecipeAtlasCombining()).setRegistryName("atlas_combine"));
      }

   }

   @SubscribeEvent
   public static void registerModels(ModelRegistryEvent event) {
      if (SettingsConfig.gameplay.itemNeeded) {
         ModelLoader.setCustomModelResourceLocation(EMPTY_ATLAS, 0, new ModelResourceLocation(EMPTY_ATLAS.getRegistryName(), "inventory"));
         ModelLoader.setCustomMeshDefinition(ATLAS, (stack) -> {
            return new ModelResourceLocation(ATLAS.getRegistryName(), "inventory");
         });
      }

   }

   @SubscribeEvent
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
}
