package ivorius.ivtoolkit.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IvCollections {
   public IvCollections() {
   }

   public static <O> ArrayList<O> modifiableCopy(List<O> list) {
      ArrayList<O> newList = new ArrayList();
      newList.addAll(list);
      return newList;
   }

   public static <O> ArrayList<O> modifiableCopyWithout(List<O> list, int... removeIndices) {
      int[] newIndices = (int[])removeIndices.clone();
      Arrays.sort(removeIndices);
      ArrayList<O> newList = modifiableCopy(list);

      for(int i = newIndices.length - 1; i >= 0; --i) {
         newList.remove(newIndices[i]);
      }

      return newList;
   }

   @SafeVarargs
   public static <O> ArrayList<O> modifiableCopyWith(List<O> list, O... objects) {
      ArrayList<O> newList = modifiableCopy(list);
      Collections.addAll(newList, objects);
      return newList;
   }

   public static <O> void setContentsOfList(List<O> list, List<O> contents) {
      list.clear();
      list.addAll(contents);
   }
}
