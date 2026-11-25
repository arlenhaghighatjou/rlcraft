package hunternif.mc.atlas.ext;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import hunternif.mc.atlas.network.PacketDispatcher;
import hunternif.mc.atlas.network.client.TileNameIDPacket;
import hunternif.mc.atlas.util.SaveData;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class ExtTileIdMap extends SaveData {
   private static final ExtTileIdMap INSTANCE = new ExtTileIdMap();
   public static final String TILE_VILLAGE_LIBRARY = "npcVillageLibrary";
   public static final String TILE_VILLAGE_SMITHY = "npcVillageSmithy";
   public static final String TILE_VILLAGE_L_HOUSE = "npcVillageLHouse";
   public static final String TILE_VILLAGE_FARMLAND_SMALL = "npcVillageFarmlandSmall";
   public static final String TILE_VILLAGE_FARMLAND_LARGE = "npcVillageFarmlandLarge";
   public static final String TILE_VILLAGE_WELL = "npcVillageWell";
   public static final String TILE_VILLAGE_TORCH = "npcVillageTorch";
   public static final String TILE_VILLAGE_HUT = "npcVillageHut";
   public static final String TILE_VILLAGE_SMALL_HOUSE = "npcVillageSmallHouse";
   public static final String TILE_VILLAGE_BUTCHERS_SHOP = "npcVillageButchersShop";
   public static final String TILE_VILLAGE_CHURCH = "npcVillageChurch";
   public static final String TILE_RAVINE = "ravine";
   public static final String TILE_LAVA = "lava";
   public static final String TILE_LAVA_SHORE = "lavaShore";
   public static final String TILE_NETHER_BRIDGE = "netherBridge";
   public static final String TILE_NETHER_BRIDGE_X = "netherBridgeX";
   public static final String TILE_NETHER_BRIDGE_Z = "netherBridgeZ";
   public static final String TILE_NETHER_BRIDGE_END_X = "netherBridgeEndX";
   public static final String TILE_NETHER_BRIDGE_END_Z = "netherBridgeEndZ";
   public static final String TILE_NETHER_BRIDGE_GATE = "netherBridgeGate";
   public static final String TILE_NETHER_TOWER = "netherTower";
   public static final String TILE_NETHER_WALL = "netherWall";
   public static final String TILE_NETHER_HALL = "netherHall";
   public static final String TILE_NETHER_FORT_STAIRS = "netherFortStairs";
   public static final String TILE_NETHER_THRONE = "netherThrone";
   public static final String TILE_END_ISLAND = "endIsland";
   public static final String TILE_END_ISLAND_PLANTS = "endIslandPlants";
   public static final String TILE_END_VOID = "endVoid";
   public static final int NOT_FOUND = -1;
   private int lastID = -1;
   private final BiMap<String, Integer> nameToIdMap = HashBiMap.create();

   public ExtTileIdMap() {
   }

   public static ExtTileIdMap instance() {
      return INSTANCE;
   }

   public int getOrCreatePseudoBiomeID(String uniqueName) {
      Integer id = (Integer)this.nameToIdMap.get(uniqueName);
      if (id == null) {
         id = this.findNewID();
         this.nameToIdMap.put(uniqueName, id);
         this.markDirty();
      }

      return id.intValue();
   }

   public int getPseudoBiomeID(String uniqueName) {
      Integer id = (Integer)this.nameToIdMap.get(uniqueName);
      return id == null ? -1 : id.intValue();
   }

   public String getPseudoBiomeName(int id) {
      return (String)this.nameToIdMap.inverse().get(Integer.valueOf(id));
   }

   private int findNewID() {
      while(this.lastID > -32768 && this.nameToIdMap.inverse().containsKey(Integer.valueOf(--this.lastID))) {
         ;
      }

      return this.lastID;
   }

   public void setPseudoBiomeID(String uniqueName, int id) {
      this.nameToIdMap.forcePut(uniqueName, Integer.valueOf(id));
   }

   Map<String, Integer> getMap() {
      return this.nameToIdMap;
   }

   public void syncOnPlayer(EntityPlayer player) {
      PacketDispatcher.sendTo(new TileNameIDPacket(this.nameToIdMap), (EntityPlayerMP)player);
   }
}
