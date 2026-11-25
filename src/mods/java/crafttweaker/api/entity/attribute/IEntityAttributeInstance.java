package crafttweaker.api.entity.attribute;

import crafttweaker.annotations.ZenRegister;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass("crafttweaker.entity.AttributeInstance")
public interface IEntityAttributeInstance {
   @ZenMethod
   @ZenGetter("attribute")
   IEntityAttribute getAttribute();

   @ZenMethod
   @ZenGetter("baseValue")
   double getBaseValue();

   @ZenMethod
   @ZenSetter("baseValue")
   void setBaseValue(double var1);

   @ZenMethod
   List<IEntityAttributeModifier> getModifiersByOperation(int var1);

   @ZenMethod
   @ZenGetter("modifiers")
   List<IEntityAttributeModifier> getModifiers();

   @ZenMethod
   boolean hasModifier(IEntityAttributeModifier var1);

   @ZenMethod
   IEntityAttributeModifier getModifier(String var1);

   @ZenMethod
   void applyModifier(IEntityAttributeModifier var1);

   @ZenMethod
   void removeModifier(IEntityAttributeModifier var1);

   @ZenMethod
   void removeModifier(String var1);

   @ZenMethod
   void removeAllModifiers();

   @ZenMethod
   @ZenGetter("attributeValue")
   double getAttributeValue();

   Object getInternal();
}
