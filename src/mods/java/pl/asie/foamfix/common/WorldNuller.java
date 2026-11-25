package pl.asie.foamfix.common;

import com.google.common.collect.Lists;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import pl.asie.foamfix.FoamFix;
import pl.asie.foamfix.shared.FoamFixShared;

public class WorldNuller {
   private final List<WorldNuller.UnloadEntry> unload_queue_client;

   private WorldNuller() {
      this.unload_queue_client = Lists.newArrayList();
   }

   public static void init() {
   }

   public static void initClient() {
      FoamFix.getLogger().info("Registered client-side world unload notifier!");
      MinecraftForge.EVENT_BUS.register(new WorldNuller.ClientNuller());
   }

   @SubscribeEvent
   public void queueWorldNull(Unload event) {
      if (event.getWorld().isRemote) {
         this.unload_queue_client.add(new WorldNuller.UnloadEntry(event.getWorld()));
      }

   }

   void tick(TickEvent event, boolean server) {
      if (event.phase == Phase.END) {
         List<WorldNuller.UnloadEntry> unload_queue = server ? Collections.emptyList() : this.unload_queue_client;
         if (!unload_queue.isEmpty()) {
            long now = System.currentTimeMillis();
            unload_queue.removeIf((e) -> {
               return e.tick(now);
            });
         }
      }
   }

   public static class ClientNuller extends WorldNuller {
      public ClientNuller() {
      }

      @SubscribeEvent
      public void clientTick(ClientTickEvent event) {
         this.tick(event, false);
      }
   }

   public static class ServerNuller extends WorldNuller {
      public ServerNuller() {
      }

      @SubscribeEvent
      public void serverTick(ServerTickEvent event) {
         this.tick(event, true);
      }
   }

   static class UnloadEntry {
      WeakReference<World> worldRef;
      long timeOfUnload;
      String name;
      static boolean spam = true;

      UnloadEntry(World world) {
         this.worldRef = new WeakReference(world);
         this.timeOfUnload = System.currentTimeMillis();
         this.name = "<" + world.toString() + " " + world.provider.getDimension() + " " + world.getChunkProvider().makeString() + ">";
      }

      static void log(String msg) {
         FoamFix.getLogger().info(msg);
      }

      boolean tick(long now) {
         long passedTime = now - this.timeOfUnload;
         int delay = FoamFixShared.config.gbWorldUnloadTime * 1000;
         if (passedTime < (long)delay) {
            return false;
         } else {
            World world = (World)this.worldRef.get();
            if (world != null && FoamFixShared.config.gbForgeGCNonUnloaded) {
               world = null;
               log("Unloaded world " + this.name + " was not garbage collected, forcing GC as requested...");
               System.gc();
               world = (World)this.worldRef.get();
            }

            int secs = delay / 1000;
            if (world == null) {
               log("Unloaded world " + this.name + " was garbage collected.");
               return true;
            } else {
               if (world instanceof WorldServer) {
                  if (DimensionManager.getWorld(world.provider.getDimension()) == world) {
                     return true;
                  }

                  MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                  if (server != null && server.worlds != null) {
                     for(WorldServer w : server.worlds) {
                        if (world == w) {
                           return true;
                        }
                     }
                  }
               }

               log("Unloaded world " + this.name + " is still hanging around after " + secs + " seconds.");
               if (spam) {
                  log("This may be due to a world leak, or there is little memory pressure.");
                  log("Use the relevant FoamFix options to adjust the wait time.");
                  spam = false;
               }

               return true;
            }
         }
      }
   }
}
