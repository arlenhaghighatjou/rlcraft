package io.github.classgraph;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.TypeUtils;

public class MethodInfo extends ScanResultObject implements Comparable<MethodInfo>, HasName {
   private String declaringClassName;
   private String name;
   private int modifiers;
   AnnotationInfoList annotationInfo;
   private String typeDescriptorStr;
   private transient MethodTypeSignature typeDescriptor;
   private String typeSignatureStr;
   private transient MethodTypeSignature typeSignature;
   private String[] parameterNames;
   private int[] parameterModifiers;
   AnnotationInfo[][] parameterAnnotationInfo;
   private transient MethodParameterInfo[] parameterInfo;
   private boolean hasBody;

   MethodInfo() {
   }

   MethodInfo(String definingClassName, String methodName, AnnotationInfoList methodAnnotationInfo, int modifiers, String typeDescriptorStr, String typeSignatureStr, String[] parameterNames, int[] parameterModifiers, AnnotationInfo[][] parameterAnnotationInfo, boolean hasBody) {
      this.declaringClassName = definingClassName;
      this.name = methodName;
      this.modifiers = modifiers;
      this.typeDescriptorStr = typeDescriptorStr;
      this.typeSignatureStr = typeSignatureStr;
      this.parameterNames = parameterNames;
      this.parameterModifiers = parameterModifiers;
      this.parameterAnnotationInfo = parameterAnnotationInfo;
      this.annotationInfo = methodAnnotationInfo != null && !methodAnnotationInfo.isEmpty() ? methodAnnotationInfo : null;
      this.hasBody = hasBody;
   }

   public String getName() {
      return this.name;
   }

   public int getModifiers() {
      return this.modifiers;
   }

