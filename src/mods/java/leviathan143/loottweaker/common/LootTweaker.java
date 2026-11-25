package leviathan143.loottweaker.common;

import crafttweaker.mc1120.commands.CTChatCommand;
import java.util.Map;
import leviathan143.loottweaker.common.command.CommandLootTables;
import leviathan143.loottweaker.common.zenscript.LootTweakerContext;
import leviathan143.loottweaker.common.zenscript.ZenLootTableTweakManager;
import leviathan143.loottweaker.common.zenscript.factory.ZenLambdaLootCondition;
import leviathan143.loottweaker.common.zenscript.factory.ZenLambdaLootFunction;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "loottweaker",
   name = "LootTweaker",
   version = "0.3.1",
   dependencies = "required-after:crafttweaker@[4.1.20,); before:jeresources; required:forge@[14.23.5.2779,);",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class LootTweaker {
   public static final String MODNAME = "LootTweaker";
   public static final String MODID = "loottweaker";
   public static final String VERSION = "0.3.1";
   public static final String DEPENDENCIES = "required-after:crafttweaker@[4.1.20,); before:jeresources; required:forge@[14.23.5.2779,);";
   public static final LootTweakerContext CONTEXT = new LootTweakerContext(new CTLoggingErrorHandler());
   private static final Logger LOGGER = LogManager.getLogger("LootTweaker");

   public LootTweaker() {
   }

   @NetworkCheckHandler
   public boolean networkCheck(Map<String, String> modVersions, Side remoteSide) {
      if (!modVersions.containsKey("forge")) {
         return false;
      } else {
         String remoteLTVersion = (String)modVersions.get("loottweaker");
         if (remoteLTVersion == null) {
            if (remoteSide == Side.CLIENT) {
               LOGGER.info("Accepted non-existent client LootTweaker install");
               return true;
            } else {
               LOGGER.info("Rejected non-existent server LootTweaker install");
               return false;
            }
         } else if (!remoteLTVersion.equals("0.3.1")) {
            LOGGER.info("Rejected {} LootTweaker install because its version {} differs from local version {}", remoteSide.name().toLowerCase(), remoteLTVersion, "0.3.1");
            return false;
         } else {
            return true;
         }
      }
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      LTConfig.onLoad();
      LootFunctionManager.registerFunction(ZenLambdaLootFunction.SERIALISER);
      LootConditionManager.registerCondition(ZenLambdaLootCondition.SERIALISER);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      CTChatCommand.registerCommand(new CommandLootTables());
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      ZenLootTableTweakManager.onServerStarting(event);
   }

   @EventHandler
   public void serverStarted(FMLServerStartedEvent event) {
      DeprecationWarningManager.printDeprecationWarnings();
   }
}
