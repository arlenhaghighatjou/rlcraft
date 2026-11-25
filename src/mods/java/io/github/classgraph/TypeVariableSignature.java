package io.github.classgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;
import nonapi.io.github.classgraph.types.TypeUtils;

public final class TypeVariableSignature extends ClassRefOrTypeVariableSignature {
   private final String name;
   private final String definingClassName;
   MethodTypeSignature containingMethodSignature;

   private TypeVariableSignature(String typeVariableName, String definingClassName) {
      this.name = typeVariableName;
      this.definingClassName = definingClassName;
   }

   public String getName() {
      return this.name;
   }

   public TypeParameter resolve() {
      if (this.containingMethodSignature != null && this.containingMethodSignature.typeParameters != null && !this.containingMethodSignature.typeParameters.isEmpty()) {
         for(TypeParameter typeParameter : this.containingMethodSignature.typeParameters) {
            if (typeParameter.name.equals(this.name)) {
               return typeParameter;
            }
         }
      }

      ClassInfo containingClassInfo = this.getClassInfo();
      if (containingClassInfo == null) {
         throw new IllegalArgumentException("Could not find ClassInfo object for " + this.definingClassName);
      } else {
         ClassTypeSignature containingClassSignature = containingClassInfo.getTypeSignature();
         if (containingClassSignature != null && containingClassSignature.typeParameters != null && !containingClassSignature.typeParameters.isEmpty()) {
            for(TypeParameter typeParameter : containingClassSignature.typeParameters) {
               if (typeParameter.name.equals(this.name)) {
                  return typeParameter;
               }
            }
         }

         throw new IllegalArgumentException("Could not resolve " + this.name + " against parameters of the defining method or enclosing class");
      }
   }

   static TypeVariableSignature parse(Parser parser, String definingClassName) throws ParseException {
      char peek = parser.peek();
      if (peek == 'T') {
         parser.next();
         if (!TypeUtils.getIdentifierToken(parser)) {
            throw new ParseException(parser, "Could not parse type variable signature");
         } else {
            parser.expect(';');
            TypeVariableSignature typeVariableSignature = new TypeVariableSignature(parser.currToken(), definingClassName);
            List<TypeVariableSignature> typeVariableSignatures = (List)parser.getState();
            if (typeVariableSignatures == null) {
               parser.setState(typeVariableSignatures = new ArrayList());
            }

            typeVariableSignatures.add(typeVariableSignature);
            return typeVariableSignature;
         }
      } else {
         return null;
      }
   }

   protected String getClassName() {
      return this.definingClassName;
   }

   protected void findReferencedClassNames(Set<String> refdClassNames) {
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof TypeVariableSignature)) {
         return false;
      } else {
         TypeVariableSignature o = (TypeVariableSignature)obj;
         return o.name.equals(this.name);
      }
   }

   public boolean equalsIgnoringTypeParams(TypeSignature other) {
      if (!(other instanceof ClassRefTypeSignature)) {
         return this.equals(other);
      } else if (((ClassRefTypeSignature)other).className.equals("java.lang.Object")) {
         return true;
      } else {
         TypeParameter typeParameter;
         try {
            typeParameter = this.resolve();
         } catch (IllegalArgumentException var5) {
            return true;
         }

         if (typeParameter.classBound != null || typeParameter.interfaceBounds != null && !typeParameter.interfaceBounds.isEmpty()) {
            if (typeParameter.classBound != null) {
               if (!(typeParameter.classBound instanceof ClassRefTypeSignature)) {
                  if (typeParameter.classBound instanceof TypeVariableSignature) {
                     return this.equalsIgnoringTypeParams(typeParameter.classBound);
                  }

                  return false;
               }

               if (typeParameter.classBound.equals(other)) {
                  return true;
               }
            }

            if (typeParameter.interfaceBounds != null) {
               for(ReferenceTypeSignature interfaceBound : typeParameter.interfaceBounds) {
                  if (!(interfaceBound instanceof ClassRefTypeSignature)) {
                     if (interfaceBound instanceof TypeVariableSignature) {
                        return this.equalsIgnoringTypeParams(interfaceBound);
                     }

                     return false;
                  }

                  if (interfaceBound.equals(other)) {
                     return true;
                  }
               }
            }

            return false;
         } else {
            return true;
         }
      }
   }

   public String toStringWithTypeBound() {
      try {
         return this.resolve().toString();
      } catch (IllegalArgumentException var2) {
         return this.name;
      }
   }

   protected String toStringInternal(boolean useSimpleNames) {
      return this.name;
   }
}
