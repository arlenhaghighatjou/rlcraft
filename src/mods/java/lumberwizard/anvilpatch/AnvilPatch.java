package lumberwizard.anvilpatch;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import lumberwizard.anvilpatch.common.CommonProxy;
import lumberwizard.anvilpatch.network.ModNetworkHandler;
import lumberwizard.anvilpatch.network.PacketConfigSync;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "anvilpatch",
   name = "Anvil Patch - Lawful",
   version = "1.1.1"
)
public class AnvilPatch {
   public static final String MODID = "anvilpatch";
   public static final String NAME = "Anvil Patch - Lawful";
   public static final String VERSION = "1.1.1";
   public static CommonProxy proxy;
   public static AnvilPatch instance;
   public static Logger logger;

   public AnvilPatch() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new lumberwizard.anvilpatch.client.ClientProxy() : new lumberwizard.anvilpatch.common.CommonProxy();
         lumberwizard.anvilpatch.ModConfig.register();
         lumberwizard.anvilpatch.AnvilPatch.register(MinecraftForge.EVENT_BUS);
         lumberwizard.anvilpatch.common.AnvilEventHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      if (Loader.isModLoaded("anvilfix")) {
         proxy.throwAnvilFixException();
      }

      ModNetworkHandler.registerPacketHandlers();
      logger = event.getModLog();
   }

   public static void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, AnvilPatch::onConfigChanged);
      bus.register(PlayerLoggedInEvent.class, AnvilPatch::onPlayerConnected);
      bus.register(PlayerLoggedOutEvent.class, AnvilPatch::onPlayerDisconnected);
   }

   public static void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("anvilpatch")) {
         ConfigManager.sync("anvilpatch", Type.INSTANCE);
      }

   }

   public static void onPlayerConnected(PlayerLoggedInEvent event) {
      if (!event.player.world.isRemote && event.player instanceof EntityPlayerMP) {
         ModNetworkHandler.INSTANCE.sendTo(new PacketConfigSync(ModConfig.levelCap, ModConfig.costIncreaseSetting, ModConfig.breakEnchantLevelCap, ModConfig.breakChance), (EntityPlayerMP)event.player);
      }

   }

   public static void onPlayerDisconnected(PlayerLoggedOutEvent event) {
      if (event.player.world.isRemote) {
         ModConfig.setValuesOverridden(false);
      }

   }
}
