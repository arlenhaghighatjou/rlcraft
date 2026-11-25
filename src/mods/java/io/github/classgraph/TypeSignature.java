package io.github.classgraph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;

public abstract class TypeSignature extends HierarchicalTypeSignature {
   protected TypeSignature() {
   }

   protected void findReferencedClassNames(Set<String> refdClassNames) {
      String className = this.getClassName();
      if (className != null && !className.isEmpty()) {
         refdClassNames.add(this.getClassName());
      }

   }

   protected final void findReferencedClassInfo(Map<String, ClassInfo> classNameToClassInfo, Set<ClassInfo> refdClassInfo) {
      Set<String> refdClassNames = new HashSet();
      this.findReferencedClassNames(refdClassNames);

      for(String refdClassName : refdClassNames) {
         ClassInfo classInfo = ClassInfo.getOrCreateClassInfo(refdClassName, classNameToClassInfo);
         classInfo.scanResult = this.scanResult;
         refdClassInfo.add(classInfo);
      }

   }

   public abstract boolean equalsIgnoringTypeParams(TypeSignature var1);

   protected abstract String toStringInternal(boolean var1);

   public String toStringWithSimpleNames() {
      return this.toStringInternal(true);
   }

   public String toString() {
      return this.toStringInternal(false);
   }

   static TypeSignature parse(Parser parser, String definingClass) throws ParseException {
      ReferenceTypeSignature referenceTypeSignature = ReferenceTypeSignature.parseReferenceTypeSignature(parser, definingClass);
      if (referenceTypeSignature != null) {
         return referenceTypeSignature;
      } else {
         BaseTypeSignature baseTypeSignature = BaseTypeSignature.parse(parser);
         return baseTypeSignature != null ? baseTypeSignature : null;
      }
   }

   static TypeSignature parse(String typeDescriptor, String definingClass) throws ParseException {
      Parser parser = new Parser(typeDescriptor);
      TypeSignature typeSignature = parse(parser, definingClass);
      if (typeSignature == null) {
         throw new ParseException(parser, "Could not parse type signature");
      } else if (parser.hasMore()) {
         throw new ParseException(parser, "Extra characters at end of type descriptor");
      } else {
         return typeSignature;
      }
   }
}
