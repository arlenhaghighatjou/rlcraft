package stanhebben.zenscript.definitions.zenclasses;

import org.objectweb.asm.ClassWriter;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.EnvironmentScript;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.ZenNativeMember;
import stanhebben.zenscript.util.MethodOutput;

public class ParsedZenClassField {
   public final boolean isStatic;
   public final ParsedExpression initializer;
   public final String name;
   private final String ownerName;
   public ZenType type;

   public ParsedZenClassField(boolean isStatic, ZenType type, ParsedExpression initializer, String name, String ownerName) {
      this.isStatic = isStatic;
      this.type = type;
      this.initializer = initializer;
      this.name = name;
      this.ownerName = ownerName;
   }

   public static ParsedZenClassField parse(ZenTokener parser, EnvironmentScript classEnvironment, boolean isStatic, String ownerName) {
      String name = parser.required(1, "Identifier expected").getValue();
      ZenType type = ZenType.ANY;
      if (parser.optional(120) != null) {
         type = ZenType.read(parser, classEnvironment);
      }

      ParsedExpression initializer = null;
      if (parser.optional(39) != null) {
         initializer = ParsedExpression.read(parser, classEnvironment);
      }

      parser.required(33, "; expected");
      return new ParsedZenClassField(isStatic, type, initializer, name, ownerName);
   }

   void addMethodsToMember(ZenNativeMember zenNativeMember) {
      zenNativeMember.setGetter(new ParsedZenClassField.ZenClassFieldMethod(false));
      zenNativeMember.setSetter(new ParsedZenClassField.ZenClassFieldMethod(true));
   }

   public boolean hasInitializer() {
      return this.initializer != null;
   }

   public void visit(ClassWriter newClass) {
      newClass.visitField(!this.isStatic ? 1 : 9, this.name, this.type.toASMType().getDescriptor(), (String)null, (Object)null).visitEnd();
   }

   public void writeAll(IEnvironmentMethod clinitEnvironment, ClassWriter newClass, MethodOutput clinit, String className) {
      Expression expression = this.hasInitializer() ? this.initializer.compile(clinitEnvironment, this.type).eval(clinitEnvironment) : null;
      if (this.type == ZenType.ANY && expression != null) {
         this.type = expression.getType();
      }

      this.visit(newClass);
      if (expression != null) {
         expression.compile(true, clinitEnvironment);
         clinit.putStaticField(className, this.name, this.type.toASMType().getDescriptor());
      }

   }

   private final class ZenClassFieldMethod implements IJavaMethod {
      private final boolean isSetter;

      private ZenClassFieldMethod(boolean isSetter) {
         this.isSetter = isSetter;
      }

      public boolean isStatic() {
         return ParsedZenClassField.this.isStatic;
      }

      public boolean accepts(int numArguments) {
         return numArguments == (this.isSetter ? 1 : 0);
      }

      public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
         return this.accepts(arguments.length) && (!this.isSetter || arguments[0].getType().canCastImplicit(ParsedZenClassField.this.type, environment));
      }

      public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
         return this.accepts(environment, arguments) ? 1 : -1;
      }

      public void invokeVirtual(MethodOutput output) {
         if (this.isStatic()) {
            if (this.isSetter) {
               output.putStaticField(ParsedZenClassField.this.ownerName, ParsedZenClassField.this.name, ParsedZenClassField.this.type.getSignature());
            } else {
               output.getStaticField(ParsedZenClassField.this.ownerName, ParsedZenClassField.this.name, ParsedZenClassField.this.type.getSignature());
            }
         } else if (this.isSetter) {
            output.putField(ParsedZenClassField.this.ownerName, ParsedZenClassField.this.name, ParsedZenClassField.this.type.getSignature());
         } else {
            output.getField(ParsedZenClassField.this.ownerName, ParsedZenClassField.this.name, ParsedZenClassField.this.type.getSignature());
         }

      }

      public void invokeStatic(MethodOutput output) {
         if (!this.isStatic()) {
            throw new IllegalArgumentException("Cannot invoke nonstatic method from a static context");
         } else {
            output.getStaticField(ParsedZenClassField.this.ownerName, ParsedZenClassField.this.name, ParsedZenClassField.this.type.getSignature());
         }
      }

      public ZenType[] getParameterTypes() {
         return this.isSetter ? new ZenType[]{ParsedZenClassField.this.type} : new ZenType[0];
      }

      public ZenType getReturnType() {
         return (ZenType)(this.isSetter ? ZenType.VOID : ParsedZenClassField.this.type);
      }

      public boolean isVarargs() {
         return false;
      }

      public String getErrorDescription() {
         return "INTERNAL METHOD";
      }
   }
}
