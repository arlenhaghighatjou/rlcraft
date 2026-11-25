package com.blamejared;

import com.blamejared.compat.botania.Botania;
import com.blamejared.compat.chisel.Chisel;
import com.blamejared.compat.inspirations.Inspirations;
import com.blamejared.compat.tconstruct.TConstruct;
import com.blamejared.compat.thaumcraft.handlers.ThaumCraft;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
   modid = "modtweaker",
   name = "Modtweaker",
   version = "4.0.19",
   dependencies = "required-after:crafttweaker;required-after:mtlib;before:jei",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class ModTweaker {
   public static final List<IAction> LATE_REMOVALS = new LinkedList();
   public static final List<IAction> LATE_ADDITIONS = new LinkedList();

   public ModTweaker() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent e) {
      if (Loader.isModLoaded("thaumcraft")) {
         ThaumCraft.preInit();
      }

   }

   @EventHandler
   public void init(FMLInitializationEvent e) {
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent e) {
   }

   @EventHandler
   public void loadComplete(FMLLoadCompleteEvent event) {
      try {
         LATE_REMOVALS.forEach(CraftTweakerAPI::apply);
         LATE_ADDITIONS.forEach(CraftTweakerAPI::apply);
      } catch (Exception var3) {
         var3.printStackTrace();
         CraftTweakerAPI.logError("Error while applying actions", var3);
      }

      LATE_REMOVALS.clear();
      LATE_ADDITIONS.clear();
   }

   @EventHandler
   public void onFMLServerStarting(FMLServerStartingEvent event) {
      if (Loader.isModLoaded("tconstruct")) {
         TConstruct.registerCommands();
      }

      if (Loader.isModLoaded("botania")) {
         Botania.registerCommands();
      }

      if (Loader.isModLoaded("chisel")) {
         Chisel.registerCommands();
      }

      if (Loader.isModLoaded("inspirations")) {
         Inspirations.registerCommands();
      }

      if (Loader.isModLoaded("thaumcraft")) {
         ThaumCraft.registerCommands();
      }

   }
}
