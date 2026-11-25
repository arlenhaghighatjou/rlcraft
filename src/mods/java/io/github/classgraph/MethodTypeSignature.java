package io.github.classgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;

public final class MethodTypeSignature extends HierarchicalTypeSignature {
   final List<TypeParameter> typeParameters;
   private final List<TypeSignature> parameterTypeSignatures;
   private final TypeSignature resultType;
   private final List<ClassRefOrTypeVariableSignature> throwsSignatures;

   private MethodTypeSignature(List<TypeParameter> typeParameters, List<TypeSignature> paramTypes, TypeSignature resultType, List<ClassRefOrTypeVariableSignature> throwsSignatures) {
      this.typeParameters = typeParameters;
      this.parameterTypeSignatures = paramTypes;
      this.resultType = resultType;
      this.throwsSignatures = throwsSignatures;
   }

   List<TypeParameter> getTypeParameters() {
      return this.typeParameters;
   }

   List<TypeSignature> getParameterTypeSignatures() {
      return this.parameterTypeSignatures;
   }

   public TypeSignature getResultType() {
      return this.resultType;
   }

   public List<ClassRefOrTypeVariableSignature> getThrowsSignatures() {
      return this.throwsSignatures;
   }

   static MethodTypeSignature parse(String typeDescriptor, String definingClassName) throws ParseException {
      if (typeDescriptor.equals("<init>")) {
         return new MethodTypeSignature(Collections.emptyList(), Collections.emptyList(), BaseTypeSignature.VOID, Collections.emptyList());
      } else {
         Parser parser = new Parser(typeDescriptor);
         List<TypeParameter> typeParameters = TypeParameter.parseList(parser, definingClassName);
         parser.expect('(');
         List<TypeSignature> paramTypes = new ArrayList();

         while(parser.peek() != ')') {
            if (!parser.hasMore()) {
               throw new ParseException(parser, "Ran out of input while parsing method signature");
            }

            TypeSignature paramType = TypeSignature.parse(parser, definingClassName);
            if (paramType == null) {
               throw new ParseException(parser, "Missing method parameter type signature");
            }

            paramTypes.add(paramType);
         }

         parser.expect(')');
         TypeSignature resultType = TypeSignature.parse(parser, definingClassName);
         if (resultType == null) {
            throw new ParseException(parser, "Missing method result type signature");
         } else {
            List<ClassRefOrTypeVariableSignature> throwsSignatures;
            if (parser.peek() == '^') {
               throwsSignatures = new ArrayList();

               while(parser.peek() == '^') {
                  parser.expect('^');
                  ClassRefTypeSignature classTypeSignature = ClassRefTypeSignature.parse(parser, definingClassName);
                  if (classTypeSignature != null) {
                     throwsSignatures.add(classTypeSignature);
                  } else {
                     TypeVariableSignature typeVariableSignature = TypeVariableSignature.parse(parser, definingClassName);
                     if (typeVariableSignature == null) {
                        throw new ParseException(parser, "Missing type variable signature");
                     }

                     throwsSignatures.add(typeVariableSignature);
                  }
               }
            } else {
               throwsSignatures = Collections.emptyList();
            }

            if (parser.hasMore()) {
               throw new ParseException(parser, "Extra characters at end of type descriptor");
            } else {
               MethodTypeSignature methodSignature = new MethodTypeSignature(typeParameters, paramTypes, resultType, throwsSignatures);
               List<TypeVariableSignature> typeVariableSignatures = (List)parser.getState();
               if (typeVariableSignatures != null) {
                  for(TypeVariableSignature typeVariableSignature : typeVariableSignatures) {
                     typeVariableSignature.containingMethodSignature = methodSignature;
                  }
               }

               return methodSignature;
            }
         }
      }
   }

   protected String getClassName() {
      throw new IllegalArgumentException("getClassName() cannot be called here");
   }

   protected ClassInfo getClassInfo() {
      throw new IllegalArgumentException("getClassInfo() cannot be called here");
   }

   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.typeParameters != null) {
         for(TypeParameter typeParameter : this.typeParameters) {
            typeParameter.setScanResult(scanResult);
         }
      }

      if (this.parameterTypeSignatures != null) {
         for(TypeSignature typeParameter : this.parameterTypeSignatures) {
            typeParameter.setScanResult(scanResult);
         }
      }

      if (this.resultType != null) {
         this.resultType.setScanResult(scanResult);
      }

      if (this.throwsSignatures != null) {
         for(ClassRefOrTypeVariableSignature throwsSignature : this.throwsSignatures) {
            throwsSignature.setScanResult(scanResult);
         }
      }

   }

   protected void findReferencedClassNames(Set<String> refdClassNames) {
      for(TypeParameter typeParameter : this.typeParameters) {
         if (typeParameter != null) {
            typeParameter.findReferencedClassNames(refdClassNames);
         }
      }

      for(TypeSignature typeSignature : this.parameterTypeSignatures) {
         if (typeSignature != null) {
            typeSignature.findReferencedClassNames(refdClassNames);
         }
      }

      this.resultType.findReferencedClassNames(refdClassNames);

      for(ClassRefOrTypeVariableSignature typeSignature : this.throwsSignatures) {
         if (typeSignature != null) {
            typeSignature.findReferencedClassNames(refdClassNames);
         }
      }

   }

   protected void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo) {
      Set<String> refdClassNames = new HashSet();
      this.findReferencedClassNames(refdClassNames);

      for(String refdClassName : refdClassNames) {
         ClassInfo classInfo = ClassInfo.getOrCreateClassInfo(refdClassName, classNameToClassInfo);
         classInfo.scanResult = this.scanResult;
         refdClassInfo.add(classInfo);
      }

   }

   public int hashCode() {
      return this.typeParameters.hashCode() + this.parameterTypeSignatures.hashCode() * 7 + this.resultType.hashCode() * 15 + this.throwsSignatures.hashCode() * 31;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof MethodTypeSignature)) {
         return false;
      } else {
         MethodTypeSignature o = (MethodTypeSignature)obj;
         return o.typeParameters.equals(this.typeParameters) && o.parameterTypeSignatures.equals(this.parameterTypeSignatures) && o.resultType.equals(this.resultType) && o.throwsSignatures.equals(this.throwsSignatures);
      }
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      if (!this.typeParameters.isEmpty()) {
         buf.append('<');

         for(int i = 0; i < this.typeParameters.size(); ++i) {
            if (i > 0) {
               buf.append(", ");
            }

            String typeParamStr = ((TypeParameter)this.typeParameters.get(i)).toString();
            buf.append(typeParamStr);
         }

         buf.append('>');
      }

      if (buf.length() > 0) {
         buf.append(' ');
      }

      buf.append(this.resultType.toString());
      buf.append(" (");

      for(int i = 0; i < this.parameterTypeSignatures.size(); ++i) {
         if (i > 0) {
            buf.append(", ");
         }

         buf.append(((TypeSignature)this.parameterTypeSignatures.get(i)).toString());
      }

      buf.append(')');
      if (!this.throwsSignatures.isEmpty()) {
         buf.append(" throws ");

         for(int i = 0; i < this.throwsSignatures.size(); ++i) {
            if (i > 0) {
               buf.append(", ");
            }

            buf.append(((ClassRefOrTypeVariableSignature)this.throwsSignatures.get(i)).toString());
         }
      }

      return buf.toString();
   }
}
