package com.teammetallurgy.aquaculture;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.teammetallurgy.aquaculture.enchantments.AquacultureEnchants;
import com.teammetallurgy.aquaculture.handlers.AquacultureRecipes;
import com.teammetallurgy.aquaculture.handlers.AquacultureTab;
import com.teammetallurgy.aquaculture.handlers.Config;
import com.teammetallurgy.aquaculture.items.AquacultureItems;
import com.teammetallurgy.aquaculture.proxy.CommonProxy;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "aquaculture",
   name = "Aquaculture",
   version = "1.6.8",
   guiFactory = "com.teammetallurgy.aquaculture.client.GuiFactory",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Aquaculture {
   public static final String MOD_ID = "aquaculture";
   public static final String MOD_NAME = "Aquaculture";
   public static final String MOD_VERSION = "1.6.8";
   public static final String GUI_FACTORY_CLASS = "com.teammetallurgy.aquaculture.client.GuiFactory";
   public static Aquaculture instance;
   public static CommonProxy proxy;
   public static AquacultureTab tab;

   public Aquaculture() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.teammetallurgy.aquaculture.proxy.ClientProxy() : new com.teammetallurgy.aquaculture.proxy.CommonProxy();
         com.teammetallurgy.aquaculture.handlers.Config.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.load((FMLInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      Config.init(event.getSuggestedConfigurationFile());
      tab = new AquacultureTab("Aquaculture");
      (new AquacultureItems()).register();
      proxy.registerItemModels();
      proxy.registerEntities();
      proxy.registerModelRenderers();
   }

   public void load(FMLInitializationEvent event) {
      AquacultureRecipes.addSmeltingRecipes();
      AquacultureRecipes.addBrewingRecipes();
      AquacultureEnchants.init();
      tab.setItemStack(new ItemStack(AquacultureItems.ironFishingRod));
   }
}
