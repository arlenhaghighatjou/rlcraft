package hunternif.mc.atlas.ext.watcher.impl;

import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.SettingsConfig;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.event.OptionalMarkerEvent;
import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.ext.watcher.IStructureWatcher;
import hunternif.mc.atlas.ext.watcher.StructureWatcher;
import hunternif.mc.atlas.ext.watcher.WatcherPos;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.registry.MarkerTypes;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.MathUtil;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

public class StructureWatcherVillage implements IStructureWatcher {
   public static final String MARKER = "village";
   private static final String LIBRARY = "ViBH";
   private static final String SMITHY = "ViS";
   private static final String L_HOUSE = "ViTRH";
   private static final String FARMLAND_LARGE = "ViDF";
   private static final String FARMLAND_SMALL = "ViF";
   private static final String PATH = "ViSR";
   private static final String TORCH = "ViL";
   private static final String WELL = "ViW";
   private static final String START = "ViStart";
   private static final String BUTCHERS = "ViPH";
   private static final String HUT = "ViSmH";
   private static final String HOUSE_SMALL = "ViSH";
   private static final String CHURCH = "ViST";
   private static final Map<String, String> partToTileMap;
   private static final Map<String, Integer> tilePriority;
   private final Set<WatcherPos> visited = new HashSet();

   public StructureWatcherVillage() {
      StructureWatcher.INSTANCE.addWatcher(this);
   }

   @Nonnull
   public Set<WatcherPos> getVisited() {
      return this.visited;
   }

   public boolean isDimensionValid(DimensionType type) {
      return type.getId() == 0;
   }

   @SubscribeEvent
   public void onOptionalMarker(OptionalMarkerEvent event) {
      if (event.type == MarkerTypes.VILLAGE && !SettingsConfig.gameplay.autoVillageMarkers) {
         event.setCanceled(true);
      }

   }

   @Nullable
   public NBTTagCompound getStructureData(@Nonnull World world) {
      MapGenStructureData data = (MapGenStructureData)world.getPerWorldStorage().getOrLoadData(MapGenStructureData.class, "Village");
      return data == null ? null : data.getTagCompound();
   }

   @Nonnull
   public Set<Pair<WatcherPos, String>> visitStructure(@Nonnull World world, @Nonnull NBTTagCompound structureTag) {
      Set<String> tagSet = structureTag.getKeySet();
      Set<Pair<WatcherPos, String>> visits = Sets.newHashSet();

      for(String coords : tagSet) {
         if (WatcherPos.POS_PATTERN.matcher(coords).matches()) {
            WatcherPos pos = new WatcherPos(coords);
            if (!this.visited.contains(pos)) {
               NBTTagCompound tag = structureTag.getCompoundTag(coords);
               this.visitVillage(world, tag);
               this.visited.add(pos);
               visits.add(Pair.of(pos, "Village"));
            }
         }
      }

      return visits;
   }

   private void visitVillage(World world, NBTTagCompound tag) {
      if (!tag.getBoolean("Valid")) {
         removeVillage(world, tag);
      } else {
         NBTTagList children = tag.getTagList("Children", 10);

         for(int i = 0; i < children.tagCount(); ++i) {
            NBTTagCompound child = children.getCompoundTagAt(i);
            String childID = child.getString("id");
            StructureBoundingBox boundingBox = new StructureBoundingBox(child.getIntArray("BB"));
            int x = MathUtil.getCenter(boundingBox).getX();
            int z = MathUtil.getCenter(boundingBox).getZ();
            int chunkX = x >> 4;
            int chunkZ = z >> 4;
            if ("ViStart".equals(childID)) {
               boolean foundMarker = false;

               for(int j = -1; j <= 1; ++j) {
                  for(int k = -1; k <= 1; ++k) {
                     List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData().getMarkersAtChunk(world.provider.getDimension(), j + chunkX / 8, k + chunkZ / 8);
                     if (markers != null) {
                        for(Marker marker : markers) {
                           if (marker.isOfType(MarkerTypes.VILLAGE)) {
                              foundMarker = true;
                              break;
                           }
                        }
                     }
                  }
               }

               if (!foundMarker && SettingsConfig.gameplay.autoVillageMarkers) {
                  AtlasAPI.markers.putGlobalMarker(world, false, MarkerTypes.VILLAGE, "gui.antiqueatlas.marker.village", x, z);
               }
            }

            String tileName = (String)partToTileMap.get(childID);
            if (tileName != null) {
               Integer curTilePriority = (Integer)tilePriority.get(tileName);
               Integer prevTilePriority = (Integer)tilePriority.get(tileAt(chunkX, chunkZ));
               if (curTilePriority != null && prevTilePriority != null) {
                  if (curTilePriority.intValue() >= prevTilePriority.intValue()) {
                     AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
                  }
               } else {
                  AtlasAPI.tiles.putCustomGlobalTile(world, tileName, chunkX, chunkZ);
               }
            }
         }

      }
   }

