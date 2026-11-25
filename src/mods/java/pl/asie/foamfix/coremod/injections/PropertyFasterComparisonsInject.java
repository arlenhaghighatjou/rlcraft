package pl.asie.foamfix.coremod.injections;

import com.google.common.base.Optional;
import java.util.Collection;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.util.IStringSerializable;

public class PropertyFasterComparisonsInject {
   public PropertyFasterComparisonsInject() {
   }

   public static class Bool extends PropertyHelper<Boolean> {
      protected Bool(String name) {
         super(name, Boolean.class);
      }

      public boolean equals(Object other) {
         return other == this || other instanceof PropertyBool && super.equals(other);
      }

      public int hashCode() {
         return super.hashCode();
      }

      public Collection<Boolean> getAllowedValues() {
         return null;
      }

      public Optional<Boolean> parseValue(String s) {
         return null;
      }

      public String getName(Boolean aBoolean) {
         return null;
      }
   }

   public static class Enum<T extends java.lang.Enum<T> & IStringSerializable> extends PropertyEnum<T> {
      protected Enum(String name, Class<T> valueClass, Collection<T> allowedValues) {
         super(name, valueClass, allowedValues);
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else if (p_equals_1_ instanceof PropertyEnum && super.equals(p_equals_1_)) {
            PropertyEnum<?> propertyenum = (PropertyEnum)p_equals_1_;
            return this.allowedValues.equals(propertyenum.allowedValues) && (this.getValueClass() == propertyenum.getValueClass() || this.nameToValue.equals(propertyenum.nameToValue));
         } else {
            return false;
         }
      }
   }
}
