package org.orecruncher.dsurround.server.services;

import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.network.Network;
import org.orecruncher.dsurround.network.PacketServerData;
import org.orecruncher.dsurround.registry.RegistryManager;

public final class ServiceManager extends Service {
   private static final ServiceManager INSTANCE = new ServiceManager();
   private final List<Service> services = new ArrayList();
   private long tpsCount = 0L;

   private ServiceManager() {
      super("ServiceManager");
   }

   private void addService(Service service) {
      this.services.add(service);
   }

   private void clearServices() {
      this.services.clear();
   }

   private void init0() {
      for(Service s : this.services) {
         s.init();
         MinecraftForge.EVENT_BUS.register(s);
      }

   }

   private void fini0() {
      for(Service s : this.services) {
         s.fini();
         MinecraftForge.EVENT_BUS.unregister(s);
      }

   }

   public static void initialize() {
      INSTANCE.addService(INSTANCE);
      INSTANCE.addService(new AtmosphereService());
      INSTANCE.addService(new SpeechBubbleService());
      INSTANCE.addService(new EnvironmentService());
      INSTANCE.init0();
   }

   public static void deinitialize() {
      INSTANCE.fini0();
      INSTANCE.clearServices();
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onWorldLoad(Load e) {
      RegistryManager.DIMENSION.loading(e.getWorld());
   }

   private static long mean(@Nonnull long[] values) {
      long sum = 0L;

      for(long v : values) {
         sum += v;
      }

      return sum / (long)values.length;
   }

   @SubscribeEvent
   public void tickEvent(@Nonnull ServerTickEvent event) {
      if (ModOptions.logging.reportServerStats && event.phase == Phase.END) {
         if (++this.tpsCount % 20L == 0L) {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            Int2DoubleOpenHashMap map = new Int2DoubleOpenHashMap();

            for(Integer dim : DimensionManager.getIDs()) {
               map.put(dim.intValue(), (double)mean((long[])server.worldTickTimes.get(dim)) / 1000000.0D);
            }

            double meanTickTime = (double)mean(server.tickTimeArray) / 1000000.0D;
            int total = (int)(Runtime.getRuntime().totalMemory() / 1024L / 1024L);
            int max = (int)(Runtime.getRuntime().maxMemory() / 1024L / 1024L);
            int free = (int)(Runtime.getRuntime().freeMemory() / 1024L / 1024L);
            PacketServerData packet = new PacketServerData(map, meanTickTime, free, total, max);
            Network.sendToAll(packet);
         }
      }
   }
}
