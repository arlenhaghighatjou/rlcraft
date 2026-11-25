package crafttweaker.mc1120.entity.attribute;

import crafttweaker.api.entity.attribute.IEntityAttribute;
import net.minecraft.entity.ai.attributes.IAttribute;

public class MCEntityAttribute implements IEntityAttribute {
   private IAttribute attribute;

   public MCEntityAttribute(IAttribute attribute) {
      this.attribute = attribute;
   }

   public String getName() {
      return this.attribute.getName();
   }

   public double clampValue(double value) {
      return this.attribute.clampValue(value);
   }

   public double getDefaultValue() {
      return this.attribute.getDefaultValue();
   }

   public boolean getShouldWatch() {
      return this.attribute.getShouldWatch();
   }

   public IEntityAttribute getParent() {
      return new MCEntityAttribute(this.attribute.getParent());
   }

   public Object getInternal() {
      return this.attribute;
   }
}
