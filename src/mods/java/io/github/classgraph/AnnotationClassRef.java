package io.github.classgraph;

import nonapi.io.github.classgraph.types.ParseException;

public class AnnotationClassRef extends ScanResultObject {
   private String typeDescriptorStr;
   private transient TypeSignature typeSignature;
   private transient String className;

   AnnotationClassRef() {
   }

   AnnotationClassRef(String typeDescriptorStr) {
      this.typeDescriptorStr = typeDescriptorStr;
   }

   public String getName() {
      return this.getClassName();
   }

   private TypeSignature getTypeSignature() {
      if (this.typeSignature == null) {
         try {
            this.typeSignature = TypeSignature.parse(this.typeDescriptorStr, (String)null);
            this.typeSignature.setScanResult(this.scanResult);
         } catch (ParseException var2) {
            throw new IllegalArgumentException(var2);
         }
      }

      return this.typeSignature;
   }

   public Class<?> loadClass(boolean ignoreExceptions) {
      this.getTypeSignature();
      if (this.typeSignature instanceof BaseTypeSignature) {
         return ((BaseTypeSignature)this.typeSignature).getType();
      } else if (this.typeSignature instanceof ClassRefTypeSignature) {
         return ((ClassRefTypeSignature)this.typeSignature).loadClass(ignoreExceptions);
      } else if (this.typeSignature instanceof ArrayTypeSignature) {
         return ((ArrayTypeSignature)this.typeSignature).loadClass(ignoreExceptions);
      } else {
         throw new IllegalArgumentException("Got unexpected type " + this.typeSignature.getClass().getName() + " for ref type signature: " + this.typeDescriptorStr);
      }
   }

   public Class<?> loadClass() {
      return this.loadClass(false);
   }

   protected String getClassName() {
      if (this.className == null) {
         this.getTypeSignature();
         if (this.typeSignature instanceof BaseTypeSignature) {
            this.className = ((BaseTypeSignature)this.typeSignature).getTypeStr();
         } else if (this.typeSignature instanceof ClassRefTypeSignature) {
            this.className = ((ClassRefTypeSignature)this.typeSignature).getFullyQualifiedClassName();
         } else {
            if (!(this.typeSignature instanceof ArrayTypeSignature)) {
               throw new IllegalArgumentException("Got unexpected type " + this.typeSignature.getClass().getName() + " for ref type signature: " + this.typeDescriptorStr);
            }

            this.className = ((ArrayTypeSignature)this.typeSignature).getClassName();
         }
      }

      return this.className;
   }

   public ClassInfo getClassInfo() {
      this.getTypeSignature();
      return this.typeSignature.getClassInfo();
   }

   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.typeSignature != null) {
         this.typeSignature.setScanResult(scanResult);
      }

   }

   public int hashCode() {
      return this.getTypeSignature().hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof AnnotationClassRef) ? false : this.getTypeSignature().equals(((AnnotationClassRef)obj).getTypeSignature());
      }
   }

   public String toString() {
      if (this.scanResult != null) {
         ClassInfo ci = this.getClassInfo();
         if (ci != null && ci.isInterfaceOrAnnotation()) {
            ;
         }
      }

      return this.getTypeSignature().toString() + ".class";
   }
}
