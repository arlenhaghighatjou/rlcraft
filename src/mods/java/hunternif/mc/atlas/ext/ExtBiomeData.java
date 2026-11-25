package hunternif.mc.atlas.ext;

import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.IntTilesPacket;
import hunternif.mc.atlas.network.client.ShortTilesPacket;
import hunternif.mc.atlas.network.client.TilesPacket;
import hunternif.mc.atlas.util.IntVec2;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.MathUtil;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.storage.WorldSavedData;

public class ExtBiomeData extends WorldSavedData {
   private static final int VERSION = 1;
   private static final String TAG_VERSION = "aaVersion";
   private static final String TAG_DIMENSION_MAP_LIST = "dimMap";
   private static final String TAG_DIMENSION_ID = "dimID";
   private static final String TAG_BIOME_IDS = "biomeIDs";
   private final Map<Integer, Map<IntVec2, Integer>> dimensionMap = new ConcurrentHashMap(2, 0.75F, 2);
   private final IntVec2 tempCoords = new IntVec2(0.0D, 0.0D);

   public ExtBiomeData(String key) {
      super(key);
   }

   public void readFromNBT(NBTTagCompound compound) {
      int version = compound.getInteger("aaVersion");
      if (version < 1) {
         Log.warn("Outdated atlas data format! Was %d but current is %d", version, Integer.valueOf(1));
         this.markDirty();
      }

      NBTTagList dimensionMapList = compound.getTagList("dimMap", 10);

      for(int d = 0; d < dimensionMapList.tagCount(); ++d) {
         NBTTagCompound tag = dimensionMapList.getCompoundTagAt(d);
         int dimensionID = tag.getInteger("dimID");
         Map<IntVec2, Integer> biomeMap = this.getBiomesInDimension(dimensionID);
         int[] intArray = tag.getIntArray("biomeIDs");

         for(int i = 0; i < intArray.length; i += 3) {
            IntVec2 coords = new IntVec2((double)intArray[i], (double)intArray[i + 1]);
            biomeMap.put(coords, Integer.valueOf(intArray[i + 2]));
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setInteger("aaVersion", 1);
      NBTTagList dimensionMapList = new NBTTagList();

      for(Integer dimension : this.dimensionMap.keySet()) {
         NBTTagCompound tag = new NBTTagCompound();
         tag.setInteger("dimID", dimension.intValue());
         Map<IntVec2, Integer> biomeMap = this.getBiomesInDimension(dimension.intValue());
         int[] intArray = new int[biomeMap.size() * 3];
         int i = 0;

         for(Entry<IntVec2, Integer> entry : biomeMap.entrySet()) {
            intArray[i++] = ((IntVec2)entry.getKey()).x;
            intArray[i++] = ((IntVec2)entry.getKey()).y;
            intArray[i++] = ((Integer)entry.getValue()).intValue();
         }

         tag.setIntArray("biomeIDs", intArray);
         dimensionMapList.appendTag(tag);
      }

      compound.setTag("dimMap", dimensionMapList);
      return compound;
   }

   private Map<IntVec2, Integer> getBiomesInDimension(int dimension) {
      return (Map)this.dimensionMap.computeIfAbsent(Integer.valueOf(dimension), (k) -> {
         return new ConcurrentHashMap(2, 0.75F, 2);
      });
   }

   public int getBiomeIdAt(int dimension, int x, int z) {
      Integer biomeID = (Integer)this.getBiomesInDimension(dimension).get(this.tempCoords.set(x, z));
      return biomeID == null ? -1 : biomeID.intValue();
   }

   public void setBiomeIdAt(int dimension, int x, int z, int biomeID) {
      this.getBiomesInDimension(dimension).put(new IntVec2((double)x, (double)z), Integer.valueOf(biomeID));
      this.markDirty();
   }

   public void removeBiomeAt(int dimension, int x, int z) {
      this.getBiomesInDimension(dimension).remove(this.tempCoords.set(x, z));
      this.markDirty();
   }

   public void syncOnPlayer(EntityPlayer player) {
      for(Integer dimension : this.dimensionMap.keySet()) {
         Map<IntVec2, Integer> biomes = this.getBiomesInDimension(dimension.intValue());
         boolean useInt = biomes.keySet().stream().anyMatch((p) -> {
            return MathUtil.exceedsShort(p.x, p.y);
         });
         TilesPacket packet = (TilesPacket)(useInt ? new IntTilesPacket(dimension.intValue()) : new ShortTilesPacket(dimension.intValue()));

         for(Entry<IntVec2, Integer> entry : biomes.entrySet()) {
            packet.addTile(((IntVec2)entry.getKey()).x, ((IntVec2)entry.getKey()).y, ((Integer)entry.getValue()).intValue());
         }

         PacketDispatcher.sendTo(packet, (EntityPlayerMP)player);
      }

      Log.info("Sent custom biome data to player %s", player.getName());
   }
}
