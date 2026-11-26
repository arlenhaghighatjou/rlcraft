package mcjty.lostcities.dimensions.world.lost.cityassets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;

public abstract class ConditionContext {
   private final int level;
   private final int floor;
   private final int floorsBelowGround;
   private final int floorsAboveGround;
   private final String part;
   private final String building;
   private final int chunkX;
   private final int chunkZ;

   public ConditionContext(int level, int floor, int floorsBelowGround, int floorsAboveGround, String part, String building, int chunkX, int chunkZ) {
      this.level = level;
      this.floor = floor;
      this.floorsBelowGround = floorsBelowGround;
      this.floorsAboveGround = floorsAboveGround;
      this.part = part;
      this.building = building;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
   }

   private static Predicate<ConditionContext> combine(Predicate<ConditionContext> orig, Predicate<ConditionContext> newTest) {
      return orig == null ? newTest : (levelInfo) -> {
         return orig.test(levelInfo) && newTest.test(levelInfo);
      };
   }

   public static Predicate<ConditionContext> parseTest(JsonElement element) {
      Predicate<ConditionContext> test = null;
      JsonObject obj = element.getAsJsonObject();
      if (obj.has("top")) {
         boolean top = obj.get("top").getAsBoolean();
         if (top) {
            test = combine(test, ConditionContext::isTopOfBuilding);
         } else {
            test = combine(test, (levelInfo) -> {
               return !levelInfo.isTopOfBuilding();
            });
         }
      }

      if (obj.has("ground")) {
         boolean ground = obj.get("ground").getAsBoolean();
         if (ground) {
            test = combine(test, ConditionContext::isGroundFloor);
         } else {
            test = combine(test, (levelInfo) -> {
               return !levelInfo.isGroundFloor();
            });
         }
      }

      if (obj.has("isbuilding")) {
         boolean ground = obj.get("isbuilding").getAsBoolean();
         if (ground) {
            test = combine(test, ConditionContext::isBuilding);
         } else {
            test = combine(test, (levelInfo) -> {
               return !levelInfo.isBuilding();
            });
         }
      }

      if (obj.has("issphere")) {
         boolean ground = obj.get("issphere").getAsBoolean();
         if (ground) {
            test = combine(test, ConditionContext::isSphere);
         } else {
            test = combine(test, (levelInfo) -> {
               return !levelInfo.isSphere();
            });
         }
      }

      if (obj.has("chunkx")) {
         int chunkX = obj.get("chunkx").getAsInt();
         test = combine(test, (context) -> {
            return chunkX == context.getChunkX();
         });
      }

      if (obj.has("chunkz")) {
         int chunkZ = obj.get("chunkz").getAsInt();
         test = combine(test, (context) -> {
            return chunkZ == context.getChunkZ();
         });
      }

      if (obj.has("inpart")) {
         String part = obj.get("inpart").getAsString();
         test = combine(test, (context) -> {
            return part.equals(context.getPart());
         });
      }

      if (obj.has("inbuilding")) {
         String building = obj.get("inbuilding").getAsString();
         test = combine(test, (context) -> {
            return building.equals(context.getBuilding());
         });
      }

      if (obj.has("inbiome")) {
         String biome = obj.get("inbiome").getAsString();
         test = combine(test, (context) -> {
            return biome.equals(context.getBiome());
         });
      }

      if (obj.has("cellar")) {
         boolean cellar = obj.get("cellar").getAsBoolean();
         if (cellar) {
            test = combine(test, ConditionContext::isCellar);
         } else {
            test = combine(test, (levelInfo) -> {
               return !levelInfo.isCellar();
            });
         }
      }

      if (obj.has("floor")) {
         int level = obj.get("floor").getAsInt();
         test = combine(test, (levelInfo) -> {
            return levelInfo.isFloor(level);
         });
      }

      if (obj.has("range")) {
         String range = obj.get("range").getAsString();
         String[] split = StringUtils.split(range, ',');

         try {
            int l1 = Integer.parseInt(split[0]);
            int l2 = Integer.parseInt(split[1]);
            test = combine(test, (levelInfo) -> {
               return levelInfo.isRange(l1, l2);
            });
         } catch (NumberFormatException var7) {
            throw new RuntimeException("Bad range specification: <l1>,<l2>!");
         } catch (ArrayIndexOutOfBoundsException var8) {
            throw new RuntimeException("Bad range specification: <l1>,<l2>!");
         }
      }

      if (test == null) {
         test = (x) -> true;
      }

      return test;
   }

   public int getLevel() {
      return this.level;
   }

   public int getFloor() {
      return this.floor;
   }

   public int getFloorsBelowGround() {
      return this.floorsBelowGround;
   }

   public int getFloorsAboveGround() {
      return this.floorsAboveGround;
   }

   public boolean isGroundFloor() {
      return this.floor == 0;
   }

   public boolean isBuilding() {
      return !"<none>".equals(this.building);
   }

   public abstract boolean isSphere();

   public abstract String getBiome();

   public boolean isTopOfBuilding() {
      return this.floor >= this.floorsAboveGround;
   }

   public boolean isCellar() {
      return this.floor < 0;
   }

   public boolean isFloor(int l) {
      return this.floor == l;
   }

   public boolean isRange(int l1, int l2) {
      return this.floor >= l1 && this.floor <= l2;
   }

   public String getPart() {
      return this.part;
   }

   public String getBuilding() {
      return this.building;
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }
}
