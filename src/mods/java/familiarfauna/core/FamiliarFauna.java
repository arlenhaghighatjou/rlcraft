package familiarfauna.core;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import familiarfauna.init.ModCompat;
import familiarfauna.init.ModConfiguration;
import familiarfauna.init.ModCrafting;
import familiarfauna.init.ModEntities;
import familiarfauna.init.ModItems;
import familiarfauna.init.ModLootTable;
import familiarfauna.init.ModSounds;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "familiarfauna",
   version = "1.0.11",
   name = "FamiliarFauna",
   dependencies = "required-after:forge@[14.23.5.2768,);after:biomesoplenty;",
   guiFactory = "familiarfauna.client.gui.GuiFFFactory",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class FamiliarFauna {
   public static final String MOD_NAME = "FamiliarFauna";
   public static final String MOD_ID = "familiarfauna";
   public static final String MOD_VERSION = "1.0.11";
   public static final String GUI_FACTORY = "familiarfauna.client.gui.GuiFFFactory";
   public static FamiliarFauna instance;
   public static CommonProxy proxy;
   public static Logger logger = LogManager.getLogger("familiarfauna");
   public static VFile2 configDirectory;

   public FamiliarFauna() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new familiarfauna.core.ClientProxy() : new familiarfauna.core.CommonProxy();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      configDirectory = new VFile2(event.getModConfigurationDirectory(), "familiarfauna");
      ModConfiguration.init(configDirectory);
      ModCompat.init();
      ModSounds.init();
      ModEntities.init();
      ModItems.init();
      ModLootTable.init();
      ModCrafting.init();
      proxy.registerRenderers();
   }
}
