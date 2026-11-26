package melonslise.locks;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import melonslise.locks.common.command.CommandLocksDebug;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksNetworks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "locks",
   name = "Locks",
   version = "3.0.0",
   acceptedMinecraftVersions = "1.12.2"
)
public final class Locks {
   public static final String ID = "locks";
   public static final String NAME = "Locks";
   public static final String VERSION = "3.0.0";
   public static final String GAMEVERSIONS = "1.12.2";
   public static Locks instance = null;
   public static boolean debug = false;
   public static Logger logger = LogManager.getLogger("Locks");

   public Locks() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         melonslise.locks.common.config.LocksConfig.register();
         melonslise.locks.common.init.LocksCapabilities.registerCapabilities();
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            melonslise.locks.client.event.LocksClientEvents.register(MinecraftForge.EVENT_BUS);
         }
         melonslise.locks.common.event.LocksDelayEvents.register(MinecraftForge.EVENT_BUS);
         melonslise.locks.common.event.LocksEvents.register(MinecraftForge.EVENT_BUS);
         net.minecraftforge.common.crafting.CraftingHelper.register(new net.minecraft.util.ResourceLocation("locks", "locking_item_cloning"), new melonslise.locks.common.recipe.LockingItemCloningRecipe.Factory());
      } else if (event instanceof FMLInitializationEvent) {
         this.onInitialization((FMLInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.onServerStarting((FMLServerStartingEvent)event);
      }

   }

   public void onInitialization(FMLInitializationEvent event) {
      LocksCapabilities.register();
      LocksNetworks.register();
      LocksConfig.init();
   }

   public void onServerStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandLocksDebug());
   }
}
