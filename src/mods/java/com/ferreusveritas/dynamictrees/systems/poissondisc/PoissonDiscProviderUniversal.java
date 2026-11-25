package com.ferreusveritas.dynamictrees.systems.poissondisc;

import com.ferreusveritas.dynamictrees.api.worldgen.IPoissonDiscProvider;
import com.ferreusveritas.dynamictrees.event.PoissonDiscProviderCreateEvent;
import com.ferreusveritas.dynamictrees.worldgen.BiomeRadiusCoordinator;
import com.ferreusveritas.dynamictrees.worldgen.TreeGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class PoissonDiscProviderUniversal {
   Map<Integer, IPoissonDiscProvider> providerMap = new HashMap();

   public PoissonDiscProviderUniversal() {
   }

   protected IPoissonDiscProvider createCircleProvider(World world) {
      BiomeRadiusCoordinator radiusCoordinator = new BiomeRadiusCoordinator(TreeGenerator.getTreeGenerator(), world);
      IPoissonDiscProvider candidate = new PoissonDiscProvider(radiusCoordinator);
      PoissonDiscProviderCreateEvent poissonDiscProviderCreateEvent = new PoissonDiscProviderCreateEvent(world, candidate);
      MinecraftForge.EVENT_BUS.post(poissonDiscProviderCreateEvent);
      return poissonDiscProviderCreateEvent.getPoissonDiscProvider();
   }

   public IPoissonDiscProvider getProvider(World world) {
      return (IPoissonDiscProvider)this.providerMap.computeIfAbsent(Integer.valueOf(world.provider.getDimension()), (d) -> {
         return this.createCircleProvider(world);
      });
   }

   public List<PoissonDisc> getPoissonDiscs(World world, int chunkX, int chunkY, int chunkZ) {
      IPoissonDiscProvider provider = this.getProvider(world);
      return provider.getPoissonDiscs(chunkX, chunkY, chunkZ);
   }

   public void loadWorld(World world) {
   }

   public void unloadWorld(World world) {
      this.providerMap.remove(Integer.valueOf(world.provider.getDimension()));
   }

   public void setChunkPoissonData(World world, int chunkX, int chunkY, int chunkZ, byte[] circleData) {
      this.getProvider(world).setChunkPoissonData(chunkX, chunkY, chunkZ, circleData);
   }

   public byte[] getChunkPoissonData(World world, int chunkX, int chunkY, int chunkZ) {
      return this.getProvider(world).getChunkPoissonData(chunkX, chunkY, chunkZ);
   }

   public void unloadChunkPoissonData(World world, int chunkX, int chunkY, int chunkZ) {
      this.getProvider(world).unloadChunkPoissonData(chunkX, chunkY, chunkZ);
   }
}
