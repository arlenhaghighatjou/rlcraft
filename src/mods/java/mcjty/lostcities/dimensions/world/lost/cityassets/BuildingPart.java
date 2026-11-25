package mcjty.lostcities.dimensions.world.lost.cityassets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import mcjty.lostcities.LostCities;
import mcjty.lostcities.api.ILostCityAsset;
import mcjty.lostcities.dimensions.world.lost.BuildingInfo;
import org.apache.commons.lang3.StringUtils;

public class BuildingPart implements IBuildingPart, ILostCityAsset {
   private String name;
   private String[] slices;
   private int xSize;
   private int zSize;
   private char[][] vslices = (char[][])null;
   private Palette localPalette = null;
   String refPaletteName;
   private Map<String, Object> metadata = new HashMap();

   public BuildingPart(JsonObject object) {
      this.readFromJSon(object);
   }

   public BuildingPart(String name, int xSize, int zSize, String[] slices) {
      this.name = name;
      this.slices = slices;
      this.xSize = xSize;
      this.zSize = zSize;
   }

   public Character getMetaChar(String key) {
      return (Character)this.metadata.get(key);
   }

   public Integer getMetaInteger(String key) {
      return (Integer)this.metadata.get(key);
   }

   public boolean getMetaBoolean(String key) {
      Object o = this.metadata.get(key);
      return o instanceof Boolean ? ((Boolean)o).booleanValue() : false;
   }

   public Float getMetaFloat(String key) {
      return (Float)this.metadata.get(key);
   }

   public String getMetaString(String key) {
      return (String)this.metadata.get(key);
   }

   public String getName() {
      return this.name;
   }

   public char[][] getVslices() {
      if (this.vslices == null) {
         this.vslices = new char[this.xSize * this.zSize][];

         for(int x = 0; x < this.xSize; ++x) {
            for(int z = 0; z < this.zSize; ++z) {
               String vs = "";
               boolean empty = true;

               for(int y = 0; y < this.slices.length; ++y) {
                  Character c = this.getC(x, y, z);
                  vs = vs + c;
                  if (c.charValue() != ' ') {
                     empty = false;
                  }
               }

               if (empty) {
                  this.vslices[z * this.xSize + x] = null;
               } else {
                  this.vslices[z * this.xSize + x] = vs.toCharArray();
               }
            }
         }
      }

      return this.vslices;
   }

   public char[] getVSlice(int x, int z) {
      return this.getVslices()[z * this.xSize + x];
   }

   public Palette getLocalPalette() {
      if (this.localPalette == null && this.refPaletteName != null) {
         this.localPalette = AssetRegistries.PALETTES.get(this.refPaletteName);
         if (this.localPalette == null) {
            LostCities.setup.getLogger().error("Could not find palette '" + this.refPaletteName + "'!");
            throw new RuntimeException("Could not find palette '" + this.refPaletteName + "'!");
         }
      }

      return this.localPalette;
   }

   public void readFromJSon(JsonObject object) {
      this.name = object.get("name").getAsString();
      this.xSize = object.get("xsize").getAsInt();
      this.zSize = object.get("zsize").getAsInt();
      JsonArray sliceArray = object.get("slices").getAsJsonArray();
      this.slices = new String[sliceArray.size()];
      int i = 0;

      for(JsonElement element : sliceArray) {
         JsonArray a = element.getAsJsonArray();
         String slice = "";

         for(JsonElement el : a) {
            slice = slice + el.getAsString();
         }

         this.slices[i++] = slice;
      }

      if (object.has("palette")) {
         if (object.get("palette").isJsonArray()) {
            JsonArray palette = object.get("palette").getAsJsonArray();
            this.localPalette = new Palette();
            this.localPalette.parsePaletteArray(palette);
         } else {
            this.refPaletteName = object.get("palette").getAsString();
         }
      }

      if (object.has("meta")) {
         for(JsonElement element : object.get("meta").getAsJsonArray()) {
            JsonObject o = element.getAsJsonObject();
            String key = o.get("key").getAsString();
            if (o.has("integer")) {
               this.metadata.put(key, Integer.valueOf(o.get("integer").getAsInt()));
            } else if (o.has("float")) {
               this.metadata.put(key, Float.valueOf(o.get("float").getAsFloat()));
            } else if (o.has("boolean")) {
               this.metadata.put(key, Boolean.valueOf(o.get("boolean").getAsBoolean()));
            } else if (o.has("char")) {
               this.metadata.put(key, Character.valueOf(o.get("char").getAsCharacter()));
            } else if (o.has("character")) {
               this.metadata.put(key, Character.valueOf(o.get("character").getAsCharacter()));
            } else if (o.has("string")) {
               this.metadata.put(key, o.get("string").getAsString());
            }
         }
      }

   }

   public JsonObject writeToJSon() {
      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive("part"));
      object.add("name", new JsonPrimitive(this.name));
      object.add("xsize", new JsonPrimitive(this.xSize));
      object.add("zsize", new JsonPrimitive(this.zSize));
      JsonArray sliceArray = new JsonArray();

      for(String slice : this.slices) {
         JsonArray a;
         String left;
         for(a = new JsonArray(); !slice.isEmpty(); slice = slice.substring(left.length())) {
            left = StringUtils.left(slice, this.xSize);
            a.add(new JsonPrimitive(left));
         }

         sliceArray.add(a);
      }

      object.add("slices", sliceArray);
      JsonArray metaArray = new JsonArray();

      for(Entry<String, Object> entry : this.metadata.entrySet()) {
         JsonObject o = new JsonObject();
         o.add("key", new JsonPrimitive((String)entry.getKey()));
         Object v = entry.getValue();
         if (v instanceof Integer) {
            o.add("integer", new JsonPrimitive((Integer)v));
         } else if (v instanceof Float) {
            o.add("float", new JsonPrimitive((Float)v));
         } else if (v instanceof Boolean) {
            o.add("boolean", new JsonPrimitive((Boolean)v));
         } else if (v instanceof String) {
            o.add("string", new JsonPrimitive((String)v));
         }

         metaArray.add(o);
      }

      object.add("meta", metaArray);
      return object;
   }

   public int getSliceCount() {
      return this.slices.length;
   }

   public String getSlice(int i) {
      return this.slices[i];
   }

   public String[] getSlices() {
      return this.slices;
   }

   public int getXSize() {
      return this.xSize;
   }

   public int getZSize() {
      return this.zSize;
   }

   public Character getPaletteChar(int x, int y, int z) {
      return this.slices[y].charAt(z * this.xSize + x);
   }

   public Character get(BuildingInfo info, int x, int y, int z) {
      return info.getCompiledPalette().get(this.slices[y].charAt(z * this.xSize + x));
   }

   public Character getC(int x, int y, int z) {
      return this.slices[y].charAt(z * this.xSize + x);
   }
}
