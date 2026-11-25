package slimeknights.mantle.pulsar.control;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.config.IConfiguration;
import slimeknights.mantle.pulsar.flightpath.Flightpath;
import slimeknights.mantle.pulsar.flightpath.IExceptionHandler;
import slimeknights.mantle.pulsar.flightpath.lib.AnnotationLocator;
import slimeknights.mantle.pulsar.internal.BusExceptionHandler;
import slimeknights.mantle.pulsar.internal.Configuration;
import slimeknights.mantle.pulsar.internal.CrashHandler;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.mantle.pulsar.pulse.PulseMeta;

@ParametersAreNonnullByDefault
public class PulseManager {
   private Logger log;
   private final boolean useConfig;
   private static final Map<String, Map<Object, PulseMeta>> ALL_PULSES = new HashMap();
   private final Map<Object, PulseMeta> pulses = new LinkedHashMap();
   private final Flightpath flightpath = new Flightpath(new AnnotationLocator(Subscribe.class));
   private boolean blockNewRegistrations = false;
   private boolean configLoaded = false;
   private IConfiguration conf;
   private String id;

   public PulseManager(String configName) {
      this.init();
      this.useConfig = true;
      this.conf = new Configuration(configName, this.log);
   }

   public PulseManager(IConfiguration config) {
      this.init();
      this.useConfig = true;
      this.conf = config;
   }

   private void init() {
      String modId = Loader.instance().activeModContainer().getModId();
      this.id = modId;
      this.log = LogManager.getLogger("Pulsar-" + modId);
      this.flightpath.setExceptionHandler(new BusExceptionHandler(modId));
      FMLCommonHandler.instance().registerCrashCallable(new CrashHandler(modId, this));
      ALL_PULSES.put(modId, this.pulses);
      this.attachToContainerEventBus(this);
   }

   public void setPulseExceptionHandler(IExceptionHandler handler) {
      this.flightpath.setExceptionHandler(handler);
   }

   public void registerPulse(Object pulse) {
      if (this.blockNewRegistrations) {
         throw new RuntimeException("A mod tried to register a plugin after preinit! Pulse: " + pulse);
      } else {
         if (!this.configLoaded) {
            this.conf.load();
            this.configLoaded = true;
         }

         boolean missingDeps = false;

         String id;
         String description;
         String deps;
         String pulseDeps;
         boolean forced;
         boolean enabled;
         boolean defaultEnabled;
         try {
            Pulse p = (Pulse)pulse.getClass().getAnnotation(Pulse.class);
            id = p.id();
            description = p.description();
            deps = p.modsRequired();
            pulseDeps = p.pulsesRequired();
            forced = p.forced();
            enabled = p.defaultEnable();
            defaultEnabled = p.defaultEnable();
         } catch (NullPointerException var15) {
            throw new RuntimeException("Could not parse @Pulse annotation for Pulse: " + pulse);
         }

         if (description.equals("")) {
            description = null;
         }

         if (!deps.equals("")) {
            String[] parsedDeps = deps.split(";");

            for(String s : parsedDeps) {
               if (!Loader.isModLoaded(s)) {
                  this.log.info("Skipping Pulse " + id + "; missing dependency: " + s);
                  missingDeps = true;
                  enabled = false;
                  break;
               }
            }
         }

         PulseMeta meta = new PulseMeta(id, description, forced, enabled, defaultEnabled);
         meta.setMissingDeps(missingDeps || !this.hasRequiredPulses(meta, pulseDeps));
         meta.setEnabled(this.getEnabledFromConfig(meta));
         if (meta.isEnabled()) {
            this.pulses.put(pulse, meta);
            this.flightpath.register(pulse);
            MinecraftForge.EVENT_BUS.register(pulse);
         }

      }
   }

   private void attachToContainerEventBus(Object obj) {
      ModContainer cnt = Loader.instance().activeModContainer();
      this.log.debug("Attaching [" + obj + "] to event bus for container [" + cnt + "]");

      try {
         FMLModContainer mc = (FMLModContainer)cnt;
         Field ebf = mc.getClass().getDeclaredField("eventBus");
         boolean access = ebf.isAccessible();
         ebf.setAccessible(true);
         EventBus eb = (EventBus)ebf.get(mc);
         ebf.setAccessible(access);
         eb.register(obj);
      } catch (NoSuchFieldException var7) {
         throw new RuntimeException("Pulsar >> Incompatible FML mod container (missing eventBus field) - wrong Forge version?");
      } catch (IllegalAccessException var8) {
         throw new RuntimeException("Pulsar >> Security Manager blocked access to eventBus on mod container. Cannot continue.");
      } catch (ClassCastException var9) {
         throw new RuntimeException("Pulsar >> Something in the mod container had the wrong type? " + var9.getMessage());
      }
   }

   @Subscribe
   public void propagateEvent(Object evt) {
      if (evt instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)evt);
      }

      this.flightpath.post(evt);
   }

   private boolean getEnabledFromConfig(PulseMeta meta) {
      return !meta.isForced() && this.useConfig ? this.conf.isModuleEnabled(meta) : true;
   }

   private void preInit(FMLPreInitializationEvent evt) {
      if (!this.blockNewRegistrations) {
         this.conf.flush();
      }

      this.blockNewRegistrations = true;
   }

   private boolean hasRequiredPulses(PulseMeta meta, String deps) {
      if (!deps.equals("")) {
         String[] parsedDeps = deps.split(";");

         for(String s : parsedDeps) {
            if (!this.isPulseLoaded(s)) {
               this.log.info("Skipping Pulse " + meta.getId() + "; missing pulse: " + s);
               return false;
            }
         }
      }

      return true;
   }

   public boolean isPulseLoaded(String pulseId) {
      return pulseId.contains(":") ? isPulseLoadedGlobal(pulseId) : isPulseLoaded(this.pulses, pulseId);
   }

   public static boolean isPulseLoadedGlobal(String pulseId) {
      ResourceLocation loc = new ResourceLocation(pulseId);
      Map<Object, PulseMeta> pulses = (Map)ALL_PULSES.get(loc.getResourceDomain());
      return pulses != null ? isPulseLoaded(pulses, loc.getResourcePath()) : false;
   }

   private static boolean isPulseLoaded(Map<Object, PulseMeta> pulses, String pulseId) {
      for(Entry<Object, PulseMeta> entry : pulses.entrySet()) {
         if (((PulseMeta)entry.getValue()).getId().equalsIgnoreCase(pulseId)) {
            return true;
         }
      }

      return false;
   }

   public Collection<PulseMeta> getAllPulseMetadata() {
      return this.pulses.values();
   }

   public String toString() {
      return "PulseManager[" + this.id + "]";
   }
}
