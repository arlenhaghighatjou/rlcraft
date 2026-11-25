package lumberwizard.anvilpatch;

import lumberwizard.anvilpatch.common.CommonProxy;
import lumberwizard.anvilpatch.network.ModNetworkHandler;
import lumberwizard.anvilpatch.network.PacketConfigSync;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "anvilpatch",
   name = "Anvil Patch - Lawful",
   version = "1.1.1"
)
@EventBusSubscriber(
   modid = "anvilpatch"
)
public class AnvilPatch {
   public static final String MODID = "anvilpatch";
   public static final String NAME = "Anvil Patch - Lawful";
   public static final String VERSION = "1.1.1";
   @SidedProxy(
      clientSide = "lumberwizard.anvilpatch.client.ClientProxy",
      serverSide = "lumberwizard.anvilpatch.common.CommonProxy"
   )
   public static CommonProxy proxy;
   @Instance
   public static AnvilPatch instance;
   public static Logger logger;

   public AnvilPatch() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      if (Loader.isModLoaded("anvilfix")) {
         proxy.throwAnvilFixException();
      }

      ModNetworkHandler.registerPacketHandlers();
      logger = event.getModLog();
   }

   @SubscribeEvent
   public static void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("anvilpatch")) {
         ConfigManager.sync("anvilpatch", Type.INSTANCE);
      }

   }

   @SubscribeEvent
   public static void onPlayerConnected(PlayerLoggedInEvent event) {
      if (!event.player.world.isRemote && event.player instanceof EntityPlayerMP) {
         ModNetworkHandler.INSTANCE.sendTo(new PacketConfigSync(ModConfig.levelCap, ModConfig.costIncreaseSetting, ModConfig.breakEnchantLevelCap, ModConfig.breakChance), (EntityPlayerMP)event.player);
      }

   }

   @SubscribeEvent
   public static void onPlayerDisconnected(PlayerLoggedOutEvent event) {
      if (event.player.world.isRemote) {
         ModConfig.setValuesOverridden(false);
      }

   }
}
