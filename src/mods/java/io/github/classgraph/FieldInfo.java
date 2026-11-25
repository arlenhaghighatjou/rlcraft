package io.github.classgraph;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.TypeUtils;

public class FieldInfo extends ScanResultObject implements Comparable<FieldInfo>, HasName {
   private String declaringClassName;
   private String name;
   private int modifiers;
   private String typeSignatureStr;
   private String typeDescriptorStr;
   private transient TypeSignature typeSignature;
   private transient TypeSignature typeDescriptor;
   private ObjectTypedValueWrapper constantInitializerValue;
   AnnotationInfoList annotationInfo;

   FieldInfo() {
   }

   FieldInfo(String definingClassName, String fieldName, int modifiers, String typeDescriptorStr, String typeSignatureStr, Object constantInitializerValue, AnnotationInfoList annotationInfo) {
      if (fieldName == null) {
         throw new IllegalArgumentException();
      } else {
         this.declaringClassName = definingClassName;
         this.name = fieldName;
         this.modifiers = modifiers;
         this.typeDescriptorStr = typeDescriptorStr;
         this.typeSignatureStr = typeSignatureStr;
         this.constantInitializerValue = constantInitializerValue == null ? null : new ObjectTypedValueWrapper(constantInitializerValue);
         this.annotationInfo = annotationInfo != null && !annotationInfo.isEmpty() ? annotationInfo : null;
      }
   }

   public String getName() {
      return this.name;
   }

   public ClassInfo getClassInfo() {
      return super.getClassInfo();
   }

