package lykrast.defiledlands.core;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "defiledlands",
   name = "Defiled Lands",
   version = "1.4.3",
   acceptedMinecraftVersions = "[1.12,1.13)",
   dependencies = "required-after:baubles;after:tconstruct"
)
public class DefiledLands {
   public static final String MODID = "defiledlands";
   public static final String NAME = "Defiled Lands";
   public static final String VERSION = "1.4.3";
   public static DefiledLands instance;
   public static final Logger LOGGER = LogManager.getLogger("defiledlands");
   public static CommonProxy proxy;

   public DefiledLands() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new lykrast.defiledlands.core.ClientProxy() : new lykrast.defiledlands.core.CommonProxy();
         lykrast.defiledlands.common.init.ModBiomes.register(MinecraftForge.EVENT_BUS);
         lykrast.defiledlands.common.init.ModBlocks.register(MinecraftForge.EVENT_BUS);
         lykrast.defiledlands.common.init.ModEnchantments.register(MinecraftForge.EVENT_BUS);
         lykrast.defiledlands.common.init.ModEntities.register(MinecraftForge.EVENT_BUS);
         lykrast.defiledlands.common.init.ModItems.register(MinecraftForge.EVENT_BUS);
         lykrast.defiledlands.common.init.ModPotions.register(MinecraftForge.EVENT_BUS);
         lykrast.defiledlands.common.init.ModRecipes.register(MinecraftForge.EVENT_BUS);
         lykrast.defiledlands.common.init.ModSounds.register(MinecraftForge.EVENT_BUS);
         lykrast.defiledlands.common.potion.PotionHooks.register(MinecraftForge.EVENT_BUS);
         lykrast.defiledlands.common.util.RavagingDropHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent e) {
      proxy.preInit(e);
   }

   public void init(FMLInitializationEvent e) {
      proxy.init(e);
   }

   public void postInit(FMLPostInitializationEvent e) {
      proxy.postInit(e);
   }
}
