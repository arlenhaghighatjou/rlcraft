package familiarfauna.core;

import familiarfauna.init.ModCompat;
import familiarfauna.init.ModConfiguration;
import familiarfauna.init.ModCrafting;
import familiarfauna.init.ModEntities;
import familiarfauna.init.ModItems;
import familiarfauna.init.ModLootTable;
import familiarfauna.init.ModSounds;
import java.io.File;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
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
   @Instance("familiarfauna")
   public static FamiliarFauna instance;
   @SidedProxy(
      clientSide = "familiarfauna.core.ClientProxy",
      serverSide = "familiarfauna.core.CommonProxy"
   )
   public static CommonProxy proxy;
   public static Logger logger = LogManager.getLogger("familiarfauna");
   public static File configDirectory;

   public FamiliarFauna() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      configDirectory = new File(event.getModConfigurationDirectory(), "familiarfauna");
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
