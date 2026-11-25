package io.github.classgraph;

import java.lang.reflect.Array;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;

public class ArrayTypeSignature extends ReferenceTypeSignature {
   private final TypeSignature elementTypeSignature;
   private final int numDims;
   private final String typeSignatureStr;
   private String className;
   private ArrayClassInfo arrayClassInfo;
   private Class<?> elementClassRef;

   ArrayTypeSignature(TypeSignature elementTypeSignature, int numDims, String typeSignatureStr) {
      this.elementTypeSignature = elementTypeSignature;
      this.numDims = numDims;
      this.typeSignatureStr = typeSignatureStr;
   }

   ArrayTypeSignature(String eltClassName, int numDims) {
      BaseTypeSignature baseTypeSignature = BaseTypeSignature.getTypeSignature(eltClassName);
      String eltTypeSigStr;
      if (baseTypeSignature != null) {
         eltTypeSigStr = baseTypeSignature.getTypeSignatureChar();
         this.elementTypeSignature = baseTypeSignature;
      } else {
         eltTypeSigStr = "L" + eltClassName.replace('.', '/') + ";";

         try {
            this.elementTypeSignature = ClassRefTypeSignature.parse(new Parser(eltTypeSigStr), (String)null);
            if (this.elementTypeSignature == null) {
               throw new IllegalArgumentException("Could not form array base type signature for class " + eltClassName);
            }
         } catch (ParseException var7) {
            throw new IllegalArgumentException("Could not form array base type signature for class " + eltClassName);
         }
      }

      StringBuilder buf = new StringBuilder(numDims + eltTypeSigStr.length());

      for(int i = 0; i < numDims; ++i) {
         buf.append('[');
      }

      buf.append(eltTypeSigStr);
      this.typeSignatureStr = buf.toString();
      this.numDims = numDims;
   }

   public String getTypeSignatureStr() {
      return this.typeSignatureStr;
   }

   public TypeSignature getElementTypeSignature() {
      return this.elementTypeSignature;
   }

   public int getNumDimensions() {
      return this.numDims;
   }

   protected String getClassName() {
      if (this.className == null) {
         this.className = this.toStringInternal(false);
      }

      return this.className;
   }

   protected ClassInfo getClassInfo() {
      return this.getArrayClassInfo();
   }

   public ArrayClassInfo getArrayClassInfo() {
      if (this.arrayClassInfo == null) {
         if (this.scanResult != null) {
            String clsName = this.getClassName();
            this.arrayClassInfo = (ArrayClassInfo)this.scanResult.classNameToClassInfo.get(clsName);
            if (this.arrayClassInfo == null) {
               this.scanResult.classNameToClassInfo.put(clsName, this.arrayClassInfo = new ArrayClassInfo(this));
               this.arrayClassInfo.setScanResult(this.scanResult);
            }
         } else {
            this.arrayClassInfo = new ArrayClassInfo(this);
         }
      }

      return this.arrayClassInfo;
   }

   void setScanResult(ScanResult scanResult) {
      super.setScanResult(scanResult);
      if (this.elementTypeSignature != null) {
         this.elementTypeSignature.setScanResult(scanResult);
      }

      if (this.arrayClassInfo != null) {
         this.arrayClassInfo.setScanResult(scanResult);
      }

   }

   protected void findReferencedClassNames(Set<String> refdClassNames) {
      this.elementTypeSignature.findReferencedClassNames(refdClassNames);
   }

   public Class<?> loadElementClass(boolean ignoreExceptions) {
      if (this.elementClassRef == null) {
         if (this.elementTypeSignature instanceof BaseTypeSignature) {
            this.elementClassRef = ((BaseTypeSignature)this.elementTypeSignature).getType();
         } else if (this.scanResult != null) {
            this.elementClassRef = this.elementTypeSignature.loadClass(ignoreExceptions);
         } else {
            String elementTypeName = ((ClassRefTypeSignature)this.elementTypeSignature).getFullyQualifiedClassName();

            try {
               this.elementClassRef = Class.forName(elementTypeName);
            } catch (Throwable var4) {
               if (!ignoreExceptions) {
                  throw new IllegalArgumentException("Could not load array element class " + elementTypeName, var4);
               }
            }
         }
      }

      return this.elementClassRef;
   }

   public Class<?> loadElementClass() {
      return this.loadElementClass(false);
   }

   public Class<?> loadClass(boolean ignoreExceptions) {
      if (this.classRef == null) {
         Class<?> eltClassRef = null;
         if (ignoreExceptions) {
            try {
               eltClassRef = this.loadElementClass();
            } catch (IllegalArgumentException var4) {
               return null;
            }
         } else {
            eltClassRef = this.loadElementClass();
         }

         if (eltClassRef == null) {
            throw new IllegalArgumentException("Could not load array element class " + this.elementTypeSignature);
         }

         Object eltArrayInstance = Array.newInstance(eltClassRef, new int[this.numDims]);
         this.classRef = eltArrayInstance.getClass();
      }

      return this.classRef;
   }

   public Class<?> loadClass() {
      return this.loadClass(false);
   }

   public int hashCode() {
      return this.elementTypeSignature.hashCode() + this.numDims * 15;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ArrayTypeSignature)) {
         return false;
      } else {
         ArrayTypeSignature other = (ArrayTypeSignature)obj;
         return other.elementTypeSignature.equals(this.elementTypeSignature) && other.numDims == this.numDims;
      }
   }

   public boolean equalsIgnoringTypeParams(TypeSignature other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof ArrayTypeSignature)) {
         return false;
      } else {
         ArrayTypeSignature o = (ArrayTypeSignature)other;
         return o.elementTypeSignature.equalsIgnoringTypeParams(this.elementTypeSignature) && o.numDims == this.numDims;
      }
   }

   protected String toStringInternal(boolean useSimpleNames) {
      StringBuilder buf = new StringBuilder();
      buf.append(useSimpleNames ? this.elementTypeSignature.toStringWithSimpleNames() : this.elementTypeSignature.toString());

      for(int i = 0; i < this.numDims; ++i) {
         buf.append("[]");
      }

      return buf.toString();
   }

   static ArrayTypeSignature parse(Parser parser, String definingClassName) throws ParseException {
      int numArrayDims = 0;
      int begin = parser.getPosition();

      while(parser.peek() == '[') {
         ++numArrayDims;
         parser.next();
      }

      if (numArrayDims > 0) {
         TypeSignature elementTypeSignature = TypeSignature.parse(parser, definingClassName);
         if (elementTypeSignature == null) {
            throw new ParseException(parser, "elementTypeSignature == null");
         } else {
            String typeSignatureStr = parser.getSubsequence(begin, parser.getPosition()).toString();
            return new ArrayTypeSignature(elementTypeSignature, numArrayDims, typeSignatureStr);
         }
      } else {
         return null;
      }
   }
}
