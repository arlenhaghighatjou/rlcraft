package com.ferreusveritas.dynamictrees.event;

import com.ferreusveritas.dynamictrees.systems.poissondisc.PoissonDiscProviderUniversal;
import com.ferreusveritas.dynamictrees.worldgen.TreeGenerator;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ChunkDataEvent.Load;
import net.minecraftforge.event.world.ChunkDataEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PoissonDiscEventHandler {
   public PoissonDiscEventHandler() {
   }

   @SubscribeEvent
   public void onWorldUnload(Unload event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         TreeGenerator.getTreeGenerator().getCircleProvider().unloadWorld(world);
      }

   }

   @SubscribeEvent
   public void onChunkDataLoad(Load event) {
      World world = event.getWorld();
      if (!world.isRemote) {
         byte[] circleData = event.getData().getByteArray("GTCD");
         PoissonDiscProviderUniversal cp = TreeGenerator.getTreeGenerator().getCircleProvider();
         cp.setChunkPoissonData(world, event.getChunk().x, 0, event.getChunk().z, circleData);
      }

   }

   @SubscribeEvent
   public void onChunkDataSave(Save event) {
      World world = event.getWorld();
      PoissonDiscProviderUniversal cp = TreeGenerator.getTreeGenerator().getCircleProvider();
      byte[] circleData = cp.getChunkPoissonData(world, event.getChunk().x, 0, event.getChunk().z);
      NBTTagByteArray circleByteArray = new NBTTagByteArray(circleData);
      event.getData().setTag("GTCD", circleByteArray);
      if (!event.getChunk().isLoaded()) {
         cp.unloadChunkPoissonData(world, event.getChunk().x, 0, event.getChunk().z);
      }

   }
}
