package hunternif.mc.atlas.ext.watcher.impl;

import com.google.common.collect.Sets;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.ext.watcher.IStructureWatcher;
import hunternif.mc.atlas.ext.watcher.StructureWatcher;
import hunternif.mc.atlas.ext.watcher.WatcherPos;
import hunternif.mc.atlas.util.MathUtil;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.apache.commons.lang3.tuple.Pair;

public class StructureWatcherFortress implements IStructureWatcher {
   private static final String ROOFED = "NeSCLT";
   private static final String ROOFED2 = "NeSCR";
   private static final String ROOFED_STAIRS = "NeCCS";
   private static final String ROOFED3 = "NeCTB";
   private static final String ROOFED4 = "NeSC";
   private static final String BRIDGE_GATE = "NeRC";
   private static final String ROOFED_CROSS = "NeSCSC";
   private static final String BRIDGE_CROSS = "NeBCr";
   private static final String START = "NeStart";
   private static final String BRIDGE = "NeBS";
   private static final String BRIDGE_END = "NeBEF";
   private static final String ENTRANCE = "NeCE";
   private static final String WART_STAIRS = "NeCSR";
   private static final String THRONE = "NeMT";
   private static final String TOWER = "NeSR";
   private final Set<WatcherPos> visited = new HashSet();

   public StructureWatcherFortress() {
      StructureWatcher.INSTANCE.addWatcher(this);
   }

   @Nonnull
   public Set<WatcherPos> getVisited() {
      return this.visited;
   }

   public boolean isDimensionValid(DimensionType type) {
      return type.getId() == -1;
   }

   @Nullable
   public NBTTagCompound getStructureData(@Nonnull World world) {
      MapGenStructureData data = (MapGenStructureData)world.getPerWorldStorage().getOrLoadData(MapGenStructureData.class, "Fortress");
      return data == null ? null : data.getTagCompound();
   }

   @Nonnull
   public Set<Pair<WatcherPos, String>> visitStructure(@Nonnull World world, @Nonnull NBTTagCompound structureTag) {
      Set<Pair<WatcherPos, String>> visits = Sets.newHashSet();

      for(String coords : structureTag.getKeySet()) {
         if (WatcherPos.POS_PATTERN.matcher(coords).matches()) {
            WatcherPos pos = new WatcherPos(coords);
            if (!this.visited.contains(pos)) {
               NBTTagCompound tag = structureTag.getCompoundTag(coords);
               this.visitFortress(world, tag);
               this.visited.add(pos);
               visits.add(Pair.of(pos, "Nether Fortress"));
            }
         }
      }

      return visits;
   }

   private void visitFortress(World world, NBTTagCompound tag) {
      NBTTagList children = tag.getTagList("Children", 10);

      for(int i = 0; i < children.tagCount(); ++i) {
         NBTTagCompound child = children.getCompoundTagAt(i);
         String childID = child.getString("id");
         StructureBoundingBox boundingBox = new StructureBoundingBox(child.getIntArray("BB"));
         if ("NeBS".equals(childID)) {
            if (boundingBox.getXSize() > 16) {
               String tileName = "netherBridgeX";
               int chunkZ = MathUtil.getCenter(boundingBox).getZ() >> 4;

               for(int x = boundingBox.minX; x < boundingBox.maxX; x += 16) {
                  int chunkX = x >> 4;
                  if (noTileAt(world, chunkX, chunkZ)) {
                     AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
                  }
               }
            } else {
               String tileName = "netherBridgeZ";
               int chunkX = MathUtil.getCenter(boundingBox).getX() >> 4;

               for(int z = boundingBox.minZ; z < boundingBox.maxZ; z += 16) {
                  int chunkZ = z >> 4;
                  if (noTileAt(world, chunkX, chunkZ)) {
                     AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
                  }
               }
            }
         } else if ("NeBEF".equals(childID)) {
            String tileName;
            int chunkX;
            int chunkZ;
            if (boundingBox.getXSize() > boundingBox.getZSize()) {
               tileName = "netherBridgeEndX";
               chunkX = boundingBox.minX >> 4;
               chunkZ = MathUtil.getCenter(boundingBox).getZ() >> 4;
            } else {
               tileName = "netherBridgeEndZ";
               chunkX = MathUtil.getCenter(boundingBox).getX() >> 4;
               chunkZ = boundingBox.minZ >> 4;
            }

            if (noTileAt(world, chunkX, chunkZ)) {
               AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
            }
         } else {
            int chunkX = MathUtil.getCenter(boundingBox).getX() >> 4;
            int chunkZ = MathUtil.getCenter(boundingBox).getZ() >> 4;
            if ("NeRC".equals(childID)) {
               String tileName = "netherBridgeGate";
               AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
            } else if (!"NeBCr".equals(childID) && !"NeStart".equals(childID)) {
               if ("NeSR".equals(childID)) {
                  String tileName = "netherTower";
                  AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
               } else if ("NeCE".equals(childID)) {
                  String tileName = "netherHall";
                  AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
               } else if ("NeCSR".equals(childID)) {
                  String tileName = "netherFortStairs";
                  AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
               } else if ("NeMT".equals(childID)) {
                  String tileName = "netherThrone";
                  AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
               } else {
                  String tileName = "netherWall";
                  if (noTileAt(world, chunkX, chunkZ)) {
                     AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
                  }
               }
            } else {
               String tileName = "netherBridge";
               AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
            }
         }
      }

   }

   private static boolean noTileAt(World world, int chunkX, int chunkZ) {
      return AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(world.provider.getDimension(), chunkX, chunkZ) == -1;
   }
}
