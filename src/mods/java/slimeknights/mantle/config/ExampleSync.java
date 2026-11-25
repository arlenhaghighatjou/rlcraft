package slimeknights.mantle.config;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.network.NetworkWrapper;

class ExampleSync {
   static NetworkWrapper networkWrapper;
   static ExampleSync INSTANCE;
   @SideOnly(Side.CLIENT)
   private static boolean needsRestart;

   ExampleSync() {
   }

   static void setup() {
      networkWrapper = new NetworkWrapper("mantle:example");
      networkWrapper.registerPacketClient(ExampleSync.ExampleSyncPacketImpl.class);
      INSTANCE = new ExampleSync();
   }

   @SubscribeEvent
   @SideOnly(Side.SERVER)
   public void playerLoggedIn(PlayerLoggedInEvent event) {
      if (event.player instanceof EntityPlayerMP && FMLCommonHandler.instance().getSide().isServer()) {
         ExampleSync.ExampleSyncPacketImpl packet = new ExampleSync.ExampleSyncPacketImpl();
         networkWrapper.network.sendTo(packet, (EntityPlayerMP)event.player);
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void playerJoinedWorld(ClientTickEvent event) {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      if (needsRestart) {
         player.sendMessage(new TextComponentString("Configs synced with server. Configs require a restart"));
      } else {
         player.sendMessage(new TextComponentString("Configs synced with server."));
      }

      MinecraftForge.EVENT_BUS.unregister(this);
   }

   static class ExampleConfig extends AbstractConfig {
      static ExampleSync.ExampleConfig INSTANCE = new ExampleSync.ExampleConfig();
      ExampleSync.ExampleConfigFile exampleConfigFile;

      ExampleConfig() {
      }

      public void onPreInit(FMLPreInitializationEvent event) {
         this.exampleConfigFile = (ExampleSync.ExampleConfigFile)this.load(new ExampleSync.ExampleConfigFile(event.getModConfigurationDirectory()), ExampleSync.ExampleConfigFile.class);
         if (event.getSide().isServer()) {
            MinecraftForge.EVENT_BUS.register(INSTANCE);
         }

      }
   }

   static class ExampleConfigFile extends AbstractConfigFile {
      public ExampleConfigFile(File configFolder) {
         super(configFolder, "exampleconfigfile");
      }

      public void insertDefaults() {
      }

      protected int getConfigVersion() {
         return 1;
      }
   }

   static class ExampleSyncPacketImpl extends AbstractConfigSyncPacket {
      ExampleSyncPacketImpl() {
      }

      protected AbstractConfig getConfig() {
         return ExampleSync.ExampleConfig.INSTANCE;
      }

      protected boolean sync() {
         if (super.sync()) {
            MinecraftForge.EVENT_BUS.register(ExampleSync.INSTANCE);
            return true;
         } else {
            return false;
         }
      }
   }
}
