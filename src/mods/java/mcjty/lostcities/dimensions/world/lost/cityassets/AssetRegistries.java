package mcjty.lostcities.dimensions.world.lost.cityassets;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import mcjty.lostcities.LostCities;
import mcjty.lostcities.varia.Counter;

public class AssetRegistries {
   public static final AbstractAssetRegistry<Condition> CONDITIONS = new AbstractAssetRegistry<Condition>();
   public static final AbstractAssetRegistry<WorldStyle> WORLDSTYLES = new AbstractAssetRegistry<WorldStyle>();
   public static final AbstractAssetRegistry<CityStyle> CITYSTYLES = new AbstractAssetRegistry<CityStyle>();
   public static final AbstractAssetRegistry<BuildingPart> PARTS = new AbstractAssetRegistry<BuildingPart>();
   public static final AbstractAssetRegistry<Building> BUILDINGS = new AbstractAssetRegistry<Building>();
   public static final AbstractAssetRegistry<MultiBuilding> MULTI_BUILDINGS = new AbstractAssetRegistry<MultiBuilding>();
   public static final AbstractAssetRegistry<Style> STYLES = new AbstractAssetRegistry<Style>();
   public static final AbstractAssetRegistry<Palette> PALETTES = new AbstractAssetRegistry<Palette>();
   public static final AbstractAssetRegistry<PredefinedCity> PREDEFINED_CITIES = new AbstractAssetRegistry<PredefinedCity>();
   public static final AbstractAssetRegistry<PredefinedSphere> PREDEFINED_SPHERES = new AbstractAssetRegistry<PredefinedSphere>();

   public AssetRegistries() {
   }

   public static final void reset() {
      CONDITIONS.reset();
      WORLDSTYLES.reset();
      PARTS.reset();
      BUILDINGS.reset();
      CITYSTYLES.reset();
      MULTI_BUILDINGS.reset();
      STYLES.reset();
      PALETTES.reset();
      PREDEFINED_CITIES.reset();
      PREDEFINED_SPHERES.reset();
   }

   public static void load(VFile2 file) {
      byte[] data = file.getAllBytes();
      if (data != null) {
         load(new EaglerInputStream(data), file.getName());
      }
   }

   private static void add(Map<Character, Set<String>> map, Character character, String partName) {
      if (!map.containsKey(character)) {
         map.put(character, new HashSet());
      }

      ((Set)map.get(character)).add(partName);
   }

   public static void showStatistics() {
      Counter<Character> counterLocal = new Counter<Character>();
      Counter<Character> counterGlobal = new Counter<Character>();
      Map<Character, Set<String>> usersPerCharacter = new HashMap();

      for(BuildingPart part : PARTS.getIterable()) {
         Palette localPalette = part.getLocalPalette();
         Map<Character, Object> palette = Collections.emptyMap();
         if (localPalette != null) {
            palette = localPalette.palette;
         }

         for(int x = 0; x < part.getXSize(); ++x) {
            for(int z = 0; z < part.getZSize(); ++z) {
               char[] slice = part.getVSlice(x, z);
               if (slice != null) {
                  for(char c : slice) {
                     if (palette.containsKey(Character.valueOf(c))) {
                        counterLocal.add(Character.valueOf(c));
                     } else {
                        counterGlobal.add(Character.valueOf(c));
                     }

                     add(usersPerCharacter, Character.valueOf(c), part.getName());
                  }
               }
            }
         }
      }

      List<Entry<Character, Integer>> global = new ArrayList(counterGlobal.getMap().entrySet());
      List<Entry<Character, Integer>> local = new ArrayList(counterLocal.getMap().entrySet());
      global.sort((e1, e2) -> {
         return ((Integer)e2.getValue()).compareTo((Integer)e1.getValue());
      });
      local.sort((e1, e2) -> {
         return ((Integer)e2.getValue()).compareTo((Integer)e1.getValue());
      });
      LostCities.setup.getLogger().info("############################################################################");
      LostCities.setup.getLogger().info("Global palette entries");
      printMap(usersPerCharacter, global);
      LostCities.setup.getLogger().info("----------------------------------------------------------------------------");
      LostCities.setup.getLogger().info("Local palette entries");
      printMap(usersPerCharacter, local);
      LostCities.setup.getLogger().info("----------------------------------------------------------------------------");
      printChars("Global: ", counterGlobal);
      printChars("Local: ", counterLocal);
      LostCities.setup.getLogger().info("############################################################################");
   }

   private static void printChars(String prefix, Counter<Character> counter) {
      List<Character> chars = new ArrayList(counter.getMap().keySet());
      chars.sort((o1, o2) -> {
         return o1.compareTo(o2);
      });
      String s = "";

      for(Character character : chars) {
         s = s + character;
      }

      LostCities.setup.getLogger().info(prefix + s);
   }

   private static void printMap(Map<Character, Set<String>> usersPerCharacter, List<Entry<Character, Integer>> map) {
      for(Entry<Character, Integer> entry : map) {
         Set<String> users = (Set)usersPerCharacter.get(entry.getKey());
         String s = "    " + entry.getKey() + ": " + entry.getValue() + " Uses";
         if (users.size() < 10) {
            s = s + ", Used by: ";

            for(String user : users) {
               s = s + user + ",";
            }
         } else {
            s = s + ", Used " + users.size() + " times";
         }

         LostCities.setup.getLogger().info(s);
      }

   }

   public static void load(InputStream inputstream, String filename) {
      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
         Throwable var3 = null;

         try {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(br);

            for(JsonElement entry : element.getAsJsonArray()) {
               JsonObject object = entry.getAsJsonObject();
               String type = object.get("type").getAsString();
               if ("style".equals(type)) {
                  STYLES.register(new Style(object));
               } else if ("condition".equals(type)) {
                  CONDITIONS.register(new Condition(object));
               } else if ("palette".equals(type)) {
                  PALETTES.register(new Palette(object));
               } else if ("citystyle".equals(type)) {
                  CITYSTYLES.register(new CityStyle(object));
               } else if ("part".equals(type)) {
                  PARTS.register(new BuildingPart(object));
               } else if ("building".equals(type)) {
                  BUILDINGS.register(new Building(object));
               } else if ("multibuilding".equals(type)) {
                  MULTI_BUILDINGS.register(new MultiBuilding(object));
               } else if ("worldstyle".equals(type)) {
                  WORLDSTYLES.register(new WorldStyle(object));
               } else if ("city".equals(type)) {
                  PREDEFINED_CITIES.register(new PredefinedCity(object));
               } else {
                  if (!"sphere".equals(type)) {
                     throw new RuntimeException("Unknown type '" + type + " in " + filename + "'!");
                  }

                  PREDEFINED_SPHERES.register(new PredefinedSphere(object));
               }
            }
         } catch (Throwable var18) {
            var3 = var18;
            throw var18;
         } finally {
            if (br != null) {
               if (var3 != null) {
                  try {
                     br.close();
                  } catch (Throwable var17) {
                     var3.addSuppressed(var17);
                  }
               } else {
                  br.close();
               }
            }

         }

      } catch (IOException var20) {
         throw new UncheckedIOException(var20);
      }
   }
}
