package com.teamacronymcoders.base.multiblocksystem;

import java.util.HashMap;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

public final class MultiblockRegistry implements IMultiblockRegistry {
   private HashMap<World, MultiblockWorldRegistry> _registries = new HashMap(2);
   public static final MultiblockRegistry INSTANCE = new MultiblockRegistry();

   public void onPartAdded(World world, IMultiblockPart part) {
      MultiblockWorldRegistry registry;
      if (this._registries.containsKey(world)) {
         registry = (MultiblockWorldRegistry)this._registries.get(world);
      } else {
         registry = new MultiblockWorldRegistry(world);
         this._registries.put(world, registry);
      }

      registry.onPartAdded(part);
   }

   public void onPartRemovedFromWorld(World world, IMultiblockPart part) {
      if (this._registries.containsKey(world)) {
         ((MultiblockWorldRegistry)this._registries.get(world)).onPartRemovedFromWorld(part);
      }

   }

   public void addDeadController(World world, MultiblockControllerBase controller) {
      if (this._registries.containsKey(world)) {
         ((MultiblockWorldRegistry)this._registries.get(world)).addDeadController(controller);
      } else {
         FMLLog.warning("Controller %d in world %s marked as dead, but that world is not tracked! Controller is being ignored.", new Object[]{controller.hashCode(), world});
      }

   }

   public void addDirtyController(World world, MultiblockControllerBase controller) {
      if (this._registries.containsKey(world)) {
         ((MultiblockWorldRegistry)this._registries.get(world)).addDirtyController(controller);
      } else {
         throw new IllegalArgumentException("Adding a dirty controller to a world that has no registered controllers!");
      }
   }

   protected void tickStart(World world) {
      if (this._registries.containsKey(world)) {
         MultiblockWorldRegistry registry = (MultiblockWorldRegistry)this._registries.get(world);
         registry.processMultiblockChanges();
         registry.tickStart();
      }

   }

   protected void onChunkLoaded(World world, int chunkX, int chunkZ) {
      if (this._registries.containsKey(world)) {
         ((MultiblockWorldRegistry)this._registries.get(world)).onChunkLoaded(chunkX, chunkZ);
      }

   }

   protected void onWorldUnloaded(World world) {
      if (this._registries.containsKey(world)) {
         ((MultiblockWorldRegistry)this._registries.get(world)).onWorldUnloaded();
         this._registries.remove(world);
      }

   }

   private MultiblockRegistry() {
   }
}
