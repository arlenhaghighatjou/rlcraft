package io.github.classgraph;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class AnnotationParameterValueList extends MappableInfoList<AnnotationParameterValue> {
   static final AnnotationParameterValueList EMPTY_LIST = new AnnotationParameterValueList();

   public static AnnotationParameterValueList emptyList() {
      return EMPTY_LIST;
   }

   public AnnotationParameterValueList() {
   }

   public AnnotationParameterValueList(int sizeHint) {
      super(sizeHint);
   }

   public AnnotationParameterValueList(Collection<AnnotationParameterValue> annotationParameterValueCollection) {
      super(annotationParameterValueCollection);
   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo) {
      for(AnnotationParameterValue apv : this) {
         apv.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
      }

   }

   void convertWrapperArraysToPrimitiveArrays(ClassInfo annotationClassInfo) {
      for(AnnotationParameterValue apv : this) {
         apv.convertWrapperArraysToPrimitiveArrays(annotationClassInfo);
      }

   }

   public Object getValue(String parameterName) {
      AnnotationParameterValue apv = (AnnotationParameterValue)this.get(parameterName);
      return apv == null ? null : apv.getValue();
   }

   static {
      EMPTY_LIST.makeUnmodifiable();
   }
}
