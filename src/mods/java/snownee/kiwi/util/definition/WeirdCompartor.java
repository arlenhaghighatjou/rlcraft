package snownee.kiwi.util.definition;

import java.util.Comparator;
import snownee.kiwi.crafting.input.ProcessingInput;

final class WeirdCompartor implements Comparator<ProcessingInput> {
   WeirdCompartor() {
   }

   public int compare(ProcessingInput a, ProcessingInput b) {
      if (a instanceof OreDictDefinition) {
         if (b instanceof OreDictDefinition) {
            int result = ((OreDictDefinition)a).ore.compareTo(((OreDictDefinition)b).ore);
            return result == 0 ? Integer.compare(((OreDictDefinition)b).size, ((OreDictDefinition)a).size) : result;
         } else {
            return -1;
         }
      } else {
         return b instanceof OreDictDefinition ? 1 : 0;
      }
   }
}
