package stanhebben.zenscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import stanhebben.zenscript.compiler.EnvironmentScript;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.definitions.Import;
import stanhebben.zenscript.definitions.ParsedExpansion;
import stanhebben.zenscript.definitions.ParsedFunction;
import stanhebben.zenscript.definitions.ParsedGlobalValue;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClass;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolType;
import stanhebben.zenscript.symbols.SymbolZenClass;
import stanhebben.zenscript.type.ZenType;

public class ZenParsedFile {
   private final String filename;
   private final String classname;
   private final List<Import> imports;
   private final Map<String, ParsedFunction> functions;
   private final Map<String, ParsedGlobalValue> globals = new LinkedHashMap();
   private final Map<String, ParsedZenClass> classes = new HashMap();
   private final List<Statement> statements;
   private final IEnvironmentGlobal environmentScript;

   public ZenParsedFile(String filename, String classname, ZenTokener tokener, IEnvironmentGlobal environment) {
      this.filename = filename;
      this.classname = classname;
      this.imports = new ArrayList();
      this.functions = new HashMap();
      this.statements = new ArrayList();
      this.environmentScript = new EnvironmentScript(environment);
      tokener.setFile(this);

      while(tokener.peek() != null && tokener.peek().getType() == 160) {
         Token start = tokener.next();
         List<String> importName = new ArrayList();
         Token tName = tokener.required(1, "identifier expected");
         importName.add(tName.getValue());

         while(tokener.optional(10) != null) {
            Token tNamePart = tokener.required(1, "identifier expected");
            importName.add(tNamePart.getValue());
         }

         String rename = null;
         if (tokener.optional(120) != null) {
            Token tRename = tokener.required(1, "identifier expected");
            rename = tRename.getValue();
         }

         tokener.required(33, "; expected");
         this.imports.add(new Import(start.getPosition(), importName, rename));
      }

      for(Import imprt : this.imports) {
         List<String> name = imprt.getName();
         IPartialExpression type = null;
         StringBuilder nameSoFar = new StringBuilder();

         for(String part : name) {
            if (type == null) {
               nameSoFar.append(part);
               type = environment.getValue(part, imprt.getPosition());
               if (type == null) {
                  environment.error(imprt.getPosition(), "could not find package " + type);
                  break;
               }
            } else {
               nameSoFar.append('.').append(part);
               type = type.getMember(imprt.getPosition(), environment, part);
               if (type == null) {
                  environment.error(imprt.getPosition(), "could not find type or package " + nameSoFar);
                  break;
               }
            }
         }

         if (type != null) {
            IZenSymbol symbol = type.toSymbol();
            if (symbol == null) {
               this.environmentScript.error(imprt.getPosition(), "Not a valid type");
            } else {
               this.environmentScript.putValue(imprt.getRename(), type.toSymbol(), imprt.getPosition());
            }
         } else {
            this.environmentScript.putValue(imprt.getRename(), new SymbolType(ZenType.ANY), imprt.getPosition());
         }
      }

      while(tokener.hasNext()) {
         Token next = tokener.peek();
         if (next.getType() != 666 && next.getType() != 667) {
            if (next.getType() == 108) {
               ParsedFunction function = ParsedFunction.parse(tokener, this.environmentScript);
               if (this.functions.containsKey(function.getName())) {
                  environment.error(function.getPosition(), "function " + function.getName() + " already exists");
               }

               this.functions.put(function.getName(), function);
            } else if (next.getType() == 123456789) {
               ParsedZenClass parsedZenClass = ParsedZenClass.parse(tokener, this.environmentScript);
               if (this.classes.containsKey(parsedZenClass.name)) {
                  environment.error(parsedZenClass.position, "Class " + parsedZenClass.name + " already exists!");
               } else {
                  this.classes.put(parsedZenClass.name, parsedZenClass);
                  this.environmentScript.putValue(parsedZenClass.name, new SymbolZenClass(parsedZenClass.type), parsedZenClass.position);
               }

               parsedZenClass.writeClass(this.environmentScript);
            } else if (next.getType() == 44) {
               ParsedExpansion expansion = ParsedExpansion.parse(tokener, this.environmentScript, this);
               String name = expansion.getName();
               String compileName = expansion.getCompileName();
               ParsedFunction function = expansion.getFunction();
               if (this.functions.containsKey(compileName)) {
                  this.environmentScript.error(function.getPosition(), "expand method  " + name + " already exists");
               }

               this.functions.put(compileName, function);
               this.environmentScript.getExpansion(expansion.getType().getName()).addZenExpandMethod(name, expansion);
            } else {
               this.statements.add(Statement.read(tokener, this.environmentScript, (ZenType)null));
            }
         } else {
            ParsedGlobalValue value = ParsedGlobalValue.parse(tokener, this.environmentScript, classname, next.getType() == 666);
            if (this.globals.containsKey(value.getName())) {
               environment.warning(value.getPosition(), "Global already defined: " + value.getName());
            }

            this.globals.put(value.getName(), value);
         }
      }

   }

   public IEnvironmentGlobal getEnvironment() {
      return this.environmentScript;
   }

   public String getClassName() {
      return this.classname;
   }

   public String getFileName() {
      return this.filename;
   }

   public List<Import> getImports() {
      return this.imports;
   }

   public List<Statement> getStatements() {
      return this.statements;
   }

   public Map<String, ParsedFunction> getFunctions() {
      return this.functions;
   }

   public Map<String, ParsedGlobalValue> getGlobals() {
      return this.globals;
   }

   public String toString() {
      return this.filename;
   }

   public Map<String, ParsedZenClass> getClasses() {
      return this.classes;
   }
}
