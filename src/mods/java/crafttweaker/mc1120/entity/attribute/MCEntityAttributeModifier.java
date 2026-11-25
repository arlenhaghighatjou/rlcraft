package crafttweaker.mc1120.entity.attribute;

import crafttweaker.api.entity.attribute.IEntityAttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier;

public class MCEntityAttributeModifier implements IEntityAttributeModifier {
   private final AttributeModifier attributeModifier;

   public MCEntityAttributeModifier(AttributeModifier attributeModifier) {
      this.attributeModifier = attributeModifier;
   }

   public String getUUID() {
      return this.attributeModifier.getID().toString();
   }

   public String getName() {
      return this.attributeModifier.getName();
   }

   public int getOperation() {
      return this.attributeModifier.getOperation();
   }

   public double getAmount() {
      return this.attributeModifier.getAmount();
   }

   public Object getInternal() {
      return this.attributeModifier;
   }
}
