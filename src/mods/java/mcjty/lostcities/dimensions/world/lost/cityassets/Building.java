package mcjty.lostcities.dimensions.world.lost.cityassets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import mcjty.lostcities.api.ILostCityBuilding;
import org.apache.commons.lang3.tuple.Pair;

public class Building implements ILostCityBuilding {
   private String name;
   private int minFloors = -1;
   private int minCellars = -1;
   private int maxFloors = -1;
   private int maxCellars = -1;
   private char fillerBlock;
   private float prefersLonely = 0.0F;
   private final List<Pair<Predicate<ConditionContext>, String>> parts = new ArrayList();
   private final List<Pair<Predicate<ConditionContext>, String>> parts2 = new ArrayList();

   public Building(JsonObject object) {
      this.readFromJSon(object);
   }

   public Building(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void readFromJSon(JsonObject object) {
      this.name = object.get("name").getAsString();
      if (object.has("minfloors")) {
         this.minFloors = object.get("minfloors").getAsInt();
      }

      if (object.has("mincellars")) {
         this.minCellars = object.get("mincellars").getAsInt();
      }

      if (object.has("maxfloors")) {
         this.maxFloors = object.get("maxfloors").getAsInt();
      }

      if (object.has("maxcellars")) {
         this.maxCellars = object.get("maxcellars").getAsInt();
      }

      if (object.has("preferslonely")) {
         this.prefersLonely = object.get("preferslonely").getAsFloat();
      }

      if (object.has("filler")) {
         this.fillerBlock = object.get("filler").getAsCharacter();
         this.readParts(object, this.parts, "parts");
         this.readParts(object, this.parts2, "parts2");
      } else {
         throw new RuntimeException("'filler' is required for building '" + this.name + "'!");
      }
   }

   public void readParts(JsonObject object, List<Pair<Predicate<ConditionContext>, String>> p, String partSection) {
      p.clear();
      if (object.has(partSection)) {
         for(JsonElement element : object.get(partSection).getAsJsonArray()) {
            String partName = element.getAsJsonObject().get("part").getAsString();
            Predicate<ConditionContext> test = ConditionContext.parseTest(element);
            this.addPart(test, partName, p);
         }

      }
   }

   public JsonObject writeToJSon() {
      JsonObject object = new JsonObject();
      object.add("type", new JsonPrimitive("building"));
      object.add("name", new JsonPrimitive(this.name));
      JsonArray partArray = new JsonArray();

      for(Pair<Predicate<ConditionContext>, String> part : this.parts) {
         JsonObject partObject = new JsonObject();
         partObject.add("test", new JsonPrimitive("@todo"));
         partObject.add("part", new JsonPrimitive((String)part.getRight()));
         partArray.add(partObject);
      }

      object.add("parts", partArray);
      return object;
   }

   public Building addPart(Predicate<ConditionContext> test, String partName, List<Pair<Predicate<ConditionContext>, String>> parts) {
      parts.add(Pair.of(test, partName));
      return this;
   }

   public float getPrefersLonely() {
      return this.prefersLonely;
   }

   public int getMaxFloors() {
      return this.maxFloors;
   }

   public int getMaxCellars() {
      return this.maxCellars;
   }

   public int getMinFloors() {
      return this.minFloors;
   }

   public int getMinCellars() {
      return this.minCellars;
   }

   public char getFillerBlock() {
      return this.fillerBlock;
   }

   public String getRandomPart(Random random, ConditionContext info) {
      List<String> partNames = new ArrayList();

      for(Pair<Predicate<ConditionContext>, String> pair : this.parts) {
         if (((Predicate)pair.getLeft()).test(info)) {
            partNames.add(pair.getRight());
         }
      }

      if (partNames.isEmpty()) {
         return null;
      } else {
         return (String)partNames.get(random.nextInt(partNames.size()));
      }
   }

   public String getRandomPart2(Random random, ConditionContext info) {
      List<String> partNames = new ArrayList();

      for(Pair<Predicate<ConditionContext>, String> pair : this.parts2) {
         if (((Predicate)pair.getLeft()).test(info)) {
            partNames.add(pair.getRight());
         }
      }

      if (partNames.isEmpty()) {
         return null;
      } else {
         return (String)partNames.get(random.nextInt(partNames.size()));
      }
   }
}
