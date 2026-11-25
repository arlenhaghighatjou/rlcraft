package io.github.classgraph;

import java.util.Set;
import nonapi.io.github.classgraph.types.Parser;

public class BaseTypeSignature extends TypeSignature {
   private final String baseType;
   private final String typeSignatureChar;
   private static final BaseTypeSignature BYTE = new BaseTypeSignature("byte", 'B');
   private static final BaseTypeSignature CHAR = new BaseTypeSignature("char", 'C');
   private static final BaseTypeSignature DOUBLE = new BaseTypeSignature("double", 'D');
   private static final BaseTypeSignature FLOAT = new BaseTypeSignature("float", 'F');
   private static final BaseTypeSignature INT = new BaseTypeSignature("int", 'I');
   private static final BaseTypeSignature LONG = new BaseTypeSignature("long", 'J');
   private static final BaseTypeSignature SHORT = new BaseTypeSignature("short", 'S');
   private static final BaseTypeSignature BOOLEAN = new BaseTypeSignature("boolean", 'Z');
   static final BaseTypeSignature VOID = new BaseTypeSignature("void", 'V');

   private BaseTypeSignature(String baseType, char typeSignatureChar) {
      this.baseType = baseType;
      this.typeSignatureChar = Character.toString(typeSignatureChar);
   }

   public String getTypeStr() {
      return this.baseType;
   }

   public String getTypeSignatureChar() {
      return this.typeSignatureChar;
   }

   public Class<?> getType() {
      String var1 = this.baseType;
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -1325958191:
         if (var1.equals("double")) {
            var2 = 2;
         }
         break;
      case 104431:
         if (var1.equals("int")) {
            var2 = 4;
         }
         break;
      case 3039496:
         if (var1.equals("byte")) {
            var2 = 0;
         }
         break;
      case 3052374:
         if (var1.equals("char")) {
            var2 = 1;
         }
         break;
      case 3327612:
         if (var1.equals("long")) {
            var2 = 5;
         }
         break;
      case 3625364:
         if (var1.equals("void")) {
            var2 = 8;
         }
         break;
      case 64711720:
         if (var1.equals("boolean")) {
            var2 = 7;
         }
         break;
      case 97526364:
         if (var1.equals("float")) {
            var2 = 3;
         }
         break;
      case 109413500:
         if (var1.equals("short")) {
            var2 = 6;
         }
      }

      switch(var2) {
      case 0:
         return Byte.TYPE;
      case 1:
         return Character.TYPE;
      case 2:
         return Double.TYPE;
      case 3:
         return Float.TYPE;
      case 4:
         return Integer.TYPE;
      case 5:
         return Long.TYPE;
      case 6:
         return Short.TYPE;
      case 7:
         return Boolean.TYPE;
      case 8:
         return Void.TYPE;
      default:
         throw new IllegalArgumentException("Unknown base type " + this.baseType);
      }
   }

   public static BaseTypeSignature getTypeSignature(String typeName) {
      byte var2 = -1;
      switch(typeName.hashCode()) {
      case -1325958191:
         if (typeName.equals("double")) {
            var2 = 2;
         }
         break;
      case 104431:
         if (typeName.equals("int")) {
            var2 = 4;
         }
         break;
      case 3039496:
         if (typeName.equals("byte")) {
            var2 = 0;
         }
         break;
      case 3052374:
         if (typeName.equals("char")) {
            var2 = 1;
         }
         break;
      case 3327612:
         if (typeName.equals("long")) {
            var2 = 5;
         }
         break;
      case 3625364:
         if (typeName.equals("void")) {
            var2 = 8;
         }
         break;
      case 64711720:
         if (typeName.equals("boolean")) {
            var2 = 7;
         }
         break;
      case 97526364:
         if (typeName.equals("float")) {
            var2 = 3;
         }
         break;
      case 109413500:
         if (typeName.equals("short")) {
            var2 = 6;
         }
      }

      switch(var2) {
      case 0:
         return BYTE;
      case 1:
         return CHAR;
      case 2:
         return DOUBLE;
      case 3:
         return FLOAT;
      case 4:
         return INT;
      case 5:
         return LONG;
      case 6:
         return SHORT;
      case 7:
         return BOOLEAN;
      case 8:
         return VOID;
      default:
         return null;
      }
   }

   Class<?> loadClass() {
      return this.getType();
   }

   <T> Class<T> loadClass(Class<T> superclassOrInterfaceType) {
      Class<?> type = this.getType();
      if (!superclassOrInterfaceType.isAssignableFrom(type)) {
         throw new IllegalArgumentException("Primitive class " + this.baseType + " cannot be cast to " + superclassOrInterfaceType.getName());
      } else {
         return type;
      }
   }

   static BaseTypeSignature parse(Parser parser) {
      switch(parser.peek()) {
      case 'B':
         parser.next();
         return BYTE;
      case 'C':
         parser.next();
         return CHAR;
      case 'D':
         parser.next();
         return DOUBLE;
      case 'E':
      case 'G':
      case 'H':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'T':
      case 'U':
      case 'W':
      case 'X':
      case 'Y':
      default:
         return null;
      case 'F':
         parser.next();
         return FLOAT;
      case 'I':
         parser.next();
         return INT;
      case 'J':
         parser.next();
         return LONG;
      case 'S':
         parser.next();
         return SHORT;
      case 'V':
         parser.next();
         return VOID;
      case 'Z':
         parser.next();
         return BOOLEAN;
      }
   }

   protected String getClassName() {
      return this.baseType;
   }

   protected ClassInfo getClassInfo() {
      return null;
   }

   protected void findReferencedClassNames(Set<String> refdClassNames) {
   }

   public int hashCode() {
      return this.baseType.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof BaseTypeSignature) ? false : ((BaseTypeSignature)obj).baseType.equals(this.baseType);
      }
   }

   public boolean equalsIgnoringTypeParams(TypeSignature other) {
      return !(other instanceof BaseTypeSignature) ? false : this.baseType.equals(((BaseTypeSignature)other).baseType);
   }

   protected String toStringInternal(boolean useSimpleNames) {
      return this.baseType;
   }
}
