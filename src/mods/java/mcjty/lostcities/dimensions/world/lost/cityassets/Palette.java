package mcjty.lostcities.dimensions.world.lost.cityassets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mcjty.lostcities.api.ILostCityAsset;
import mcjty.lostcities.varia.Tools;
import net.minecraft.block.state.IBlockState;
import org.apache.commons.lang3.tuple.Pair;

public class Palette implements ILostCityAsset {
   private String name;
   final Map<Character, Object> palette = new HashMap();
   private final Map<IBlockState, IBlockState> damaged = new HashMap();
   private final Map<Character, String> mobIds = new HashMap();
   private final Map<Character, String> lootTables = new HashMap();
   private final Map<Character, Map<String, Integer>> torchOrientations = new HashMap();

   public Palette() {
   }

   public Palette(JsonObject object) {
      this.readFromJSon(object);
   }

   public Palette(String name) {
      this.name = name;
   }

   public void merge(Palette other) {
      this.palette.putAll(other.palette);
      this.damaged.putAll(other.damaged);
      this.mobIds.putAll(other.mobIds);
      this.lootTables.putAll(other.lootTables);
      this.torchOrientations.putAll(other.torchOrientations);
   }

   public String getName() {
      return this.name;
   }

   public Map<IBlockState, IBlockState> getDamaged() {
      return this.damaged;
   }

   public Map<Character, String> getMobIds() {
      return this.mobIds;
   }

   public Map<Character, String> getLootTables() {
      return this.lootTables;
   }

   public Map<Character, Map<String, Integer>> getTorchOrientations() {
      return this.torchOrientations;
   }

   public Map<Character, Object> getPalette() {
      return this.palette;
   }

   public void readFromJSon(JsonObject object) {
      this.name = object.get("name").getAsString();
      JsonArray paletteArray = object.get("palette").getAsJsonArray();
      this.parsePaletteArray(paletteArray);
   }

   public void parsePaletteArray(JsonArray paletteArray) {
      for(JsonElement element : paletteArray) {
         JsonObject o = element.getAsJsonObject();
         Object value = null;
         Character c = o.get("char").getAsCharacter();
         IBlockState dmg = null;
         if (o.has("damaged")) {
            dmg = Tools.stringToState(o.get("damaged").getAsString());
         }

         if (o.has("mob")) {
            this.mobIds.put(c, o.get("mob").getAsString());
         }

         if (o.has("loot")) {
            this.lootTables.put(c, o.get("loot").getAsString());
         }

         if (o.has("facing")) {
            Map<String, Integer> or = new HashMap();
            JsonObject torchObj = o.get("facing").getAsJsonObject();
            this.getOrientation(or, torchObj, "north");
            this.getOrientation(or, torchObj, "south");
            this.getOrientation(or, torchObj, "west");
            this.getOrientation(or, torchObj, "east");
            this.getOrientation(or, torchObj, "up");
            this.torchOrientations.put(c, or);
         }

         if (o.has("block")) {
            String block = o.get("block").getAsString();
            IBlockState state = Tools.stringToState(block);
            this.palette.put(c, state);
            if (dmg != null) {
               this.damaged.put(state, dmg);
            }
         } else if (o.has("frompalette")) {
            value = o.get("frompalette").getAsString();
            this.palette.put(c, value);
         } else {
            if (!o.has("blocks")) {
               throw new RuntimeException("Illegal palette!");
            }

            JsonArray array = o.get("blocks").getAsJsonArray();
            List<Pair<Integer, IBlockState>> blocks = new ArrayList();

            for(JsonElement el : array) {
               JsonObject ob = el.getAsJsonObject();
               Integer f = ob.get("random").getAsInt();
               String block = ob.get("block").getAsString();
               IBlockState state = Tools.stringToState(block);
               blocks.add(Pair.of(f, state));
               if (dmg != null) {
                  this.damaged.put(state, dmg);
               }
            }

            this.addMappingViaState(c.charValue(), (Pair[])blocks.toArray(new Pair[blocks.size()]));
         }
      }

   }

   private void getOrientation(Map<String, Integer> or, JsonObject torchObj, String orientation) {
      if (torchObj.has(orientation)) {
         or.put(orientation, Integer.valueOf(torchObj.get(orientation).getAsInt()));
      } else {
         or.put(orientation, Integer.valueOf(0));
      }

   }

   public JsonObject writeToJSon() {
      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive("palette"));
      object.add("name", new JsonPrimitive(this.name));
      JsonArray array = new JsonArray();

      for(Entry<Character, Object> entry : this.palette.entrySet()) {
         JsonObject o = new JsonObject();
         o.add("char", new JsonPrimitive((Character)entry.getKey()));
         if (entry.getValue() instanceof IBlockState) {
            IBlockState state = (IBlockState)entry.getValue();
            o.add("block", new JsonPrimitive(Tools.stateToString(state)));
            if (this.damaged.containsKey(state)) {
               o.add("damaged", new JsonPrimitive(Tools.stateToString((IBlockState)this.damaged.get(state))));
            }
         } else if (entry.getValue() instanceof String) {
            o.add("frompalette", new JsonPrimitive((String)entry.getValue()));
         } else {
            o.add("test", new JsonPrimitive("@todo"));
         }

         if (this.mobIds.containsKey(entry.getKey())) {
            o.add("mob", new JsonPrimitive((String)this.mobIds.get(entry.getKey())));
         }

         if (this.lootTables.containsKey(entry.getKey())) {
            o.add("loot", new JsonPrimitive((String)this.lootTables.get(entry.getKey())));
         }

         array.add(o);
      }

      object.add("palette", array);
      return object;
   }

   public Palette addMapping(char c, IBlockState state) {
      this.palette.put(Character.valueOf(c), state);
      return this;
   }

   @SafeVarargs
   private final Palette addMappingViaState(char c, Pair<Integer, IBlockState>... randomBlocks) {
      this.palette.put(Character.valueOf(c), randomBlocks);
      return this;
   }
}
