package mcjty.lostcities.dimensions.world.lost.cityassets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mcjty.lostcities.api.ILostCityAsset;
import mcjty.lostcities.dimensions.world.LostCityChunkGenerator;
import org.apache.commons.lang3.tuple.Pair;

public class Style implements ILostCityAsset {
   private String name;
   private final List<List<Pair<Float, String>>> randomPaletteChoices = new ArrayList();

   public Style(String name) {
      this.name = name;
   }

   public Style(JsonObject object) {
      this.readFromJSon(object);
   }

   public void readFromJSon(JsonObject object) {
      this.name = object.get("name").getAsString();

      for(JsonElement element : object.get("randompalettes").getAsJsonArray()) {
         List<Pair<Float, String>> palettes = new ArrayList();

         for(JsonElement el : element.getAsJsonArray()) {
            float factor = el.getAsJsonObject().get("factor").getAsFloat();
            String style = el.getAsJsonObject().get("palette").getAsString();
            palettes.add(Pair.of(Float.valueOf(factor), style));
         }

         this.randomPaletteChoices.add(palettes);
      }

   }

   public JsonObject writeToJSon() {
      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive("style"));
      object.add("name", new JsonPrimitive(this.name));
      JsonArray array = new JsonArray();

      for(List<Pair<Float, String>> list : this.randomPaletteChoices) {
         JsonArray a = new JsonArray();

         for(Pair<Float, String> pair : list) {
            JsonObject o = new JsonObject();
            o.add("factor", new JsonPrimitive((Number)pair.getKey()));
            o.add("palette", new JsonPrimitive((String)pair.getValue()));
            a.add(o);
         }

         array.add(a);
      }

      object.add("randompalettes", array);
      return object;
   }

   public String getName() {
      return this.name;
   }

   public Palette getRandomPalette(LostCityChunkGenerator provider, Random random) {
      Palette palette = new Palette();

      for(List<Pair<Float, String>> pairs : this.randomPaletteChoices) {
         float totalweight = 0.0F;

         for(Pair<Float, String> pair : pairs) {
            totalweight += ((Float)pair.getKey()).floatValue();
         }

         float r = random.nextFloat() * totalweight;
         Palette tomerge = null;

         for(Pair<Float, String> pair : pairs) {
            r -= ((Float)pair.getKey()).floatValue();
            if (r <= 0.0F) {
               tomerge = AssetRegistries.PALETTES.get((String)pair.getRight());
               if (tomerge == null) {
                  throw new RuntimeException("Palette '" + (String)pair.getRight() + "' is missing!");
               }
               break;
            }
         }

         palette.merge(tomerge);
      }

      return palette;
   }
}
