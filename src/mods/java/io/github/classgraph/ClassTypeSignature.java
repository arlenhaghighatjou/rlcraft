package io.github.classgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;
import nonapi.io.github.classgraph.types.TypeUtils;
import nonapi.io.github.classgraph.utils.Join;

public final class ClassTypeSignature extends HierarchicalTypeSignature {
   private final ClassInfo classInfo;
   final List<TypeParameter> typeParameters;
   private final ClassRefTypeSignature superclassSignature;
   private final List<ClassRefTypeSignature> superinterfaceSignatures;

   private ClassTypeSignature(ClassInfo classInfo, List<TypeParameter> typeParameters, ClassRefTypeSignature superclassSignature, List<ClassRefTypeSignature> superinterfaceSignatures) {
      this.classInfo = classInfo;
      this.typeParameters = typeParameters;
      this.superclassSignature = superclassSignature;
      this.superinterfaceSignatures = superinterfaceSignatures;
   }

   public List<TypeParameter> getTypeParameters() {
      return this.typeParameters;
   }

   public ClassRefTypeSignature getSuperclassSignature() {
      return this.superclassSignature;
   }

   public List<ClassRefTypeSignature> getSuperinterfaceSignatures() {
      return this.superinterfaceSignatures;
   }

   static ClassTypeSignature parse(String typeDescriptor, ClassInfo classInfo) throws ParseException {
      Parser parser = new Parser(typeDescriptor);
      String definingClassNameNull = null;
      List<TypeParameter> typeParameters = TypeParameter.parseList(parser, definingClassNameNull);
      ClassRefTypeSignature superclassSignature = ClassRefTypeSignature.parse(parser, definingClassNameNull);
      List<ClassRefTypeSignature> superinterfaceSignatures;
      if (parser.hasMore()) {
         superinterfaceSignatures = new ArrayList();

         while(parser.hasMore()) {
            ClassRefTypeSignature superinterfaceSignature = ClassRefTypeSignature.parse(parser, definingClassNameNull);
            if (superinterfaceSignature == null) {
               throw new ParseException(parser, "Could not parse superinterface signature");
            }

            superinterfaceSignatures.add(superinterfaceSignature);
         }
      } else {
         superinterfaceSignatures = Collections.emptyList();
      }

      if (parser.hasMore()) {
         throw new ParseException(parser, "Extra characters at end of type descriptor");
      } else {
         return new ClassTypeSignature(classInfo, typeParameters, superclassSignature, superinterfaceSignatures);
      }
   }

   protected String getClassName() {
      return this.classInfo != null ? this.classInfo.getName() : null;
   }

   protected ClassInfo getClassInfo() {
      return this.classInfo;
   }

   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.typeParameters != null) {
         for(TypeParameter typeParameter : this.typeParameters) {
            typeParameter.setScanResult(scanResult);
         }
      }

      if (this.superclassSignature != null) {
         this.superclassSignature.setScanResult(scanResult);
      }

      if (this.superinterfaceSignatures != null) {
         for(ClassRefTypeSignature classRefTypeSignature : this.superinterfaceSignatures) {
            classRefTypeSignature.setScanResult(scanResult);
         }
      }

   }

   protected void findReferencedClassNames(Set<String> refdClassNames) {
      for(TypeParameter typeParameter : this.typeParameters) {
         typeParameter.findReferencedClassNames(refdClassNames);
      }

      if (this.superclassSignature != null) {
         this.superclassSignature.findReferencedClassNames(refdClassNames);
      }

      for(ClassRefTypeSignature typeSignature : this.superinterfaceSignatures) {
         typeSignature.findReferencedClassNames(refdClassNames);
      }

   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo) {
      Set<String> refdClassNames = new HashSet();
      this.findReferencedClassNames(refdClassNames);

      for(String refdClassName : refdClassNames) {
         ClassInfo clsInfo = ClassInfo.getOrCreateClassInfo(refdClassName, classNameToClassInfo);
         clsInfo.scanResult = this.scanResult;
         refdClassInfo.add(clsInfo);
      }

   }

   public int hashCode() {
      return this.typeParameters.hashCode() + this.superclassSignature.hashCode() * 7 + this.superinterfaceSignatures.hashCode() * 15;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ClassTypeSignature)) {
         return false;
      } else {
         ClassTypeSignature o = (ClassTypeSignature)obj;
         return o.typeParameters.equals(this.typeParameters) && o.superclassSignature.equals(this.superclassSignature) && o.superinterfaceSignatures.equals(this.superinterfaceSignatures);
      }
   }

   String toString(String className, boolean typeNameOnly, int modifiers, boolean isAnnotation, boolean isInterface) {
      StringBuilder buf = new StringBuilder();
      if (!typeNameOnly) {
         if (modifiers != 0) {
            TypeUtils.modifiersToString(modifiers, TypeUtils.ModifierType.CLASS, false, buf);
         }

         if (buf.length() > 0) {
            buf.append(' ');
         }

         buf.append(isAnnotation ? "@interface" : (isInterface ? "interface" : ((modifiers & 16384) != 0 ? "enum" : "class")));
         buf.append(' ');
      }

      if (className != null) {
         buf.append(className);
      }

      if (!this.typeParameters.isEmpty()) {
         Join.join(buf, "<", ", ", ">", this.typeParameters);
      }

      if (!typeNameOnly) {
         if (this.superclassSignature != null) {
            String superSig = this.superclassSignature.toString();
            if (!superSig.equals("java.lang.Object")) {
               buf.append(" extends ");
               buf.append(superSig);
            }
         }

         if (!this.superinterfaceSignatures.isEmpty()) {
            buf.append(isInterface ? " extends " : " implements ");
            Join.join(buf, "", ", ", "", this.superinterfaceSignatures);
         }
      }

      return buf.toString();
   }

   public String toString() {
      return this.toString(this.classInfo.getName(), false, this.classInfo.getModifiers(), this.classInfo.isAnnotation(), this.classInfo.isInterface());
   }
}
