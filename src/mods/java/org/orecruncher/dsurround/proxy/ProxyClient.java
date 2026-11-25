package org.orecruncher.dsurround.proxy;

import java.util.Arrays;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilityEntityFXData;
import org.orecruncher.dsurround.capabilities.CapabilitySeasonInfo;
import org.orecruncher.dsurround.capabilities.CapabilitySpeechData;
import org.orecruncher.dsurround.client.fx.ParticleCollections;
import org.orecruncher.dsurround.client.fx.particle.ParticleDripOverride;
import org.orecruncher.dsurround.client.gui.HumDinger;
import org.orecruncher.dsurround.client.handlers.EffectManager;
import org.orecruncher.dsurround.client.hud.GuiHUDHandler;
import org.orecruncher.dsurround.client.hud.InspectionHUD;
import org.orecruncher.dsurround.client.keyboard.KeyHandler;
import org.orecruncher.dsurround.client.renderer.AnimaniaBadge;
import org.orecruncher.dsurround.client.renderer.weather.RenderWeather;
import org.orecruncher.dsurround.client.sound.BackgroundMute;
import org.orecruncher.dsurround.client.sound.SoundEngine;
import org.orecruncher.dsurround.client.weather.Weather;
import org.orecruncher.dsurround.commands.CommandCalc;
import org.orecruncher.dsurround.event.WorldEventDetector;
import org.orecruncher.dsurround.lib.compat.ModEnvironment;
import org.orecruncher.dsurround.registry.RegistryDataEvent;
import org.orecruncher.lib.ForgeUtils;
import org.orecruncher.lib.Localization;
import org.orecruncher.lib.chunk.ClientChunkCache;
import org.orecruncher.lib.task.Scheduler;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy implements IProxy, ISelectiveResourceReloadListener {
   public ProxyClient() {
   }

   protected void registerLanguage() {
      Localization.initialize(Side.CLIENT, "dsurround");
   }

   protected void eventBusRegistrations() {
      super.eventBusRegistrations();
      register(HumDinger.class);
      register(InspectionHUD.class);
      register(KeyHandler.class);
      register(BackgroundMute.class);
      register(RenderWeather.class);
      register(Weather.class);
      register(WorldEventDetector.class);
      register(ParticleCollections.class);
   }

   public boolean isRunningAsServer() {
      return false;
   }

   public Side effectiveSide() {
      return FMLCommonHandler.instance().getEffectiveSide();
   }

   public void preInit(@Nonnull FMLPreInitializationEvent event) {
      super.preInit(event);
      CapabilitySpeechData.register();
      CapabilitySeasonInfo.register();
      CapabilityEntityFXData.register();
   }

   public void init(@Nonnull FMLInitializationEvent event) {
      super.init(event);
      KeyHandler.init();
      ParticleDripOverride.register();
      ClientCommandHandler.instance.registerCommand(new CommandCalc());
      if (ModOptions.effects.disableWaterSuspendParticle) {
         Minecraft.getMinecraft().effectRenderer.registerParticle(EnumParticleTypes.SUSPENDED.getParticleID(), (IParticleFactory)null);
      }

      if (ModEnvironment.AmbientSounds.isLoaded()) {
         SoundEngine.configureSound((SoundSetupEvent)null);
      }

   }

   public void postInit(@Nonnull FMLPostInitializationEvent event) {
      super.postInit(event);
      ModMetadata data = ForgeUtils.getModMetadata("dsurround");
      if (data != null) {
         data.name = Localization.loadString("dsurround.metadata.Name");
         data.credits = Localization.loadString("dsurround.metadata.Credits");
         data.description = Localization.loadString("dsurround.metadata.Description");
         data.authorList = Arrays.asList(StringUtils.split(Localization.loadString("dsurround.metadata.Authors"), ','));
      }

      IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
      ((IReloadableResourceManager)resourceManager).registerReloadListener(this);
      if (ModEnvironment.Animania.isLoaded()) {
         AnimaniaBadge.initialize();
      }

      int r = Math.max(32, ModOptions.effects.specialEffectRange);
      r = r + 2;
      ClientChunkCache.initialize(r, !ModOptions.general.enableClientChunkCaching);
   }

   public void clientConnect(@Nonnull ClientConnectedToServerEvent event) {
      Scheduler.schedule(Side.CLIENT, () -> {
         EffectManager.connect();
         GuiHUDHandler.register();
         Weather.register(ModBase.isInstalledOnServer());
      });
   }

   public void clientDisconnect(@Nonnull ClientDisconnectionFromServerEvent event) {
      Scheduler.schedule(Side.CLIENT, () -> {
         EffectManager.disconnect();
         GuiHUDHandler.unregister();
         Weather.unregister();
      });
   }

   public void onResourceManagerReload(@Nonnull IResourceManager resourceManager, @Nonnull Predicate<IResourceType> resourcePredicate) {
      if (resourcePredicate.test(VanillaResourceType.SOUNDS)) {
         MinecraftForge.EVENT_BUS.post(new RegistryDataEvent.Resources(resourceManager));
      }

   }

   public IThreadListener getThreadListener(@Nonnull MessageContext context) {
      return (IThreadListener)(context.side.isClient() ? Minecraft.getMinecraft() : context.getServerHandler().player.getServer());
   }
}
