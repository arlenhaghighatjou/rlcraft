package io.github.classgraph;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AnnotationParameterValue extends ScanResultObject implements HasName, Comparable<AnnotationParameterValue> {
   private String name;
   private ObjectTypedValueWrapper value;

   AnnotationParameterValue() {
   }

   AnnotationParameterValue(String name, Object value) {
      this.name = name;
      this.value = new ObjectTypedValueWrapper(value);
   }

   public String getName() {
      return this.name;
   }

   public Object getValue() {
      return this.value == null ? null : this.value.get();
   }

   void setValue(Object newValue) {
      this.value = new ObjectTypedValueWrapper(newValue);
   }

   protected String getClassName() {
      throw new IllegalArgumentException("getClassName() cannot be called here");
   }

   protected ClassInfo getClassInfo() {
      throw new IllegalArgumentException("getClassInfo() cannot be called here");
   }

   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.value != null) {
         this.value.setScanResult(scanResult);
      }

   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo) {
      if (this.value != null) {
         this.value.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
      }

   }

   void convertWrapperArraysToPrimitiveArrays(ClassInfo annotationClassInfo) {
      if (this.value != null) {
         this.value.convertWrapperArraysToPrimitiveArrays(annotationClassInfo, this.name);
      }

   }

   Object instantiate(ClassInfo annotationClassInfo) {
      return this.value.instantiateOrGet(annotationClassInfo, this.name);
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      this.toString(buf);
      return buf.toString();
   }

   void toString(StringBuilder buf) {
      buf.append(this.name);
      buf.append("=");
      this.toStringParamValueOnly(buf);
   }

   void toStringParamValueOnly(StringBuilder buf) {
      if (this.value == null) {
         buf.append("null");
      } else {
         Object paramVal = this.value.get();
         Class<?> valClass = paramVal.getClass();
         if (valClass.isArray()) {
            buf.append('{');
            int j = 0;

            for(int n = Array.getLength(paramVal); j < n; ++j) {
               if (j > 0) {
                  buf.append(", ");
               }

               Object elt = Array.get(paramVal, j);
               buf.append(elt == null ? "null" : elt.toString());
            }

            buf.append('}');
         } else if (paramVal instanceof String) {
            buf.append('"');
            buf.append(paramVal.toString().replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r"));
            buf.append('"');
         } else if (paramVal instanceof Character) {
            buf.append('\'');
            buf.append(paramVal.toString().replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r"));
            buf.append('\'');
         } else {
            buf.append(paramVal.toString());
         }
      }

   }

   private String toStringParamValueOnly() {
      StringBuilder buf = new StringBuilder();
      this.toStringParamValueOnly(buf);
      return buf.toString();
   }

   public int compareTo(AnnotationParameterValue other) {
      if (other == this) {
         return 0;
      } else {
         int diff = this.name.compareTo(other.getName());
         if (diff != 0) {
            return diff;
         } else if (this.value.equals(other.value)) {
            return 0;
         } else {
            Object p0 = this.getValue();
            Object p1 = other.getValue();
            return p0 != null && p1 != null ? this.toStringParamValueOnly().compareTo(other.toStringParamValueOnly()) : (p0 == null ? 0 : 1) - (p1 == null ? 0 : 1);
         }
      }
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof AnnotationParameterValue)) {
         return false;
      } else {
         AnnotationParameterValue other = (AnnotationParameterValue)obj;
         return this.name.equals(other.name) && this.value == null == (other.value == null) && (this.value == null || this.value.equals(other.value));
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.value});
   }
}
