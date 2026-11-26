package bl4ckscor3.mod.particleculling;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "particleculling",
   name = "Particle Culling",
   version = "v1.4.1",
   clientSideOnly = true,
   acceptedMinecraftVersions = "[1.12.2]"
)
public class ParticleCulling {
   public static final String MODID = "particleculling";
   public static final Logger LOGGER = LogManager.getLogger("particleculling");
   public static CullThread cullThread;

   public ParticleCulling() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         ParticleCulling.Configuration.register();
         bl4ckscor3.mod.particleculling.ParticleCulling.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLLoadCompleteEvent) {
         this.onLoadComplete((FMLLoadCompleteEvent)event);
      }

   }

   public void onLoadComplete(FMLLoadCompleteEvent event) {
      updateIgnoredParticles();
      cullThread = new CullThread();
   }

   public static void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, ParticleCulling::onConfigChanged);
   }

   public static void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("particleculling")) {
         ConfigManager.sync("particleculling", Type.INSTANCE);
         updateIgnoredParticles();
      }

   }

   private static void updateIgnoredParticles() {
      ParticleCulling.Configuration.ignoredParticleClasses = new ArrayList();

      for(String className : ParticleCulling.Configuration.ignoredParticles) {
         ParticleCulling.Configuration.ignoredParticleClasses.add(className);
      }

   }

   @Config(
      modid = "particleculling"
   )
   public static class Configuration {
      @Comment({"Set this to false if you do not want to cull particles that technically are in the player's view but are obstructed by blocks."})
      public static boolean cullBehindBlocks = true;
      @Comment({"Set this to true if you don't want particles to be rendered behind glass and other transparent blocks. This does nothing if \"cullBehindBlocks\" is turned off."})
      public static boolean cullBehindGlass = false;
      @Comment({"Set this to false to disable particle culling while being in spectator mode. This is useful to take screenshots without having particles removed."})
      public static boolean cullInSpectator = true;
      @Comment({"The minimum amount of blocks behind which particles start to get culled. Only effective if \"cullBehindBlocks\" is turned on."})
      @RangeInt(
         min = 1,
         max = 50
      )
      public static int blockBuffer = 1;
      @Comment({"Set this to false to disable all of particle culling's features."})
      public static boolean cullingEnabled = true;
      @Comment({"Add particle classes here that should be ignored by Particle Culling.", "Example: To ignore Minecraft's breaking particles and any derivates, add \"net.minecraft.client.particle.ParticleBreaking\" to the list"})
      public static String[] ignoredParticles = new String[]{"org.orecruncher.dsurround.client.fx.particle.mote.ParticleCollection", "com.TominoCZ.FBP.particle.FBPParticleBlock", "xzeroair.trinkets.client.particles.ParticleGreed"};
      @Ignore
      public static List<String> ignoredParticleClasses;

      public Configuration() {
      }

      public static void register() {
         ConfigManager.register("particleculling", "", "general", null,
            ConfigEntry.value("cullBehindBlocks", boolean.class, () -> Configuration.cullBehindBlocks, v -> Configuration.cullBehindBlocks = (Boolean)v).comment("Set this to false if you do not want to cull particles that technically are in the player's view but are obstructed by blocks.").langKey("particleculling.general.cullbehindblocks"),
            ConfigEntry.value("cullBehindGlass", boolean.class, () -> Configuration.cullBehindGlass, v -> Configuration.cullBehindGlass = (Boolean)v).comment("Set this to true if you don't want particles to be rendered behind glass and other transparent blocks. This does nothing if \"cullBehindBlocks\" is turned off.").langKey("particleculling.general.cullbehindglass"),
            ConfigEntry.value("cullInSpectator", boolean.class, () -> Configuration.cullInSpectator, v -> Configuration.cullInSpectator = (Boolean)v).comment("Set this to false to disable particle culling while being in spectator mode. This is useful to take screenshots without having particles removed.").langKey("particleculling.general.cullinspectator"),
            ConfigEntry.value("blockBuffer", int.class, () -> Configuration.blockBuffer, v -> Configuration.blockBuffer = (Integer)v).comment("The minimum amount of blocks behind which particles start to get culled. Only effective if \"cullBehindBlocks\" is turned on.").langKey("particleculling.general.blockbuffer").range(1, 50),
            ConfigEntry.value("cullingEnabled", boolean.class, () -> Configuration.cullingEnabled, v -> Configuration.cullingEnabled = (Boolean)v).comment("Set this to false to disable all of particle culling's features.").langKey("particleculling.general.cullingenabled"),
            ConfigEntry.value("ignoredParticles", String[].class, () -> Configuration.ignoredParticles, v -> Configuration.ignoredParticles = (String[])v).comment("Add particle classes here that should be ignored by Particle Culling.", "Example: To ignore Minecraft's breaking particles and any derivates, add \"net.minecraft.client.particle.ParticleBreaking\" to the list").langKey("particleculling.general.ignoredparticles"));
      }
   }
}
