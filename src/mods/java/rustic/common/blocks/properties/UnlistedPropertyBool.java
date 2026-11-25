package rustic.common.blocks.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertyBool implements IUnlistedProperty<Boolean> {
   private String name;

   public UnlistedPropertyBool(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public boolean isValid(Boolean value) {
      return true;
   }

   public Class<Boolean> getType() {
      return Boolean.class;
   }

   public String valueToString(Boolean value) {
      return value.toString();
   }
}
