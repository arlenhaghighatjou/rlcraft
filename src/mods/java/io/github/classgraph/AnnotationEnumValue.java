package io.github.classgraph;

import java.lang.reflect.Field;

public class AnnotationEnumValue extends ScanResultObject implements Comparable<AnnotationEnumValue> {
   private String className;
   private String valueName;

   AnnotationEnumValue() {
   }

   AnnotationEnumValue(String className, String constValueName) {
      this.className = className;
      this.valueName = constValueName;
   }

   public String getClassName() {
      return this.className;
   }

   public String getValueName() {
      return this.valueName;
   }

   public String getName() {
      return this.className + "." + this.valueName;
   }

   public Object loadClassAndReturnEnumValue(boolean ignoreExceptions) throws IllegalArgumentException {
      Class<?> classRef = super.loadClass(ignoreExceptions);
      if (classRef == null) {
         if (ignoreExceptions) {
            return null;
         } else {
            throw new IllegalArgumentException("Enum class " + this.className + " could not be loaded");
         }
      } else if (!classRef.isEnum()) {
         throw new IllegalArgumentException("Class " + this.className + " is not an enum");
      } else {
         Field field;
         try {
            field = classRef.getDeclaredField(this.valueName);
         } catch (SecurityException | ReflectiveOperationException var6) {
            throw new IllegalArgumentException("Could not find enum constant " + this.toString(), var6);
         }

         if (!field.isEnumConstant()) {
            throw new IllegalArgumentException("Field " + this.toString() + " is not an enum constant");
         } else {
            try {
               return field.get((Object)null);
            } catch (SecurityException | ReflectiveOperationException var5) {
               throw new IllegalArgumentException("Field " + this.toString() + " is not accessible", var5);
            }
         }
      }
   }

   public Object loadClassAndReturnEnumValue() throws IllegalArgumentException {
      return this.loadClassAndReturnEnumValue(false);
   }

   public int compareTo(AnnotationEnumValue o) {
      int diff = this.className.compareTo(o.className);
      return diff == 0 ? this.valueName.compareTo(o.valueName) : diff;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof AnnotationEnumValue)) {
         return false;
      } else {
         return this.compareTo((AnnotationEnumValue)obj) == 0;
      }
   }

   public int hashCode() {
      return this.className.hashCode() * 11 + this.valueName.hashCode();
   }

   public String toString() {
      return this.className + "." + this.valueName;
   }
}
