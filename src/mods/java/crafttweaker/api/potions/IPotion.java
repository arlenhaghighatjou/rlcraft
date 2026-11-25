package crafttweaker.api.potions;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.potions.IPotion")
@ZenRegister
public interface IPotion {
   @ZenGetter("name")
   String name();

   @ZenGetter("badEffect")
   boolean isBadEffect();

   @ZenGetter("liquidColor")
   int getLiquidColor();

   @ZenGetter("liquidColour")
   int getLiquidColour();

   @ZenMethod
   IPotionEffect makePotionEffect(int var1, int var2);

   @ZenMethod
   IPotionEffect makePotionEffect(int var1, int var2, boolean var3, boolean var4);

   @ZenGetter("curativeItems")
   List<IItemStack> getCurativeItems();

   @ZenGetter
   boolean hasStatusIcon();

   @ZenGetter
   boolean isBeneficial();

   @ZenMethod
   default void setBeneficial() {
   }

   @ZenGetter
   boolean isInstant();

   Object getInternal();
}
