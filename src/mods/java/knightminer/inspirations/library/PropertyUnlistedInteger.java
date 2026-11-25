package knightminer.inspirations.library;

import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyUnlistedInteger implements IUnlistedProperty<Integer> {
   private final String name;

   public PropertyUnlistedInteger(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public boolean isValid(Integer value) {
      return value.intValue() >= 0;
   }

   public Class<Integer> getType() {
      return Integer.class;
   }

   public String valueToString(Integer value) {
      return value.toString();
   }
}
