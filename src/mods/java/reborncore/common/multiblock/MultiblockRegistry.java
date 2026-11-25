package reborncore.common.multiblock;

import java.util.HashMap;
import java.util.Set;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import reborncore.RebornCore;

public class MultiblockRegistry {
   private static HashMap<World, MultiblockWorldRegistry> registries = new HashMap();

   public MultiblockRegistry() {
   }

   public static void tickStart(World world) {
      if (registries.containsKey(world)) {
         MultiblockWorldRegistry registry = (MultiblockWorldRegistry)registries.get(world);
         registry.processMultiblockChanges();
         registry.tickStart();
      }

   }

   public static void onChunkLoaded(World world, Chunk chunk) {
      if (registries.containsKey(world)) {
         ((MultiblockWorldRegistry)registries.get(world)).onChunkLoaded(chunk);
      }

   }

   public static void onPartAdded(World world, IMultiblockPart part) {
      MultiblockWorldRegistry registry = getOrCreateRegistry(world);
      registry.onPartAdded(part);
   }

   public static void onPartRemovedFromWorld(World world, IMultiblockPart part) {
      if (registries.containsKey(world)) {
         ((MultiblockWorldRegistry)registries.get(world)).onPartRemovedFromWorld(part);
      }

   }

   public static void onWorldUnloaded(World world) {
      if (registries.containsKey(world)) {
         ((MultiblockWorldRegistry)registries.get(world)).onWorldUnloaded();
         registries.remove(world);
      }

   }

   public static void addDirtyController(World world, MultiblockControllerBase controller) {
      if (registries.containsKey(world)) {
         ((MultiblockWorldRegistry)registries.get(world)).addDirtyController(controller);
      } else {
         RebornCore.logHelper.error("Adding a dirty controller to a world that has no registered controllers! This is most likey not an issue with reborn core, please check the full log file for more infomation!");
      }

   }

   public static void addDeadController(World world, MultiblockControllerBase controller) {
      if (registries.containsKey(world)) {
         ((MultiblockWorldRegistry)registries.get(world)).addDeadController(controller);
      } else {
         RebornCore.logHelper.warn(String.format("Controller %d in world %s marked as dead, but that world is not tracked! Controller is being ignored.", controller.hashCode(), world));
      }

   }

   public static Set<MultiblockControllerBase> getControllersFromWorld(World world) {
      return registries.containsKey(world) ? ((MultiblockWorldRegistry)registries.get(world)).getControllers() : null;
   }

   private static MultiblockWorldRegistry getOrCreateRegistry(World world) {
      if (registries.containsKey(world)) {
         return (MultiblockWorldRegistry)registries.get(world);
      } else {
         MultiblockWorldRegistry newRegistry = new MultiblockWorldRegistry(world);
         registries.put(world, newRegistry);
         return newRegistry;
      }
   }
}
