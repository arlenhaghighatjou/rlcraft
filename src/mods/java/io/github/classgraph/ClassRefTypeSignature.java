package io.github.classgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;
import nonapi.io.github.classgraph.types.TypeUtils;

public final class ClassRefTypeSignature extends ClassRefOrTypeVariableSignature {
   final String className;
   private String fullyQualifiedClassName;
   private final List<TypeArgument> typeArguments;
   private final List<String> suffixes;
   private final List<List<TypeArgument>> suffixTypeArguments;

   private ClassRefTypeSignature(String className, List<TypeArgument> typeArguments, List<String> suffixes, List<List<TypeArgument>> suffixTypeArguments) {
      this.className = className;
      this.typeArguments = typeArguments;
      this.suffixes = suffixes;
      this.suffixTypeArguments = suffixTypeArguments;
   }

   public String getBaseClassName() {
      return this.className;
   }

   public String getFullyQualifiedClassName() {
      if (this.fullyQualifiedClassName == null) {
         StringBuilder buf = new StringBuilder();
         buf.append(this.className);

         for(String suffix : this.suffixes) {
            buf.append('$');
            buf.append(suffix);
         }

         this.fullyQualifiedClassName = buf.toString();
      }

      return this.fullyQualifiedClassName;
   }

   public List<TypeArgument> getTypeArguments() {
      return this.typeArguments;
   }

   public List<String> getSuffixes() {
      return this.suffixes;
   }

   public List<List<TypeArgument>> getSuffixTypeArguments() {
      return this.suffixTypeArguments;
   }

   public Class<?> loadClass(boolean ignoreExceptions) {
      return super.loadClass(ignoreExceptions);
   }

   public Class<?> loadClass() {
      return super.loadClass();
   }

   protected String getClassName() {
      return this.getFullyQualifiedClassName();
   }

   public ClassInfo getClassInfo() {
      return super.getClassInfo();
   }

   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.typeArguments != null) {
         for(TypeArgument typeArgument : this.typeArguments) {
            typeArgument.setScanResult(scanResult);
         }
      }

      if (this.suffixTypeArguments != null) {
         Iterator var6 = this.suffixTypeArguments.iterator();

         while(var6.hasNext()) {
            for(TypeArgument typeArgument : (List)var6.next()) {
               typeArgument.setScanResult(scanResult);
            }
         }
      }

   }

   protected void findReferencedClassNames(Set<String> refdClassNames) {
      refdClassNames.add(this.getFullyQualifiedClassName());

      for(TypeArgument typeArgument : this.typeArguments) {
         typeArgument.findReferencedClassNames(refdClassNames);
      }

   }

   public int hashCode() {
      return this.className.hashCode() + 7 * this.typeArguments.hashCode() + 15 * this.suffixes.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ClassRefTypeSignature)) {
         return false;
      } else {
         ClassRefTypeSignature o = (ClassRefTypeSignature)obj;
         return o.className.equals(this.className) && o.typeArguments.equals(this.typeArguments) && o.suffixes.equals(this.suffixes);
      }
   }

   public boolean equalsIgnoringTypeParams(TypeSignature other) {
      if (other instanceof TypeVariableSignature) {
         return other.equalsIgnoringTypeParams(this);
      } else if (!(other instanceof ClassRefTypeSignature)) {
         return false;
      } else {
         ClassRefTypeSignature o = (ClassRefTypeSignature)other;
         return o.suffixes.equals(this.suffixes) ? o.className.equals(this.className) : o.getFullyQualifiedClassName().equals(this.getFullyQualifiedClassName());
      }
   }

   protected String toStringInternal(boolean useSimpleNames) {
      StringBuilder buf = new StringBuilder();
      if (!useSimpleNames || this.suffixes.isEmpty()) {
         buf.append(useSimpleNames ? ClassInfo.getSimpleName(this.className) : this.className);
         if (!this.typeArguments.isEmpty()) {
            buf.append('<');

            for(int i = 0; i < this.typeArguments.size(); ++i) {
               if (i > 0) {
                  buf.append(", ");
               }

               buf.append(useSimpleNames ? ((TypeArgument)this.typeArguments.get(i)).toStringWithSimpleNames() : ((TypeArgument)this.typeArguments.get(i)).toString());
            }

            buf.append('>');
         }
      }

      for(int i = useSimpleNames && !this.suffixes.isEmpty() ? this.suffixes.size() - 1 : 0; i < this.suffixes.size(); ++i) {
         if (!useSimpleNames) {
            buf.append('.');
         }

         buf.append((String)this.suffixes.get(i));
         List<TypeArgument> suffixTypeArgs = (List)this.suffixTypeArguments.get(i);
         if (!suffixTypeArgs.isEmpty()) {
            buf.append('<');

            for(int j = 0; j < suffixTypeArgs.size(); ++j) {
               if (j > 0) {
                  buf.append(", ");
               }

               buf.append(useSimpleNames ? ((TypeArgument)suffixTypeArgs.get(j)).toStringWithSimpleNames() : ((TypeArgument)suffixTypeArgs.get(j)).toString());
            }

            buf.append('>');
         }
      }

      return buf.toString();
   }

   static ClassRefTypeSignature parse(Parser parser, String definingClassName) throws ParseException {
      if (parser.peek() == 'L') {
         parser.next();
         if (!TypeUtils.getIdentifierToken(parser, '/', '.')) {
            throw new ParseException(parser, "Could not parse identifier token");
         } else {
            String className = parser.currToken();
            List<TypeArgument> typeArguments = TypeArgument.parseList(parser, definingClassName);
            List<String> suffixes;
            List<List<TypeArgument>> suffixTypeArguments;
            if (parser.peek() == '.') {
               suffixes = new ArrayList();
               suffixTypeArguments = new ArrayList();

               while(parser.peek() == '.') {
                  parser.expect('.');
                  if (!TypeUtils.getIdentifierToken(parser, '/', '.')) {
                     throw new ParseException(parser, "Could not parse identifier token");
                  }

                  suffixes.add(parser.currToken());
                  suffixTypeArguments.add(TypeArgument.parseList(parser, definingClassName));
               }
            } else {
               suffixes = Collections.emptyList();
               suffixTypeArguments = Collections.emptyList();
            }

            parser.expect(';');
            return new ClassRefTypeSignature(className, typeArguments, suffixes, suffixTypeArguments);
         }
      } else {
         return null;
      }
   }
}
