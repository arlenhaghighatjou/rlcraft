package io.github.classgraph;

import java.util.Map;
import java.util.Set;

public class ArrayClassInfo extends ClassInfo {
   private ArrayTypeSignature arrayTypeSignature;
   private ClassInfo elementClassInfo;

   ArrayClassInfo() {
   }

   ArrayClassInfo(ArrayTypeSignature arrayTypeSignature) {
      super(arrayTypeSignature.getClassName(), 0, (Resource)null);
      this.arrayTypeSignature = arrayTypeSignature;
      this.getElementClassInfo();
   }

   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
   }

   public String getTypeSignatureStr() {
      return this.arrayTypeSignature.getTypeSignatureStr();
   }

   public ClassTypeSignature getTypeSignature() {
      return null;
   }

   public ArrayTypeSignature getArrayTypeSignature() {
      return this.arrayTypeSignature;
   }

   public TypeSignature getElementTypeSignature() {
      return this.arrayTypeSignature.getElementTypeSignature();
   }

   public int getNumDimensions() {
      return this.arrayTypeSignature.getNumDimensions();
   }

   public ClassInfo getElementClassInfo() {
      if (this.elementClassInfo == null) {
         TypeSignature elementTypeSignature = this.arrayTypeSignature.getElementTypeSignature();
         if (!(elementTypeSignature instanceof BaseTypeSignature)) {
            this.elementClassInfo = this.arrayTypeSignature.getElementTypeSignature().getClassInfo();
            if (this.elementClassInfo != null) {
               this.classpathElement = this.elementClassInfo.classpathElement;
               this.classfileResource = this.elementClassInfo.classfileResource;
               this.classLoader = this.elementClassInfo.classLoader;
               this.isScannedClass = this.elementClassInfo.isScannedClass;
               this.isExternalClass = this.elementClassInfo.isExternalClass;
               this.moduleInfo = this.elementClassInfo.moduleInfo;
               this.packageInfo = this.elementClassInfo.packageInfo;
            }
         }
      }

      return this.elementClassInfo;
   }

   public Class<?> loadElementClass(boolean ignoreExceptions) {
      return this.arrayTypeSignature.loadElementClass(ignoreExceptions);
   }

   public Class<?> loadElementClass() {
      return this.arrayTypeSignature.loadElementClass();
   }

   public Class<?> loadClass(boolean ignoreExceptions) {
      if (this.classRef == null) {
         this.classRef = this.arrayTypeSignature.loadClass(ignoreExceptions);
      }

      return this.classRef;
   }

   public Class<?> loadClass() {
      if (this.classRef == null) {
         this.classRef = this.arrayTypeSignature.loadClass();
      }

      return this.classRef;
   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo) {
      super.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
   }

   public boolean equals(Object obj) {
      return super.equals(obj);
   }

   public int hashCode() {
      return super.hashCode();
   }
}
