package io.github.classgraph;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class FieldInfoList extends MappableInfoList<FieldInfo> {
   static final FieldInfoList EMPTY_LIST = new FieldInfoList();

   public static FieldInfoList emptyList() {
      return EMPTY_LIST;
   }

   public FieldInfoList() {
   }

   public FieldInfoList(int sizeHint) {
      super(sizeHint);
   }

   public FieldInfoList(Collection<FieldInfo> fieldInfoCollection) {
      super(fieldInfoCollection);
   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo) {
      for(FieldInfo fi : this) {
         fi.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
      }

   }

   public FieldInfoList filter(FieldInfoList.FieldInfoFilter filter) {
      FieldInfoList fieldInfoFiltered = new FieldInfoList();

      for(FieldInfo resource : this) {
         if (filter.accept(resource)) {
            fieldInfoFiltered.add(resource);
         }
      }

      return fieldInfoFiltered;
   }

   static {
      EMPTY_LIST.makeUnmodifiable();
   }

   @FunctionalInterface
   public interface FieldInfoFilter {
      boolean accept(FieldInfo var1);
   }
}