   private static String tileAt(int chunkX, int chunkZ) {
      int biomeID = AntiqueAtlasMod.extBiomeData.getData().getBiomeIdAt(0, chunkX, chunkZ);
      return ExtTileIdMap.instance().getPseudoBiomeName(biomeID);
   }

   private static void removeVillage(World world, NBTTagCompound tag) {
      NBTTagList children = tag.getTagList("Children", 10);

      for(int i = 0; i < children.tagCount(); ++i) {
         NBTTagCompound child = children.getCompoundTagAt(i);
         String childID = child.getString("id");
         StructureBoundingBox boundingBox = new StructureBoundingBox(child.getIntArray("BB"));
         int x = MathUtil.getCenter(boundingBox).getX();
         int z = MathUtil.getCenter(boundingBox).getZ();
         int chunkX = x >> 4;
         int chunkZ = z >> 4;
         if ("ViStart".equals(childID)) {
            List<Marker> markers = AntiqueAtlasMod.globalMarkersData.getData().getMarkersAtChunk(world.provider.getDimension(), chunkX / 8, chunkZ / 8);
            if (markers != null) {
               for(Marker marker : markers) {
                  if (marker.isOfType(MarkerTypes.VILLAGE)) {
                     AtlasAPI.markers.deleteGlobalMarker(world, marker.getId());
                     Log.info("Removed faux village marker");
                     break;
                  }
               }
            }
         }

         AtlasAPI.tiles.deleteCustomGlobalTile(world, chunkX, chunkZ);
         Log.info("Removed faux village tile");
      }

   }

   static {
      Builder<String, String> builder = new Builder();
      builder.put("ViBH", "npcVillageLibrary");
      builder.put("ViS", "npcVillageSmithy");
      builder.put("ViTRH", "npcVillageLHouse");
      builder.put("ViDF", "npcVillageFarmlandLarge");
      builder.put("ViF", "npcVillageFarmlandSmall");
      builder.put("ViL", "npcVillageTorch");
      builder.put("ViW", "npcVillageWell");
      builder.put("ViStart", "npcVillageWell");
      builder.put("ViPH", "npcVillageButchersShop");
      builder.put("ViSH", "npcVillageSmallHouse");
      builder.put("ViSmH", "npcVillageHut");
      builder.put("ViST", "npcVillageChurch");
      partToTileMap = builder.build();
      builder = new Builder();
      builder.put("npcVillageLibrary", Integer.valueOf(5));
      builder.put("npcVillageSmithy", Integer.valueOf(6));
      builder.put("npcVillageLHouse", Integer.valueOf(5));
      builder.put("npcVillageFarmlandLarge", Integer.valueOf(3));
      builder.put("npcVillageFarmlandSmall", Integer.valueOf(3));
      builder.put("npcVillageTorch", Integer.valueOf(1));
      builder.put("npcVillageWell", Integer.valueOf(7));
      builder.put("npcVillageButchersShop", Integer.valueOf(4));
      builder.put("npcVillageSmallHouse", Integer.valueOf(4));
      builder.put("npcVillageHut", Integer.valueOf(3));
      builder.put("npcVillageChurch", Integer.valueOf(6));
      tilePriority = builder.build();
   }
}
