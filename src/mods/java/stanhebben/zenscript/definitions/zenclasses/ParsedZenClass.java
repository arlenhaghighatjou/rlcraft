package stanhebben.zenscript.definitions.zenclasses;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassWriter;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.EnvironmentClass;
import stanhebben.zenscript.compiler.EnvironmentMethod;
import stanhebben.zenscript.compiler.EnvironmentScript;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.ZenClassWriter;
import stanhebben.zenscript.definitions.ParsedFunction;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionThis;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolType;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeZenClass;
import stanhebben.zenscript.type.natives.ZenNativeMember;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedZenClass {
   public final ZenPosition position;
   public final String name;
   public final String className;
   public final ZenTypeZenClass type;
   private final EnvironmentScript classEnvironment;
   private final List<ParsedClassConstructor> constructors = new LinkedList();
   private final List<ParsedZenClassField> statics = new LinkedList();
   private final List<ParsedZenClassField> nonStatics = new LinkedList();
   private final List<ParsedZenClassMethod> methods = new LinkedList();
   private final Map<String, ZenNativeMember> members = new LinkedHashMap();
   public Class thisClass;

   public ParsedZenClass(ZenPosition position, String name, String className, EnvironmentScript classEnvironment) {
      this.position = position;
      this.name = name;
      this.className = className;
      this.classEnvironment = classEnvironment;
      this.type = new ZenTypeZenClass(this);
   }

   public static ParsedZenClass parse(ZenTokener parser, IEnvironmentGlobal environmentGlobal) {
      EnvironmentScript classEnvironment = new EnvironmentScript(environmentGlobal);
      parser.next();
      Token id = parser.required(1, "ClassName required");
      parser.required(5, "{ expected");
      String name = id.getValue();
      ZenPosition position = id.getPosition();
      ParsedZenClass classTemplate = new ParsedZenClass(position, name, environmentGlobal.makeClassNameWithMiddleName(position.getFile().getClassName() + "_" + name + "_"), classEnvironment);
      classEnvironment.putValue(name, new SymbolType(classTemplate.type), classTemplate.position);

      Token keyword;
      while((keyword = parser.optional(new int[]{127, 126, 667, 987654321, 108})) != null) {
         int type = keyword.getType();
         switch(type) {
         case 108:
            classTemplate.addMethod(ParsedZenClassMethod.parse(parser, classEnvironment, classTemplate.className));
            break;
         case 126:
         case 127:
         case 667:
            classTemplate.addField(ParsedZenClassField.parse(parser, classEnvironment, type == 667, classTemplate.className));
            break;
         case 987654321:
            classTemplate.addConstructor(ParsedClassConstructor.parse(parser, classEnvironment));
         }
      }

      parser.required(6, "} expected");
      return classTemplate;
   }

   private void addMethod(ParsedZenClassMethod parsedMethod) {
      ParsedFunction method = parsedMethod.method;
      this.methods.add(parsedMethod);
      if (!this.members.containsKey(method.getName())) {
         this.members.put(method.getName(), new ZenNativeMember());
         this.classEnvironment.putValue(method.getName(), (position1) -> {
            return (new ExpressionThis(position1, this.type)).getMember(position1, this.classEnvironment, method.getName());
         }, this.position);
      }

      parsedMethod.addToMember((ZenNativeMember)this.members.get(method.getName()));

      for(int i = 0; i < method.getArguments().size(); ++i) {
         ParsedFunctionArgument argument = (ParsedFunctionArgument)method.getArguments().get(i);
         if (argument.getDefaultExpression() != null) {
            this.addField(new ParsedZenClassField(true, argument.getType(), argument.getDefaultExpression(), method.getDefaultParameterFieldName(i), this.className));
         }
      }

   }

   private void addConstructor(ParsedClassConstructor parsedClassConstructor) {
      this.constructors.add(parsedClassConstructor);
   }

   private void addField(ParsedZenClassField parsedZenClassField) {
      String fieldName = parsedZenClassField.name;
      if (!this.members.containsKey(fieldName)) {
         this.members.put(fieldName, new ZenNativeMember());
      }

      parsedZenClassField.addMethodsToMember((ZenNativeMember)this.members.get(fieldName));
      if (parsedZenClassField.isStatic) {
         this.statics.add(parsedZenClassField);
         this.classEnvironment.putValue(fieldName, (position1) -> {
            return this.type.getStaticMember(position1, this.classEnvironment, fieldName);
         }, this.position);
      } else {
         this.nonStatics.add(parsedZenClassField);
         this.classEnvironment.putValue(fieldName, (position1) -> {
            return (new ExpressionThis(position1, this.type)).getMember(position1, this.classEnvironment, fieldName);
         }, this.position);
      }

   }

   public void writeClass(IEnvironmentGlobal environmentGlobal) {
      ClassWriter newClass = new ZenClassWriter(2);
      newClass.visitSource(this.position.getFileName(), (String)null);
      newClass.visit(50, 1, this.className, (String)null, "java/lang/Object", new String[0]);
      EnvironmentClass environmentNewClass = new EnvironmentClass(newClass, this.classEnvironment);
      environmentNewClass.putValue("this", (position1) -> {
         return new ExpressionThis(position1, this.type);
      }, this.position);
      this.writeStatics(newClass, environmentNewClass);
      this.visitNonStatics(newClass);
      this.writeConstructors(newClass, environmentNewClass);
      this.writeMethods(newClass, environmentNewClass);
      newClass.visitEnd();
      byte[] thisClassArray = newClass.toByteArray();
      environmentGlobal.putClass(this.className, thisClassArray);
      this.thisClass = (new ClassLoader() {
         private Class<?> find(byte[] array) {
            return this.defineClass(ParsedZenClass.this.className, array, 0, array.length);
         }
      }).find(thisClassArray);
   }

   private void visitNonStatics(ClassWriter newClass) {
      for(ParsedZenClassField nonStatic : this.nonStatics) {
         nonStatic.visit(newClass);
      }

   }

   private void writeStatics(ClassWriter newClass, EnvironmentClass environmentNewClass) {
      if (!this.statics.isEmpty()) {
         MethodOutput clinit = new MethodOutput(newClass, 9, "<clinit>", "()V", (String)null, (String[])null);
         EnvironmentMethod clinitEnvironment = new EnvironmentMethod(clinit, environmentNewClass);
         clinit.start();

         for(ParsedZenClassField aStatic : this.statics) {
            aStatic.writeAll(clinitEnvironment, newClass, clinit, this.className);
         }

         clinit.ret();
         clinit.end();
      }

   }

   private void writeConstructors(ClassWriter newClass, EnvironmentClass environmentNewClass) {
      for(ParsedClassConstructor constructor : this.constructors) {
         constructor.writeAll(environmentNewClass, newClass, this.nonStatics, this.className, this.position);
      }

   }

   private void writeMethods(ClassWriter newClass, EnvironmentClass environmentNewClass) {
      for(ParsedZenClassMethod parsedMethod : this.methods) {
         parsedMethod.writeAll(newClass, environmentNewClass);
      }

   }

   public ZenType[] predictCallTypes(int numArguments) {
      for(ParsedClassConstructor con : this.constructors) {
         if (con.types.length == numArguments) {
            return con.types;
         }
      }

      return new ZenType[0];
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression[] arguments) {
      for(ParsedClassConstructor constructor : this.constructors) {
         if (constructor.canAccept(arguments, environment)) {
            return constructor.call(position, arguments, this.type);
         }
      }

      environment.error("Could not find constructor for " + this.name + "with " + arguments.length + " arguments.");
      return new ExpressionInvalid(position);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name, boolean isStatic) {
      if (this.members.containsKey(name)) {
         return isStatic ? ((ZenNativeMember)this.members.get(name)).instance(position, environment) : ((ZenNativeMember)this.members.get(name)).instance(position, environment, value);
      } else {
         environment.error("Could not find " + (isStatic ? "static " : "") + "member " + name);
         return new ExpressionInvalid(position);
      }
   }
}
