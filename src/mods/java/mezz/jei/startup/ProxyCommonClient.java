package mezz.jei.startup;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.api.IModPlugin;
import mezz.jei.config.Config;
import mezz.jei.config.KeyBindings;
import mezz.jei.config.ServerInfo;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.gui.textures.JeiTextureMap;
import mezz.jei.gui.textures.Textures;
import mezz.jei.network.PacketHandlerClient;
import mezz.jei.network.packets.PacketJei;
import mezz.jei.plugins.jei.JEIInternalPlugin;
import mezz.jei.plugins.vanilla.VanillaPlugin;
import mezz.jei.runtime.JeiRuntime;
import mezz.jei.util.Log;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ProxyCommonClient extends ProxyCommon {
   private List<IModPlugin> plugins = new ArrayList();
   private final JeiStarter starter = new JeiStarter();
   private final JeiTextureMap textureMap = new JeiTextureMap("textures");
   @Nullable
   private Textures textures;

   public ProxyCommonClient() {
   }

   private static void initVersionChecker() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("curseProjectName", "jei");
      compound.setString("curseFilenameParser", "jei_1.12.2-[].jar");
      FMLInterModComms.sendRuntimeMessage("jei", "VersionChecker", "addCurseCheck", compound);
   }

   public void preInit(FMLPreInitializationEvent event) {
      PacketHandlerClient packetHandler = new PacketHandlerClient();
      this.channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("jei");
      packetHandler.register(this.channel);
      Config.preInit(event);
      initVersionChecker();
      this.plugins = AnnotatedInstanceUtil.getModPlugins();
      IModPlugin vanillaPlugin = this.getVanillaPlugin(this.plugins);
      if (vanillaPlugin != null) {
         this.plugins.remove(vanillaPlugin);
         this.plugins.add(0, vanillaPlugin);
      }

      IModPlugin jeiInternalPlugin = this.getJeiInternalPlugin(this.plugins);
      if (jeiInternalPlugin != null) {
         this.plugins.remove(jeiInternalPlugin);
         this.plugins.add(jeiInternalPlugin);
      }

      this.register(MinecraftForge.EVENT_BUS);
   }

   @Nullable
   private IModPlugin getVanillaPlugin(List<IModPlugin> modPlugins) {
      for(IModPlugin modPlugin : modPlugins) {
         if (modPlugin instanceof VanillaPlugin) {
            return modPlugin;
         }
      }

      return null;
   }

   @Nullable
   private IModPlugin getJeiInternalPlugin(List<IModPlugin> modPlugins) {
      for(IModPlugin modPlugin : modPlugins) {
         if (modPlugin instanceof JEIInternalPlugin) {
            return modPlugin;
         }
      }

      return null;
   }

   public void init(FMLInitializationEvent event) {
      KeyBindings.init();
      Minecraft minecraft = Minecraft.getMinecraft();
      minecraft.renderEngine.loadTickableTexture(this.textureMap.getLocation(), this.textureMap);
   }

   public void loadComplete(FMLLoadCompleteEvent event) {
      Minecraft minecraft = Minecraft.getMinecraft();
      IReloadableResourceManager reloadableResourceManager = (IReloadableResourceManager)minecraft.getResourceManager();
      reloadableResourceManager.registerReloadListener((resourceManager) -> {
         if (this.starter.hasStarted()) {
            if (Config.isDebugModeEnabled()) {
               Log.get().info("Restarting JEI.", new RuntimeException("Stack trace for debugging"));
            } else {
               Log.get().info("Restarting JEI.");
            }

            Preconditions.checkNotNull(this.textures);
            this.starter.start(this.plugins, this.textures);
         }

      });
      Preconditions.checkNotNull(this.textures);
      this.starter.start(this.plugins, this.textures);
   }

   public void register(EventBus bus) {
      bus.register(ClientConnectedToServerEvent.class, this::onClientConnectedToServer);
      bus.register(OnConfigChangedEvent.class, this::onConfigChanged);
      bus.register(Save.class, this::onWorldSave);
      bus.register(Pre.class, this::handleTextureRemap);
   }

   public void onClientConnectedToServer(ClientConnectedToServerEvent event) {
      if (!event.isLocal() && !event.getConnectionType().equals("MODDED")) {
         ServerInfo.onConnectedToServer(false);
      }

      INetworkManager networkManager = event.getManager();
      Config.syncWorldConfig(networkManager);
      MinecraftForge.EVENT_BUS.post(new PlayerJoinedWorldEvent());
   }

   private static void reloadItemList() {
      JeiRuntime runtime = Internal.getRuntime();
      if (runtime != null) {
         IngredientListOverlay ingredientListOverlay = runtime.getIngredientListOverlay();
         ingredientListOverlay.rebuildItemFilter();
      }

   }

   public void sendPacketToServer(PacketJei packet) {
      NetHandlerPlayClient netHandler = FMLClientHandler.instance().getClient().getConnection();
      if (netHandler != null && ServerInfo.isJeiOnServer()) {
         netHandler.sendPacket(packet.getPacket());
      }

   }

   public void onConfigChanged(OnConfigChangedEvent eventArgs) {
      if ("jei".equals(eventArgs.getModID())) {
         if (Config.syncAllConfig()) {
            reloadItemList();
         }
      } else if (this.starter.hasStarted()) {
         Config.checkForModNameFormatOverride();
      }

   }

   public void onWorldSave(Save event) {
      try {
         Config.saveFilterText();
      } catch (RuntimeException var3) {
         Log.get().error("Failed to save filter text.", var3);
      }

   }

   @SideOnly(Side.CLIENT)
   public void handleTextureRemap(Pre event) {
      this.textures = new Textures(this.textureMap);
   }
}
