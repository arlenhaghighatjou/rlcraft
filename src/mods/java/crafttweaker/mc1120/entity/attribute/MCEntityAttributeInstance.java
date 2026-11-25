package crafttweaker.mc1120.entity.attribute;

import crafttweaker.api.entity.attribute.IEntityAttribute;
import crafttweaker.api.entity.attribute.IEntityAttributeInstance;
import crafttweaker.api.entity.attribute.IEntityAttributeModifier;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public class MCEntityAttributeInstance implements IEntityAttributeInstance {
   private final IAttributeInstance attributeInstance;

   public MCEntityAttributeInstance(IAttributeInstance attributeInstance) {
      this.attributeInstance = attributeInstance;
   }

   public IEntityAttribute getAttribute() {
      return new MCEntityAttribute(this.attributeInstance.getAttribute());
   }

   public double getBaseValue() {
      return this.attributeInstance.getBaseValue();
   }

   public void setBaseValue(double baseValue) {
      this.attributeInstance.setBaseValue(baseValue);
   }

   public List<IEntityAttributeModifier> getModifiersByOperation(int operation) {
      return (List)this.attributeInstance.getModifiersByOperation(operation).stream().filter(Objects::nonNull).map(MCEntityAttributeModifier::new).collect(Collectors.toList());
   }

   public List<IEntityAttributeModifier> getModifiers() {
      return (List)this.attributeInstance.getModifiers().stream().map(MCEntityAttributeModifier::new).collect(Collectors.toList());
   }

   public boolean hasModifier(IEntityAttributeModifier modifier) {
      return this.attributeInstance.hasModifier((AttributeModifier)modifier.getInternal());
   }

   public IEntityAttributeModifier getModifier(String uuid) {
      return (IEntityAttributeModifier)Optional.ofNullable(this.attributeInstance.getModifier(EaglercraftUUID.fromString(uuid))).map(MCEntityAttributeModifier::new).orElse((Object)null);
   }

   public void applyModifier(IEntityAttributeModifier modifier) {
      this.attributeInstance.applyModifier((AttributeModifier)modifier.getInternal());
   }

   public void removeModifier(IEntityAttributeModifier modifier) {
      this.attributeInstance.removeModifier((EaglercraftUUID)modifier.getInternal());
   }

   public void removeModifier(String uuid) {
      this.attributeInstance.removeModifier(EaglercraftUUID.fromString(uuid));
   }

   public void removeAllModifiers() {
      Collection var10000 = this.attributeInstance.getModifiers();
      IAttributeInstance var10001 = this.attributeInstance;
      this.attributeInstance.getClass();
      var10000.forEach(var10001::removeModifier);
   }

   public double getAttributeValue() {
      return this.attributeInstance.getAttributeValue();
   }

   public Object getInternal() {
      return this.attributeInstance;
   }
}