   public String getModifierStr() {
      StringBuilder buf = new StringBuilder();
      TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.FIELD, false, buf);
      return buf.toString();
   }

   public boolean isPublic() {
      return Modifier.isPublic(this.modifiers);
   }

   public boolean isStatic() {
      return Modifier.isStatic(this.modifiers);
   }

   public boolean isFinal() {
      return Modifier.isFinal(this.modifiers);
   }

   public boolean isTransient() {
      return Modifier.isTransient(this.modifiers);
   }

   public int getModifiers() {
      return this.modifiers;
   }

   public TypeSignature getTypeDescriptor() {
      if (this.typeDescriptorStr == null) {
         return null;
      } else {
         if (this.typeDescriptor == null) {
            try {
               this.typeDescriptor = TypeSignature.parse(this.typeDescriptorStr, this.declaringClassName);
               this.typeDescriptor.setScanResult(this.scanResult);
            } catch (ParseException var2) {
               throw new IllegalArgumentException(var2);
            }
         }

         return this.typeDescriptor;
      }
   }

   public String getTypeDescriptorStr() {
      return this.typeDescriptorStr;
   }

   public TypeSignature getTypeSignature() {
      if (this.typeSignatureStr == null) {
         return null;
      } else {
         if (this.typeSignature == null) {
            try {
               this.typeSignature = TypeSignature.parse(this.typeSignatureStr, this.declaringClassName);
               this.typeSignature.setScanResult(this.scanResult);
            } catch (ParseException var2) {
               throw new IllegalArgumentException(var2);
            }
         }

         return this.typeSignature;
      }
   }

   public String getTypeSignatureStr() {
      return this.typeSignatureStr;
   }

   public TypeSignature getTypeSignatureOrTypeDescriptor() {
      TypeSignature typeSig = this.getTypeSignature();
      return typeSig != null ? typeSig : this.getTypeDescriptor();
   }

   public String getTypeSignatureOrTypeDescriptorStr() {
      return this.typeSignatureStr != null ? this.typeSignatureStr : this.typeDescriptorStr;
   }

   public Object getConstantInitializerValue() {
      if (!this.scanResult.scanSpec.enableStaticFinalFieldConstantInitializerValues) {
         throw new IllegalArgumentException("Please call ClassGraph#enableStaticFinalFieldConstantInitializerValues() before #scan()");
      } else {
         return this.constantInitializerValue == null ? null : this.constantInitializerValue.get();
      }
   }

   public AnnotationInfoList getAnnotationInfo() {
      if (!this.scanResult.scanSpec.enableAnnotationInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
      } else {
         return this.annotationInfo == null ? AnnotationInfoList.EMPTY_LIST : AnnotationInfoList.getIndirectAnnotations(this.annotationInfo, (ClassInfo)null);
      }
   }

   public AnnotationInfo getAnnotationInfo(String annotationName) {
      return (AnnotationInfo)this.getAnnotationInfo().get(annotationName);
   }

   public AnnotationInfoList getAnnotationInfoRepeatable(String annotationName) {
      return this.getAnnotationInfo().getRepeatable(annotationName);
   }

   public boolean hasAnnotation(String annotationName) {
      return this.getAnnotationInfo().containsName(annotationName);
   }

   public Field loadClassAndGetField() throws IllegalArgumentException {
      try {
         return this.loadClass().getField(this.getName());
      } catch (NoSuchFieldException var4) {
         try {
            return this.loadClass().getDeclaredField(this.getName());
         } catch (NoSuchFieldException var3) {
            throw new IllegalArgumentException("No such field: " + this.getClassName() + "." + this.getName());
         }
      }
   }

   void handleRepeatableAnnotations(Set<String> allRepeatableAnnotationNames) {
      if (this.annotationInfo != null) {
         this.annotationInfo.handleRepeatableAnnotations(allRepeatableAnnotationNames, this.getClassInfo(), ClassInfo.RelType.FIELD_ANNOTATIONS, ClassInfo.RelType.CLASSES_WITH_FIELD_ANNOTATION, ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_FIELD_ANNOTATION);
      }

   }

   protected String getClassName() {
      return this.declaringClassName;
   }

   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.typeSignature != null) {
         this.typeSignature.setScanResult(scanResult);
      }

      if (this.typeDescriptor != null) {
         this.typeDescriptor.setScanResult(scanResult);
      }

      if (this.annotationInfo != null) {
         for(AnnotationInfo ai : this.annotationInfo) {
            ai.setScanResult(scanResult);
         }
      }

   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo) {
      TypeSignature methodSig = this.getTypeSignature();
      if (methodSig != null) {
         methodSig.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
      }

      TypeSignature methodDesc = this.getTypeDescriptor();
      if (methodDesc != null) {
         methodDesc.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
      }

      if (this.annotationInfo != null) {
         for(AnnotationInfo ai : this.annotationInfo) {
            ai.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
         }
      }

   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof FieldInfo)) {
         return false;
      } else {
         FieldInfo other = (FieldInfo)obj;
         return this.declaringClassName.equals(other.declaringClassName) && this.name.equals(other.name);
      }
   }

   public int hashCode() {
      return this.name.hashCode() + this.declaringClassName.hashCode() * 11;
   }

   public int compareTo(FieldInfo other) {
      int diff = this.declaringClassName.compareTo(other.declaringClassName);
      return diff != 0 ? diff : this.name.compareTo(other.name);
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      if (this.annotationInfo != null) {
         for(AnnotationInfo annotation : this.annotationInfo) {
            if (buf.length() > 0) {
               buf.append(' ');
            }

            buf.append(annotation.toString());
         }
      }

      if (this.modifiers != 0) {
         if (buf.length() > 0) {
            buf.append(' ');
         }

         TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.FIELD, false, buf);
      }

      if (buf.length() > 0) {
         buf.append(' ');
      }

      buf.append(this.getTypeSignatureOrTypeDescriptor().toString());
      buf.append(' ');
      buf.append(this.name);
      if (this.constantInitializerValue != null) {
         Object val = this.constantInitializerValue.get();
         buf.append(" = ");
         if (val instanceof String) {
            buf.append('"').append(((String)val).replace("\\", "\\\\").replace("\"", "\\\"")).append('"');
         } else if (val instanceof Character) {
            buf.append('\'').append(((Character)val).toString().replace("\\", "\\\\").replaceAll("'", "\\'")).append('\'');
         } else {
            buf.append(val.toString());
         }
      }

      return buf.toString();
   }
}
