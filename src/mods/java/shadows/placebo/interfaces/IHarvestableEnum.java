package shadows.placebo.interfaces;

import shadows.placebo.util.StackPrimer;

public interface IHarvestableEnum extends IPropertyEnum {
   default int getPredicateIndex() {
      return ((Enum)this).ordinal() / 8;
   }

   default int getMetadata() {
      return ((Enum)this).ordinal() % 8;
   }

   StackPrimer[] getDrops();
}
