package codersafterdark.compatskills;

import codersafterdark.compatskills.common.CommonProxy;
import codersafterdark.compatskills.utils.CompatModuleBase;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.mc1120.commands.CTChatCommand;
import crafttweaker.mc1120.commands.CraftTweakerCommand;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "compatskills",
   name = "CompatSkills",
   version = "1.12.2-1.17.0",
   dependencies = "required-after:reskillable@[1.12.2-1.13.0,);after:baubles;after:bloodmagic@[1.12.2-2.3.0-98,);after:gamestages;after:immersiveengineering@[0.12-92,);after:magneticraft;after:tconstruct;after:theoneprobe;after:projecte@[1.12-PE1.3.1B,);after:thaumcraft@[1.12.2-6.1.BETA20,);after:betterquesting@[3.5.311,);after:astralsorcery",
   acceptedMinecraftVersions = "1.12,"
)
public class CompatSkills {
   public static final List<IAction> LATE_ADDITIONS = new LinkedList();
   @SidedProxy(
      serverSide = "codersafterdark.compatskills.common.CommonProxy",
      clientSide = "codersafterdark.compatskills.client.ClientProxy"
   )
   public static CommonProxy proxy;
   public static Logger logger;
   public static boolean craftweakerLoaded;

   public CompatSkills() {
   }

   public static void registerCommand(Object command) {
      if (command instanceof CraftTweakerCommand) {
         CTChatCommand.registerCommand((CraftTweakerCommand)command);
      }

   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      proxy.preInit(event);
      craftweakerLoaded = Loader.isModLoaded("crafttweaker");
      CompatModuleBase.doModulesPreInit();
      if (event.getSide().isClient()) {
         CompatModuleBase.doModulesPreInitClient();
      }

   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.Init(event);
      CompatModuleBase.doModulesInit();
      if (event.getSide().isClient()) {
         CompatModuleBase.doModulesInitClient();
      }

   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
      CompatModuleBase.doModulesPostInit();
      if (event.getSide().isClient()) {
         CompatModuleBase.doModulesPostInitClient();
      }

      if (craftweakerLoaded) {
         LATE_ADDITIONS.forEach(CraftTweakerAPI::apply);
      }

   }

   @EventHandler
   public void serverStart(FMLServerStartingEvent event) {
      proxy.serverStart(event);
      CompatModuleBase.doModulesLoadComplete();
   }
}
