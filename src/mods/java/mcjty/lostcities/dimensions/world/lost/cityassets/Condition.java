package mcjty.lostcities.dimensions.world.lost.cityassets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Predicate;
import mcjty.lostcities.api.ILostCityAsset;
import mcjty.lostcities.varia.Tools;
import org.apache.commons.lang3.tuple.Pair;

public class Condition implements ILostCityAsset {
   private String name;
   private final List<Pair<Predicate<ConditionContext>, Pair<Float, String>>> valueSelector = new ArrayList();

   public Condition(JsonObject object) {
      this.readFromJSon(object);
   }

   public Condition(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void readFromJSon(JsonObject object) {
      this.name = object.get("name").getAsString();

      for(JsonElement element : object.get("values").getAsJsonArray()) {
         JsonObject o = element.getAsJsonObject();
         float factor = o.get("factor").getAsFloat();
         String value = o.get("value").getAsString();
         Predicate<ConditionContext> test = ConditionContext.parseTest(element);
         this.valueSelector.add(Pair.of(test, Pair.of(Float.valueOf(factor), value)));
      }

   }

   public JsonObject writeToJSon() {
      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive("condition"));
      object.add("name", new JsonPrimitive(this.name));
      return object;
   }

   public String getRandomValue(EaglercraftRandom random, ConditionContext info) {
      List<Pair<Float, String>> values = new ArrayList();

      for(Pair<Predicate<ConditionContext>, Pair<Float, String>> pair : this.valueSelector) {
         if (((Predicate)pair.getLeft()).test(info)) {
            values.add(pair.getRight());
         }
      }

      if (values.isEmpty()) {
         return null;
      } else {
         return Tools.getRandomFromList(random, values);
      }
   }
}
