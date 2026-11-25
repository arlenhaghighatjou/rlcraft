package crafttweaker.api.potions;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.item.IItemStack;
import java.util.List;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenOperator;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.potions.IPotionEffect")
@ZenRegister
public interface IPotionEffect {
   @ZenOperator(OperatorType.COMPARE)
   int compareTo(IPotion var1);

   @ZenGetter("duration")
   int getDuration();

   @ZenGetter("potion")
   IPotion getPotion();

   @ZenGetter
   boolean doesShowParticles();

   @ZenGetter("amplifier")
   int getAmplifier();

   @ZenGetter("curativeItems")
   List<IItemStack> getCurativeItems();

   @ZenGetter("effectName")
   String getEffectName();

   @ZenGetter
   boolean isAmbient();

   @ZenGetter
   boolean isPotionDurationMax();

   @ZenMethod
   boolean isCurativeItem(IItemStack var1);

   @ZenSetter("isPotionDurationMax")
   void setIsPotionDurationMax(boolean var1);

   @ZenMethod
   void performEffect(IEntity var1);

   @ZenMethod
   default void combine(IPotionEffect other) {
   }

   Object getInternal();
}
