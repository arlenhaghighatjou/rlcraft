package hunternif.mc.atlas.api.impl;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.TileAPI;
import hunternif.mc.atlas.client.BiomeTextureMap;
import hunternif.mc.atlas.client.TextureSet;
import hunternif.mc.atlas.client.TextureSetMap;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.ext.ExtBiomeData;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.ext.ExtTileTextureMap;
import hunternif.mc.atlas.ext.TileIdRegisteredEvent;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.bidirectional.PutBiomeTilePacket;
import hunternif.mc.atlas.network.client.DeleteCustomGlobalTilePacket;
import hunternif.mc.atlas.network.client.IntTilesPacket;
import hunternif.mc.atlas.network.client.ShortTilesPacket;
import hunternif.mc.atlas.network.client.TileNameIDPacket;
import hunternif.mc.atlas.network.client.TilesPacket;
import hunternif.mc.atlas.network.server.RegisterTileIdPacket;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.MathUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TileApiImpl implements TileAPI {
   private final Map<String, TileApiImpl.TileData> pendingTiles = new HashMap();

   public TileApiImpl() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   public TextureSet registerTextureSet(String name, ResourceLocation... textures) {
      TextureSet textureSet = new TextureSet(name, textures);
      TextureSetMap.instance().register(textureSet);
      return textureSet;
   }

   public void registerTextureSet(TextureSet textureSet) {
      TextureSetMap.instance().register(textureSet);
   }

   public void setBiomeTexture(int biomeID, String textureSetName, ResourceLocation... textures) {
      TextureSet set = new TextureSet(textureSetName, textures);
      TextureSetMap.instance().register(set);
      BiomeTextureMap.instance().setTexture(Biome.getBiomeForId(biomeID), set);
   }

   public void setBiomeTexture(Biome biome, String textureSetName, ResourceLocation... textures) {
      this.setBiomeTexture(Biome.getIdForBiome(biome), textureSetName, textures);
   }

   public void setBiomeTexture(int biomeID, TextureSet textureSet) {
      BiomeTextureMap.instance().setTexture(Biome.getBiomeForId(biomeID), textureSet);
   }

   public void setBiomeTexture(Biome biome, TextureSet textureSet) {
      this.setBiomeTexture(Biome.getIdForBiome(biome), textureSet);
   }

   public void setCustomTileTexture(String uniqueTileName, ResourceLocation... textures) {
      TextureSet set = new TextureSet(uniqueTileName, textures);
      TextureSetMap.instance().register(set);
      this.setCustomTileTexture(uniqueTileName, set);
   }

   public void setCustomTileTexture(String uniqueTileName, TextureSet textureSet) {
      ExtTileTextureMap.instance().setTexture(uniqueTileName, textureSet);
   }

   public void putBiomeTile(World world, int atlasID, int biomeID, int chunkX, int chunkZ) {
      int dimension = world.provider.getDimension();
      PutBiomeTilePacket packet = new PutBiomeTilePacket(atlasID, dimension, chunkX, chunkZ, biomeID);
      if (world.isRemote) {
         PacketDispatcher.sendToServer(packet);
      } else {
         AtlasData data = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
         Tile tile = new Tile(biomeID);
         data.setTile(dimension, chunkX, chunkZ, tile);

         for(EntityPlayer syncedPlayer : data.getSyncedPlayers()) {
            PacketDispatcher.sendTo(new PutBiomeTilePacket(atlasID, dimension, chunkX, chunkZ, biomeID), (EntityPlayerMP)syncedPlayer);
         }
      }

   }

   public void putBiomeTile(World world, int atlasID, Biome biome, int chunkX, int chunkZ) {
      this.putBiomeTile(world, atlasID, Biome.getIdForBiome(biome), chunkX, chunkZ);
   }

   public void putCustomTile(World world, int atlasID, String tileName, int chunkX, int chunkZ) {
      if (tileName == null) {
         Log.error("Attempted to put custom tile with null name");
      } else {
         if (world.isRemote) {
            int biomeID = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
            if (biomeID != -1) {
               this.putBiomeTile(world, atlasID, biomeID, chunkX, chunkZ);
            } else {
               this.pendingTiles.put(tileName, new TileApiImpl.TileData(world, atlasID, chunkX, chunkZ));
               PacketDispatcher.sendToServer(new RegisterTileIdPacket(tileName));
            }
         } else {
            int biomeID = ExtTileIdMap.instance().getPseudoBiomeID(tileName);
            if (biomeID == -1) {
               biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
               TileNameIDPacket packet = new TileNameIDPacket();
               packet.put(tileName, biomeID);
               PacketDispatcher.sendToAll(packet);
            }

            this.putBiomeTile(world, atlasID, biomeID, chunkX, chunkZ);
         }

      }
   }

   public void putCustomGlobalTile(World world, String tileName, int chunkX, int chunkZ) {
      if (tileName == null) {
         Log.error("Attempted to put custom global tile with null name");
      } else if (world.isRemote) {
         Log.warn("Client attempted to put global tile");
      } else {
         boolean isIdRegistered = ExtTileIdMap.instance().getPseudoBiomeID(tileName) != -1;
         int biomeID = ExtTileIdMap.instance().getOrCreatePseudoBiomeID(tileName);
         ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
         data.setBiomeIdAt(world.provider.getDimension(), chunkX, chunkZ, biomeID);
         if (!isIdRegistered) {
            TileNameIDPacket packet = new TileNameIDPacket();
            packet.put(tileName, biomeID);
            PacketDispatcher.sendToAll(packet);
         }

         int dimension = world.provider.getDimension();
         boolean useInt = MathUtil.exceedsShort(chunkX, chunkZ);
         TilesPacket packet = (TilesPacket)(useInt ? new IntTilesPacket(dimension) : new ShortTilesPacket(dimension));
         packet.addTile(chunkX, chunkZ, biomeID);
         PacketDispatcher.sendToAll(packet);
      }
   }

   @SubscribeEvent
   public void onTileIdRegistered(TileIdRegisteredEvent event) {
      for(Entry<String, Integer> entry : event.nameToIdMap.entrySet()) {
         TileApiImpl.TileData tile = (TileApiImpl.TileData)this.pendingTiles.remove(entry.getKey());
         if (tile != null) {
            this.putBiomeTile(tile.world, tile.atlasID, ((Integer)entry.getValue()).intValue(), tile.x, tile.z);
         }
      }

   }

   public void deleteCustomGlobalTile(World world, int chunkX, int chunkZ) {
      if (world.isRemote) {
         Log.warn("Client attempted to delete global tile");
      } else {
         ExtBiomeData data = AntiqueAtlasMod.extBiomeData.getData();
         int dimension = world.provider.getDimension();
         if (data.getBiomeIdAt(dimension, chunkX, chunkZ) != -1) {
            data.removeBiomeAt(dimension, chunkX, chunkZ);
            PacketDispatcher.sendToAll(new DeleteCustomGlobalTilePacket(dimension, chunkX, chunkZ));
         }

      }
   }

   private static class TileData {
      final World world;
      final int atlasID;
      final int x;
      final int z;

      TileData(World world, int atlasID, int x, int z) {
         this.world = world;
         this.atlasID = atlasID;
         this.x = x;
         this.z = z;
      }
   }
}
