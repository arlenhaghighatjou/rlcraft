package stanhebben.zenscript.impl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.IZenRegistry;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.compiler.ClassNameGenerator;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class GenericGlobalEnvironment implements IEnvironmentGlobal {
   private final Map<String, byte[]> classes;
   private final Map<String, IZenSymbol> symbols;
   private final ClassNameGenerator generator;
   private final IZenRegistry registry;

   public GenericGlobalEnvironment(Map<String, byte[]> classes, IZenRegistry registry) {
      this.classes = classes;
      this.symbols = new HashMap();
      this.generator = new ClassNameGenerator();
      this.registry = registry;
   }

   public IZenCompileEnvironment getEnvironment() {
      return this.registry.getCompileEnvironment();
   }

   public TypeExpansion getExpansion(String name) {
      return (TypeExpansion)this.registry.getExpansions().computeIfAbsent(name, TypeExpansion::new);
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

   public Set<String> getClassNames() {
      return this.classes.keySet();
   }

   public byte[] getClass(String name) {
      return (byte[])this.classes.get(name);
   }

   public void putClass(String name, byte[] data) {
      this.classes.put(name, data);
   }

   public IPartialExpression getValue(String name, ZenPosition position) {
      if (this.symbols.containsKey(name)) {
         return ((IZenSymbol)this.symbols.get(name)).instance(position);
      } else if (this.registry.getGlobals().containsKey(name)) {
         return ((IZenSymbol)this.registry.getGlobals().get(name)).instance(position);
      } else {
         IZenSymbol pkg = this.registry.getRoot().get(name);
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

   public void error(ZenPosition position, String message) {
      this.registry.getErrorLogger().error(position, message);
   }

   public void warning(ZenPosition position, String message) {
      this.registry.getErrorLogger().warning(position, message);
   }

   public void info(ZenPosition position, String message) {
      this.registry.getErrorLogger().info(position, message);
   }

   public void error(String message) {
      this.registry.getErrorLogger().error(message);
   }

   public void error(String message, Throwable e) {
      this.registry.getErrorLogger().error(message, e);
   }

   public void warning(String message) {
      this.registry.getErrorLogger().warning(message);
   }

   public void info(String message) {
      this.registry.getErrorLogger().info(message);
   }

   public ZenType getType(Type type) {
      return this.registry.getTypes().getType(type);
   }
}
