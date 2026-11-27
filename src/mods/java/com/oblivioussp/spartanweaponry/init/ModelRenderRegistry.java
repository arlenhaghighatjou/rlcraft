package com.oblivioussp.spartanweaponry.init;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.item.ItemMultiSW;
import com.oblivioussp.spartanweaponry.util.Log;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelRenderRegistry {
   private static List<ModelRenderRegistry.ModelRegistryEntry> models = new ArrayList();
   private static List<ModelRenderRegistry.ColourHandlerEntry> colourHandlers = new ArrayList();

   public ModelRenderRegistry() {
   }

   public static void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, ModelRenderRegistry::registerItemRenders);
   }

   public static void registerItemRenders(ModelRegistryEvent ev) {
      registerMultiItemRender(ItemRegistrySW.material);

      for(ModelRenderRegistry.ModelRegistryEntry entry : models) {
         registerItemRender(entry.getItem(), entry.getModelLocation());
      }

      models.clear();
      Log.info("Registered all item renders!");
   }

   @SideOnly(Side.CLIENT)
   public static void registerColourHandlers() {
      for(ModelRenderRegistry.ColourHandlerEntry entry : colourHandlers) {
         registerCustomWeaponColourHandler(entry.getItem(), entry.getColourPrimary(), entry.getColourSecondary());
      }

      colourHandlers.clear();
   }

   public static void addItemToRegistry(Item item, ResourceLocation modelLoc) {
      models.add(new ModelRenderRegistry.ModelRegistryEntry(item, modelLoc));
   }

   public static void addItemToRegistry(Item item, ResourceLocation modelLoc, ToolMaterialEx material) {
      models.add(new ModelRenderRegistry.ModelRegistryEntry(item, modelLoc));
      ModSpartanWeaponry var10000 = ModSpartanWeaponry.instance;
      ModSpartanWeaponry.proxy.addColourHandler(item, material);
   }

   public static void addItemToColourHandler(Item item, ToolMaterialEx material) {
      colourHandlers.add(new ModelRenderRegistry.ColourHandlerEntry(item, material.getPrimaryColour(), material.getSecondaryColour()));
   }

   protected static void registerItemRender(Item item, ResourceLocation modelLocation) {
      ModelResourceLocation modelResLoc = new ModelResourceLocation(modelLocation, "inventory");
      ModelLoader.setCustomModelResourceLocation(item, 0, modelResLoc);
   }

   public static void registerMultiItemRender(ItemMultiSW item) {
      String[] localizedNames = item.getAllUnlocalizedNames();
      if (localizedNames != null) {
         for(int i = 0; i < localizedNames.length; ++i) {
            String unlocName = localizedNames[i];
            Log.debug("Registering model of item: " + item.getRegistryName().toString() + ":" + i);
            ModelResourceLocation modelLoc = new ModelResourceLocation("spartanweaponry:" + StringHelper.stripUnlocalizedName(unlocName), "inventory");
            ModelLoader.setCustomModelResourceLocation(item, i, modelLoc);
         }

      }
   }

   @SideOnly(Side.CLIENT)
   private static void registerCustomWeaponColourHandler(Item item, final int colour1, final int colour2) {
      FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler(new IItemColor() {
         public int colorMultiplier(ItemStack stack, int tintIndex) {
            return tintIndex == 1 ? colour1 : (tintIndex == 2 ? colour2 : 16777215);
         }
      }, new Item[]{item});
   }

   public static class ColourHandlerEntry {
      protected Item item;
      protected int colourPrimary;
      protected int colourSecondary;

      public ColourHandlerEntry(Item itemIn, int colourPriIn, int colourSecIn) {
         this.item = itemIn;
         this.colourPrimary = colourPriIn;
         this.colourSecondary = colourSecIn;
      }

      public Item getItem() {
         return this.item;
      }

      public int getColourPrimary() {
         return this.colourPrimary;
      }

      public int getColourSecondary() {
         return this.colourSecondary;
      }
   }

   public static class ModelRegistryEntry {
      protected ResourceLocation modelLoc;
      protected Item item;

      public ModelRegistryEntry(Item itemForEntry, ResourceLocation modelLocation) {
         this.item = itemForEntry;
         this.modelLoc = modelLocation;
      }

      public ModelRegistryEntry(Item itemForEntry, String modId, String modelLocation) {
         this.item = itemForEntry;
         this.modelLoc = new ResourceLocation(modId, modelLocation);
      }

      public Item getItem() {
         return this.item;
      }

      public ResourceLocation getModelLocation() {
         return this.modelLoc;
      }
   }
}
