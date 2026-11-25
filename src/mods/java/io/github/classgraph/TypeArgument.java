package io.github.classgraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;

public final class TypeArgument extends HierarchicalTypeSignature {
   private final TypeArgument.Wildcard wildcard;
   private final ReferenceTypeSignature typeSignature;

   private TypeArgument(TypeArgument.Wildcard wildcard, ReferenceTypeSignature typeSignature) {
      this.wildcard = wildcard;
      this.typeSignature = typeSignature;
   }

   public TypeArgument.Wildcard getWildcard() {
      return this.wildcard;
   }

   public ReferenceTypeSignature getTypeSignature() {
      return this.typeSignature;
   }

   private static TypeArgument parse(Parser parser, String definingClassName) throws ParseException {
      char peek = parser.peek();
      if (peek == '*') {
         parser.expect('*');
         return new TypeArgument(TypeArgument.Wildcard.ANY, (ReferenceTypeSignature)null);
      } else if (peek == '+') {
         parser.expect('+');
         ReferenceTypeSignature typeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClassName);
         if (typeSignature == null) {
            throw new ParseException(parser, "Missing '+' type bound");
         } else {
            return new TypeArgument(TypeArgument.Wildcard.EXTENDS, typeSignature);
         }
      } else if (peek == '-') {
         parser.expect('-');
         ReferenceTypeSignature typeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClassName);
         if (typeSignature == null) {
            throw new ParseException(parser, "Missing '-' type bound");
         } else {
            return new TypeArgument(TypeArgument.Wildcard.SUPER, typeSignature);
         }
      } else {
         ReferenceTypeSignature typeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClassName);
         if (typeSignature == null) {
            throw new ParseException(parser, "Missing type bound");
         } else {
            return new TypeArgument(TypeArgument.Wildcard.NONE, typeSignature);
         }
      }
   }

   static List<TypeArgument> parseList(Parser parser, String definingClassName) throws ParseException {
      if (parser.peek() == '<') {
         parser.expect('<');
         List<TypeArgument> typeArguments = new ArrayList(2);

         while(parser.peek() != '>') {
            if (!parser.hasMore()) {
               throw new ParseException(parser, "Missing '>'");
            }

            typeArguments.add(parse(parser, definingClassName));
         }

         parser.expect('>');
         return typeArguments;
      } else {
         return Collections.emptyList();
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
      if (this.typeSignature != null) {
         this.typeSignature.setScanResult(scanResult);
      }

   }

   public void findReferencedClassNames(Set<String> refdClassNames) {
      if (this.typeSignature != null) {
         this.typeSignature.findReferencedClassNames(refdClassNames);
      }

   }

   public int hashCode() {
      return this.typeSignature.hashCode() + 7 * this.wildcard.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof TypeArgument)) {
         return false;
      } else {
         TypeArgument o = (TypeArgument)obj;
         return o.typeSignature.equals(this.typeSignature) && o.wildcard.equals(this.wildcard);
      }
   }

   private String toStringInternal(boolean useSimpleNames) {
      switch(this.wildcard) {
      case ANY:
         return "?";
      case EXTENDS:
         String typeSigStr = this.typeSignature.toString();
         return typeSigStr.equals("java.lang.Object") ? "?" : "? extends " + typeSigStr;
      case SUPER:
         return "? super " + this.typeSignature.toString();
      case NONE:
         return useSimpleNames ? this.typeSignature.toStringWithSimpleNames() : this.typeSignature.toString();
      default:
         throw ClassGraphException.newClassGraphException("Unknown wildcard type " + this.wildcard);
      }
   }

   public String toStringWithSimpleNames() {
      return this.toStringInternal(true);
   }

   public String toString() {
      return this.toStringInternal(false);
   }

   public static enum Wildcard {
      NONE,
      ANY,
      EXTENDS,
      SUPER;

      private Wildcard() {
      }
   }
}
