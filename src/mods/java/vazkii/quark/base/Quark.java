package vazkii.quark.base;

import java.util.Map;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import vazkii.quark.base.proxy.CommonProxy;
import vazkii.quark.experimental.features.RejectMods;

@Mod(
   modid = "quark",
   name = "quark",
   version = "r1.6-179",
   guiFactory = "vazkii.quark.base.client.gui.GuiFactory",
   dependencies = "required-after:forge@[14.23.5.2831,);required-before:autoreglib@[1.3-32,);after:jei@[4.6.0,);",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Quark {
   public static final Logger LOG = LogManager.getLogger("Quark");
   @Instance("quark")
   public static Quark instance;
   @SidedProxy(
      serverSide = "vazkii.quark.base.proxy.CommonProxy",
      clientSide = "vazkii.quark.base.proxy.ClientProxy"
   )
   public static CommonProxy proxy;

   public Quark() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
      proxy.finalInit(event);
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      proxy.serverStarting(event);
   }

   @NetworkCheckHandler
   public boolean acceptsMods(Map<String, String> mods, Side remoteSide) {
      return RejectMods.accepts(mods, remoteSide);
   }
}
