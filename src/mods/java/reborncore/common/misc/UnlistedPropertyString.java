package reborncore.common.misc;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyString implements IUnlistedProperty<String> {
   private final String name;

   private UnlistedPropertyString(String n) {
      this.name = n;
   }

   public static UnlistedPropertyString create(String name) {
      return new UnlistedPropertyString(name);
   }

   public String getName() {
      return this.name;
   }

   public boolean isValid(String value) {
      return true;
   }

   public Class<String> getType() {
      return String.class;
   }

   public String valueToString(String value) {
      return value;
   }
}
