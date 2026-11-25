package ljfa.tntutils.util;

import java.util.List;

public class ListHelper {
   public ListHelper() {
   }

   public static <T> void removeIf(List<T> list, Predicate<T> pred) {
      int insIndex = 0;

      for(int i = 0; i < list.size(); ++i) {
         T element = (T)list.get(i);
         if (!pred.test(element)) {
            if (insIndex != i) {
               list.set(insIndex, element);
            }

            ++insIndex;
         }
      }

      for(int i = list.size() - 1; i >= insIndex; --i) {
         list.remove(i);
      }

   }
}