   public String getModifiersStr() {
      StringBuilder buf = new StringBuilder();
      TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.METHOD, this.isDefault(), buf);
      return buf.toString();
   }

   public ClassInfo getClassInfo() {
      return super.getClassInfo();
   }

   public MethodTypeSignature getTypeDescriptor() {
      if (this.typeDescriptor == null) {
         try {
            this.typeDescriptor = MethodTypeSignature.parse(this.typeDescriptorStr, this.declaringClassName);
            this.typeDescriptor.setScanResult(this.scanResult);
         } catch (ParseException var2) {
            throw new IllegalArgumentException(var2);
         }
      }

      return this.typeDescriptor;
   }

   public String getTypeDescriptorStr() {
      return this.typeDescriptorStr;
   }

   public MethodTypeSignature getTypeSignature() {
      if (this.typeSignature == null && this.typeSignatureStr != null) {
         try {
            this.typeSignature = MethodTypeSignature.parse(this.typeSignatureStr, this.declaringClassName);
            this.typeSignature.setScanResult(this.scanResult);
         } catch (ParseException var2) {
            throw new IllegalArgumentException(var2);
         }
      }

      return this.typeSignature;
   }

   public String getTypeSignatureStr() {
      return this.typeSignatureStr;
   }

   public MethodTypeSignature getTypeSignatureOrTypeDescriptor() {
      MethodTypeSignature typeSig = this.getTypeSignature();
      return typeSig != null ? typeSig : this.getTypeDescriptor();
   }

   public String getTypeSignatureOrTypeDescriptorStr() {
      return this.typeSignatureStr != null ? this.typeSignatureStr : this.typeDescriptorStr;
   }

   public boolean isConstructor() {
      return "<init>".equals(this.name);
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

   public boolean isSynchronized() {
      return Modifier.isSynchronized(this.modifiers);
   }

   public boolean isBridge() {
      return (this.modifiers & 64) != 0;
   }

   public boolean isSynthetic() {
      return (this.modifiers & 4096) != 0;
   }

   public boolean isVarArgs() {
      return (this.modifiers & 128) != 0;
   }

   public boolean isNative() {
      return Modifier.isNative(this.modifiers);
   }

   public boolean hasBody() {
      return this.hasBody;
   }

   public boolean isDefault() {
      ClassInfo classInfo = this.getClassInfo();
      return classInfo != null && classInfo.isInterface() && this.hasBody;
   }

   public MethodParameterInfo[] getParameterInfo() {
      if (this.parameterInfo == null) {
         List<TypeSignature> paramTypeDescriptors = this.getTypeDescriptor().getParameterTypeSignatures();
         List<TypeSignature> paramTypeSignatures = this.getTypeSignature() != null ? this.getTypeSignature().getParameterTypeSignatures() : null;
         int numParams = paramTypeDescriptors.size();
         if (paramTypeSignatures != null && paramTypeSignatures.size() > numParams) {
            throw ClassGraphException.newClassGraphException("typeSignatureParamTypes.size() > typeDescriptorParamTypes.size() for method " + this.declaringClassName + "." + this.name);
         }

         int otherParamMax = Math.max(this.parameterNames == null ? 0 : this.parameterNames.length, Math.max(this.parameterModifiers == null ? 0 : this.parameterModifiers.length, this.parameterAnnotationInfo == null ? 0 : this.parameterAnnotationInfo.length));
         if (otherParamMax > numParams) {
            throw ClassGraphException.newClassGraphException("Type descriptor for method " + this.declaringClassName + "." + this.name + " has insufficient parameters");
         }

         String[] paramNamesAligned = null;
         if (this.parameterNames != null && numParams > 0) {
            if (this.parameterNames.length == numParams) {
               paramNamesAligned = this.parameterNames;
            } else {
               paramNamesAligned = new String[numParams];
               int i = 0;

               for(int lenDiff = numParams - this.parameterNames.length; i < this.parameterNames.length; ++i) {
                  paramNamesAligned[lenDiff + i] = this.parameterNames[i];
               }
            }
         }

         int[] paramModifiersAligned = null;
         if (this.parameterModifiers != null && numParams > 0) {
            if (this.parameterModifiers.length == numParams) {
               paramModifiersAligned = this.parameterModifiers;
            } else {
               paramModifiersAligned = new int[numParams];
               int i = 0;

               for(int lenDiff = numParams - this.parameterModifiers.length; i < this.parameterModifiers.length; ++i) {
                  paramModifiersAligned[lenDiff + i] = this.parameterModifiers[i];
               }
            }
         }

         AnnotationInfo[][] paramAnnotationInfoAligned = null;
         if (this.parameterAnnotationInfo != null && numParams > 0) {
            if (this.parameterAnnotationInfo.length == numParams) {
               paramAnnotationInfoAligned = this.parameterAnnotationInfo;
            } else {
               paramAnnotationInfoAligned = new AnnotationInfo[numParams][];
               int i = 0;

               for(int lenDiff = numParams - this.parameterAnnotationInfo.length; i < this.parameterAnnotationInfo.length; ++i) {
                  paramAnnotationInfoAligned[lenDiff + i] = this.parameterAnnotationInfo[i];
               }
            }
         }

         List<TypeSignature> paramTypeSignaturesAligned = null;
         if (paramTypeSignatures != null && numParams > 0) {
            if (paramTypeSignatures.size() == paramTypeDescriptors.size()) {
               paramTypeSignaturesAligned = paramTypeSignatures;
            } else {
               paramTypeSignaturesAligned = new ArrayList(numParams);
               int i = 0;

               for(int n = numParams - paramTypeSignatures.size(); i < n; ++i) {
                  paramTypeSignaturesAligned.add((Object)null);
               }

               paramTypeSignaturesAligned.addAll(paramTypeSignatures);
            }
         }

         this.parameterInfo = new MethodParameterInfo[numParams];

         for(int i = 0; i < numParams; ++i) {
            this.parameterInfo[i] = new MethodParameterInfo(this, paramAnnotationInfoAligned == null ? null : paramAnnotationInfoAligned[i], paramModifiersAligned == null ? 0 : paramModifiersAligned[i], (TypeSignature)paramTypeDescriptors.get(i), paramTypeSignaturesAligned == null ? null : (TypeSignature)paramTypeSignaturesAligned.get(i), paramNamesAligned == null ? null : paramNamesAligned[i]);
            this.parameterInfo[i].setScanResult(this.scanResult);
         }
      }

      return this.parameterInfo;
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

   public boolean hasParameterAnnotation(String annotationName) {
      for(MethodParameterInfo methodParameterInfo : this.getParameterInfo()) {
         if (methodParameterInfo.hasAnnotation(annotationName)) {
            return true;
         }
      }

      return false;
   }

   public Method loadClassAndGetMethod() throws IllegalArgumentException {
      MethodParameterInfo[] allParameterInfo = this.getParameterInfo();
      List<Class<?>> parameterClasses = new ArrayList(allParameterInfo.length);

      for(MethodParameterInfo mpi : allParameterInfo) {
         TypeSignature parameterType = mpi.getTypeSignatureOrTypeDescriptor();
         parameterClasses.add(parameterType.loadClass());
      }

      Class<?>[] parameterClassesArr = (Class[])parameterClasses.toArray(new Class[0]);

      try {
         return this.loadClass().getMethod(this.getName(), parameterClassesArr);
      } catch (NoSuchMethodException var9) {
         try {
            return this.loadClass().getDeclaredMethod(this.getName(), parameterClassesArr);
         } catch (NoSuchMethodException var8) {
            throw new IllegalArgumentException("No such method: " + this.getClassName() + "." + this.getName());
         }
      }
   }

   void handleRepeatableAnnotations(Set<String> allRepeatableAnnotationNames) {
      if (this.annotationInfo != null) {
         this.annotationInfo.handleRepeatableAnnotations(allRepeatableAnnotationNames, this.getClassInfo(), ClassInfo.RelType.METHOD_ANNOTATIONS, ClassInfo.RelType.CLASSES_WITH_METHOD_ANNOTATION, ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_METHOD_ANNOTATION);
      }

      if (this.parameterAnnotationInfo != null) {
         for(int i = 0; i < this.parameterAnnotationInfo.length; ++i) {
            AnnotationInfo[] pai = this.parameterAnnotationInfo[i];
            if (pai != null && pai.length > 0) {
               boolean hasRepeatableAnnotation = false;

               for(AnnotationInfo ai : pai) {
                  if (allRepeatableAnnotationNames.contains(ai.getName())) {
                     hasRepeatableAnnotation = true;
                     break;
                  }
               }

               if (hasRepeatableAnnotation) {
                  AnnotationInfoList aiList = new AnnotationInfoList(pai.length);

                  for(AnnotationInfo ai : pai) {
                     aiList.add(ai);
                  }

                  aiList.handleRepeatableAnnotations(allRepeatableAnnotationNames, this.getClassInfo(), ClassInfo.RelType.METHOD_PARAMETER_ANNOTATIONS, ClassInfo.RelType.CLASSES_WITH_METHOD_PARAMETER_ANNOTATION, ClassInfo.RelType.CLASSES_WITH_NONPRIVATE_METHOD_PARAMETER_ANNOTATION);
                  this.parameterAnnotationInfo[i] = (AnnotationInfo[])aiList.toArray(new AnnotationInfo[0]);
               }
            }
         }
      }

   }

   protected String getClassName() {
      return this.declaringClassName;
   }

   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.typeDescriptor != null) {
         this.typeDescriptor.setScanResult(scanResult);
      }

      if (this.typeSignature != null) {
         this.typeSignature.setScanResult(scanResult);
      }

      if (this.annotationInfo != null) {
         for(AnnotationInfo ai : this.annotationInfo) {
            ai.setScanResult(scanResult);
         }
      }

      if (this.parameterAnnotationInfo != null) {
         for(AnnotationInfo[] pai : this.parameterAnnotationInfo) {
            if (pai != null) {
               for(AnnotationInfo ai : pai) {
                  ai.setScanResult(scanResult);
               }
            }
         }
      }

      if (this.parameterInfo != null) {
         for(MethodParameterInfo mpi : this.parameterInfo) {
            mpi.setScanResult(scanResult);
         }
      }

   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo) {
      MethodTypeSignature methodSig = this.getTypeSignature();
      if (methodSig != null) {
         methodSig.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
      }

      MethodTypeSignature methodDesc = this.getTypeDescriptor();
      if (methodDesc != null) {
         methodDesc.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
      }

      if (this.annotationInfo != null) {
         for(AnnotationInfo ai : this.annotationInfo) {
            ai.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
         }
      }

      for(MethodParameterInfo mpi : this.getParameterInfo()) {
         AnnotationInfo[] aiArr = mpi.annotationInfo;
         if (aiArr != null) {
            for(AnnotationInfo ai : aiArr) {
               ai.findReferencedClassInfo(classNameToClassInfo, refdClassInfo);
            }
         }
      }

   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof MethodInfo)) {
         return false;
      } else {
         MethodInfo other = (MethodInfo)obj;
         return this.declaringClassName.equals(other.declaringClassName) && this.typeDescriptorStr.equals(other.typeDescriptorStr) && this.name.equals(other.name);
      }
   }

   public int hashCode() {
      return this.name.hashCode() + this.typeDescriptorStr.hashCode() * 11 + this.declaringClassName.hashCode() * 57;
   }

   public int compareTo(MethodInfo other) {
      int diff0 = this.declaringClassName.compareTo(other.declaringClassName);
      if (diff0 != 0) {
         return diff0;
      } else {
         int diff1 = this.name.compareTo(other.name);
         return diff1 != 0 ? diff1 : this.typeDescriptorStr.compareTo(other.typeDescriptorStr);
      }
   }

   public String toString() {
      MethodTypeSignature methodType = this.getTypeSignatureOrTypeDescriptor();
      StringBuilder buf = new StringBuilder();
      if (this.annotationInfo != null) {
         for(AnnotationInfo annotation : this.annotationInfo) {
            if (buf.length() > 0) {
               buf.append(' ');
            }

            annotation.toString(buf);
         }
      }

      if (this.modifiers != 0) {
         if (buf.length() > 0) {
            buf.append(' ');
         }

         TypeUtils.modifiersToString(this.modifiers, TypeUtils.ModifierType.METHOD, this.isDefault(), buf);
      }

      List<TypeParameter> typeParameters = methodType.getTypeParameters();
      if (!typeParameters.isEmpty()) {
         if (buf.length() > 0) {
            buf.append(' ');
         }

         buf.append('<');

         for(int i = 0; i < typeParameters.size(); ++i) {
            if (i > 0) {
               buf.append(", ");
            }

            String typeParamStr = ((TypeParameter)typeParameters.get(i)).toString();
            buf.append(typeParamStr);
         }

         buf.append('>');
      }

      if (!this.isConstructor()) {
         if (buf.length() > 0) {
            buf.append(' ');
         }

         buf.append(methodType.getResultType().toString());
      }

      buf.append(' ');
      if (this.name != null) {
         buf.append(this.name);
      }

      MethodParameterInfo[] allParamInfo = this.getParameterInfo();
      boolean hasParamNames = false;

      for(MethodParameterInfo methodParamInfo : allParamInfo) {
         if (methodParamInfo.getName() != null) {
            hasParamNames = true;
            break;
         }
      }

      int varArgsParamIndex = -1;
      if (this.isVarArgs()) {
         for(int i = allParamInfo.length - 1; i >= 0; --i) {
            int mods = allParamInfo[i].getModifiers();
            if ((mods & 4096) == 0 && (mods & '耀') == 0) {
               TypeSignature paramType = allParamInfo[i].getTypeSignatureOrTypeDescriptor();
               if (paramType instanceof ArrayTypeSignature) {
                  varArgsParamIndex = i;
                  break;
               }
            }
         }
      }

      buf.append('(');
      int i = 0;

      for(int numParams = allParamInfo.length; i < numParams; ++i) {
         MethodParameterInfo paramInfo = allParamInfo[i];
         if (i > 0) {
            buf.append(", ");
         }

         if (paramInfo.annotationInfo != null) {
            for(AnnotationInfo ai : paramInfo.annotationInfo) {
               ai.toString(buf);
               buf.append(' ');
            }
         }

         MethodParameterInfo.modifiersToString(paramInfo.getModifiers(), buf);
         TypeSignature paramType = paramInfo.getTypeSignatureOrTypeDescriptor();
         if (i == varArgsParamIndex) {
            if (!(paramType instanceof ArrayTypeSignature)) {
               throw new IllegalArgumentException("Got non-array type for last parameter of varargs method " + this.name);
            }

            ArrayTypeSignature arrayType = (ArrayTypeSignature)paramType;
            if (arrayType.getNumDimensions() == 0) {
               throw new IllegalArgumentException("Got a zero-dimension array type for last parameter of varargs method " + this.name);
            }

            buf.append((new ArrayTypeSignature(arrayType.getElementTypeSignature(), arrayType.getNumDimensions() - 1, (String)null)).toString());
            buf.append("...");
         } else {
            buf.append(paramType.toString());
         }

         if (hasParamNames) {
            String paramName = paramInfo.getName();
            if (paramName != null) {
               buf.append(' ');
               buf.append(paramName);
            }
         }
      }

      buf.append(')');
      if (!methodType.getThrowsSignatures().isEmpty()) {
         buf.append(" throws ");

         for(int i = 0; i < methodType.getThrowsSignatures().size(); ++i) {
            if (i > 0) {
               buf.append(", ");
            }

            buf.append(((ClassRefOrTypeVariableSignature)methodType.getThrowsSignatures().get(i)).toString());
         }
      }

      return buf.toString();
   }
}
