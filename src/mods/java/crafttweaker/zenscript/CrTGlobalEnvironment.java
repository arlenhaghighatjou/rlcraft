package crafttweaker.zenscript;

import crafttweaker.CraftTweakerAPI;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.compiler.ClassNameGenerator;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class CrTGlobalEnvironment implements IEnvironmentGlobal {
   private final Map<String, byte[]> classes;
   private final Map<String, IZenSymbol> symbols;
   private final ClassNameGenerator generator;
   private List<SingleError> errors;

   public CrTGlobalEnvironment(Map<String, byte[]> classes) {
      this(classes, "");
   }

   public CrTGlobalEnvironment(Map<String, byte[]> classes, String loaderMainName) {
      this.errors = new ArrayList();
      this.classes = classes;
      this.symbols = new HashMap();
      this.generator = new ClassNameGenerator("ZenClass" + loaderMainName);
   }

   public IZenCompileEnvironment getEnvironment() {
      return GlobalRegistry.getEnvironment();
   }

   public TypeExpansion getExpansion(String name) {
      return (TypeExpansion)GlobalRegistry.getExpansions().computeIfAbsent(name, TypeExpansion::new);
   }

   public ClassNameGenerator getClassNameGenerator() {
      return this.generator;
   }

   public String makeClassName() {
      return this.generator.generate();
   }

   public String makeClassNameWithMiddleName(String middleName) {
      return this.generator.generateWithMiddleName(middleName);
   }

   public boolean containsClass(String name) {
      return this.classes.containsKey(name);
   }

   public void putClass(String name, byte[] data) {
      this.classes.put(name, data);
   }

   public IPartialExpression getValue(String name, ZenPosition position) {
      if (this.symbols.containsKey(name)) {
         return ((IZenSymbol)this.symbols.get(name)).instance(position);
      } else if (GlobalRegistry.getGlobals().containsKey(name)) {
         return ((IZenSymbol)GlobalRegistry.getGlobals().get(name)).instance(position);
      } else {
         IZenSymbol pkg = GlobalRegistry.getRoot().get(name);
         return pkg == null ? null : pkg.instance(position);
      }
   }

   public void putValue(String name, IZenSymbol value, ZenPosition position) {
      if (this.symbols.containsKey(name)) {
         this.error(position, "Value already defined in this scope: " + name);
      } else {
         this.symbols.put(name, value);
      }

   }

   public ZenType getType(Type type) {
      return GlobalRegistry.getTypes().getType(type);
   }

   public void error(ZenPosition position, String message) {
      CraftTweakerAPI.logError(position.toString() + " > " + message);
      this.errors.add(new SingleError(position.getFileName(), position.getLine(), position.getLineOffset(), message, SingleError.Level.ERROR));
   }

   public void warning(ZenPosition position, String message) {
      CraftTweakerAPI.logWarning(position.toString() + " > " + message);
      this.errors.add(new SingleError(position.getFileName(), position.getLine(), position.getLineOffset(), message, SingleError.Level.WARN));
   }

   public void info(ZenPosition position, String message) {
      CraftTweakerAPI.logInfo(position.toString() + " > " + message);
      this.errors.add(new SingleError(position.getFileName(), position.getLine(), position.getLineOffset(), message, SingleError.Level.INFO));
   }

   public Set<String> getClassNames() {
      return this.classes.keySet();
   }

   public byte[] getClass(String name) {
      return (byte[])this.classes.get(name);
   }

   public void error(String message) {
      CraftTweakerAPI.logError(message);
   }

   public void error(String message, Throwable e) {
      CraftTweakerAPI.logError(message, e);
   }

   public void warning(String message) {
      CraftTweakerAPI.logWarning(message);
   }

   public void info(String message) {
      CraftTweakerAPI.logInfo(message);
   }

   public void clear() {
      this.errors.clear();
   }

   public List<SingleError> getErrors() {
      return this.errors;
   }
}
