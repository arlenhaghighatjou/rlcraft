package com.teamacronymcoders.base;

import com.teamacronymcoders.base.capability.tool.ToolConfiguration;
import com.teamacronymcoders.base.command.CommandSubBase;
import com.teamacronymcoders.base.entities.dataserializers.BaseDataSerializers;
import com.teamacronymcoders.base.items.ItemEventHandler;
import com.teamacronymcoders.base.proxies.ModCommonProxy;
import com.teamacronymcoders.base.recipesystem.RecipeSystem;
import com.teamacronymcoders.base.util.LanguageHelper;
import com.teamacronymcoders.base.util.OreDictUtils;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.server.command.CommandTreeBase;

@Mod(
   modid = "base",
   name = "BASE",
   version = "3.14.0",
   dependencies = "after:crafttweaker;after:jei",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Base extends BaseModFoundation<Base> {
   public static final LanguageHelper languageHelper = new LanguageHelper("base");
   @Instance("base")
   public static Base instance;
   @SidedProxy(
      clientSide = "com.teamacronymcoders.base.proxies.ModClientProxy",
      serverSide = "com.teamacronymcoders.base.proxies.ModServerProxy"
   )
   public static ModCommonProxy proxy;
   private CommandTreeBase baseCommand = new CommandSubBase("base");

   public Base() {
      super("base", "BASE", "3.14.0", CreativeTabs.MISC);
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      OreDictUtils.setup();
      BaseDataSerializers.registerSerializers();
      Capabilities.register();
      ToolConfiguration.configureTool();
      RecipeSystem.setup();
      MinecraftForge.EVENT_BUS.register(new ItemEventHandler());
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      super.init(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
   }

   @EventHandler
   public void serverInit(FMLServerStartingEvent event) {
      event.registerServerCommand(this.getBaseCommand());
   }

   public Base getInstance() {
      return instance;
   }

   public CommandTreeBase getBaseCommand() {
      return this.baseCommand;
   }

   static {
      FluidRegistry.enableUniversalBucket();
   }
}
