package io.github.classgraph;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

abstract class ScanResultObject {
   protected transient ScanResult scanResult;
   private transient ClassInfo classInfo;
   protected transient Class<?> classRef;

   ScanResultObject() {
   }

   void setScanResult(ScanResult scanResult) {
      this.scanResult = scanResult;
   }

   final Set<ClassInfo> findReferencedClassInfo() {
      Set<ClassInfo> refdClassInfo = new LinkedHashSet();
      if (this.scanResult != null) {
         this.findReferencedClassInfo(this.scanResult.classNameToClassInfo, refdClassInfo);
      }

      return refdClassInfo;
   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo) {
      ClassInfo ci = this.getClassInfo();
      if (ci != null) {
         refdClassInfo.add(ci);
      }

   }

   protected abstract String getClassName();

   ClassInfo getClassInfo() {
      if (this.classInfo == null) {
         if (this.scanResult == null) {
            return null;
         }

         String className = this.getClassName();
         if (className == null) {
            throw new IllegalArgumentException("Class name is not set");
         }

         this.classInfo = this.scanResult.getClassInfo(className);
      }

      return this.classInfo;
   }

   private String getClassInfoNameOrClassName() {
      ClassInfo ci = null;

      try {
         ci = this.getClassInfo();
      } catch (IllegalArgumentException var4) {
         ;
      }

      if (ci == null) {
         ci = this.classInfo;
      }

      String className;
      if (ci != null) {
         className = ci.getName();
      } else {
         className = this.getClassName();
      }

      if (className == null) {
         throw new IllegalArgumentException("Class name is not set");
      } else {
         return className;
      }
   }

   <T> Class<T> loadClass(Class<T> superclassOrInterfaceType, boolean ignoreExceptions) {
      if (this.classRef == null) {
         String className = this.getClassInfoNameOrClassName();
         if (this.scanResult != null) {
            this.classRef = this.scanResult.loadClass(className, superclassOrInterfaceType, ignoreExceptions);
         } else {
            try {
               this.classRef = Class.forName(className);
            } catch (Throwable var5) {
               if (!ignoreExceptions) {
                  throw new IllegalArgumentException("Could not load class " + className, var5);
               }
            }
         }
      }

      Class<T> classT = this.classRef;
      return classT;
   }

   <T> Class<T> loadClass(Class<T> superclassOrInterfaceType) {
      return this.<T>loadClass(superclassOrInterfaceType, false);
   }

   Class<?> loadClass(boolean ignoreExceptions) {
      if (this.classRef == null) {
         String className = this.getClassInfoNameOrClassName();
         if (this.scanResult != null) {
            this.classRef = this.scanResult.loadClass(className, ignoreExceptions);
         } else {
            try {
               this.classRef = Class.forName(className);
            } catch (Throwable var4) {
               if (!ignoreExceptions) {
                  throw new IllegalArgumentException("Could not load class " + className, var4);
               }
            }
         }
      }

      return this.classRef;
   }

   Class<?> loadClass() {
      return this.loadClass(false);
   }
}
