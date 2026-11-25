package crafttweaker.api.enchantments;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import java.util.Map;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.enchantments.IEnchantment")
@ZenRegister
public interface IEnchantment {
   @ZenGetter("definition")
   IEnchantmentDefinition getDefinition();

   @ZenCaster
   @ZenMethod
   IData makeTag();

   @ZenCaster
   @ZenMethod
   IData makeBookTag();

   @ZenCaster
   @ZenMethod
   IItemStack makeBook();

   @ZenGetter("level")
   int getLevel();

   @ZenSetter("level")
   void setLevel(int var1);

   @ZenGetter("displayName")
   String displayName();

   Map<String, IData> makeTagInternal();

   Object makeNBTInternal();
}
