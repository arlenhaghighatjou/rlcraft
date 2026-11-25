package stanhebben.zenscript.compiler;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.IZenErrorLogger;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class EnvironmentGlobal implements IEnvironmentGlobal {
   private final IZenCompileEnvironment environment;
   private final IZenErrorLogger errors;
   private final Map<String, byte[]> classes;
   private final Map<String, IZenSymbol> local;
   private final ClassNameGenerator nameGen;
   private final TypeRegistry types;

   public EnvironmentGlobal(IZenCompileEnvironment environment, Map<String, byte[]> classes, ClassNameGenerator nameGen) {
      this.environment = environment;
      this.errors = environment.getErrorLogger();
      this.classes = classes;
      this.nameGen = nameGen;
      this.types = environment.getTypeRegistry();
      this.local = new HashMap();
   }

   public IZenCompileEnvironment getCompileEnvironment() {
      return this.environment;
   }

   public ZenType getType(Type type) {
      return this.types.getType(type);
   }

   public boolean containsClass(String name) {
      return this.classes.containsKey(name);
   }

   public void putClass(String name, byte[] data) {
      this.classes.put(name, data);
   }

   public ClassNameGenerator getClassNameGenerator() {
      return this.nameGen;
   }

   public String makeClassName() {
      return this.nameGen.generate();
   }

   public String makeClassNameWithMiddleName(String middleName) {
      return this.nameGen.generateWithMiddleName(middleName);
   }

   public TypeExpansion getExpansion(String type) {
      return this.environment.getExpansion(type);
   }

   public void error(ZenPosition position, String message) {
      this.errors.error(position, message);
   }

   public void warning(ZenPosition position, String message) {
      this.errors.warning(position, message);
   }

   public void info(ZenPosition position, String message) {
      this.errors.info(position, message);
   }

   public IZenCompileEnvironment getEnvironment() {
      return this.environment;
   }

   public IPartialExpression getValue(String name, ZenPosition position) {
      if (this.local.containsKey(name)) {
         return ((IZenSymbol)this.local.get(name)).instance(position);
      } else {
         IZenSymbol symbol = this.environment.getGlobal(name);
         return symbol == null ? null : symbol.instance(position);
      }
   }

   public void putValue(String name, IZenSymbol value, ZenPosition position) {
      if (this.local.containsKey(name)) {
         this.error(position, "Value already defined in this scope: " + name);
      } else {
         this.local.put(name, value);
      }

   }

   public Set<String> getClassNames() {
      return this.classes.keySet();
   }

   public byte[] getClass(String name) {
      return (byte[])this.classes.get(name);
   }

   public void error(String message) {
      this.errors.error(message);
   }

   public void error(String message, Throwable e) {
      this.errors.error(message, e);
   }

   public void warning(String message) {
      this.errors.warning(message);
   }

   public void info(String message) {
      this.errors.info(message);
   }
}
