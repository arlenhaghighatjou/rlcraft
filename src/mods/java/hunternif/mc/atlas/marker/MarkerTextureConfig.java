package hunternif.mc.atlas.marker;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.atlas.registry.MarkerRegistry;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.Log;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

@SideOnly(Side.CLIENT)
public class MarkerTextureConfig extends AbstractJSONConfig<MarkerRegistry> {
   private static final int VERSION = 1;
   private static final JsonParser parser = new JsonParser();
   private static final String EXAMPLE_LOC = "antiqueatlas:Example";
   private final JsonElement EXAMPLE_JSON;

   public MarkerTextureConfig(File file) {
      super(file);
      InputStream is = this.getClass().getResourceAsStream("/markerExample.json");
      InputStreamReader reader = new InputStreamReader(is);
      this.EXAMPLE_JSON = parser.parse(reader);
      IOUtils.closeQuietly(is);
   }

   public int currentVersion() {
      return 1;
   }

   protected void loadData(JsonObject json, MarkerRegistry data, int version) {
      for(Entry<String, JsonElement> entry : json.entrySet()) {
         String markerType = (String)entry.getKey();
         if (!markerType.equals("antiqueatlas:Example")) {
            if (!((JsonElement)entry.getValue()).isJsonObject()) {
               Log.warn("Loading marker %s from JSON: Entry isn't a JSON object!", markerType);
            }

            JsonObject object = (JsonObject)entry.getValue();
            ResourceLocation key = MarkerRegistry.getLoc(markerType);
            if (MarkerRegistry.hasKey(key)) {
               MarkerRegistry.find(key).getJSONData().readFrom(object);
            } else {
               MarkerType type = new MarkerType(key, new ResourceLocation[0]);
               type.getJSONData().readFrom(object);
               type.setIsFromJson(true);
               MarkerRegistry.register(type);
            }
         }
      }

   }

   protected void saveData(JsonObject json, MarkerRegistry data) {
      Queue<ResourceLocation> queue = new PriorityQueue(Comparator.comparing(ResourceLocation::toString));
      queue.addAll(MarkerRegistry.getKeys());
      json.add("antiqueatlas:Example", this.EXAMPLE_JSON);

      while(!queue.isEmpty()) {
         ResourceLocation key = (ResourceLocation)queue.poll();
         JsonObject value = new JsonObject();
         MarkerRegistry.find(key).getJSONData().saveTo(value);
         json.add(key.toString(), value);
      }

      queue = null;
   }
}
