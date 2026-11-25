package greymerk.roguelike.treasure.loot;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Predicate;

public enum Quality {
   WOOD("Hand-carved", "leather", "wooden"),
   STONE("Stonecut", "chainmail", "stone"),
   IRON("Iron", "iron", "iron"),
   GOLD("Gilded", "golden", "golden"),
   DIAMOND("Crystal", "diamond", "diamond");

   private final String descriptor;
   private final String armorName;
   private final String toolName;

   private Quality(String descriptor, String armorName, String toolName) {
      this.descriptor = descriptor;
      this.armorName = armorName;
      this.toolName = toolName;
   }

   public String getDescriptor() {
      return this.descriptor;
   }

   public String getArmorName() {
      return this.armorName;
   }

   public String getToolName() {
      return this.toolName;
   }

   public static Quality random(Random random) {
      Quality[] values = values();
      return values[random.nextInt(values.length)];
   }

   public static Quality get(int level) {
      return (Quality)Arrays.stream(values()).filter((quality) -> {
         return quality.ordinal() == level;
      }).findFirst().orElse(WOOD);
   }
}
