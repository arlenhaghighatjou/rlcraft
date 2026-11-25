package stanhebben.zenscript.compiler;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.ClassVisitor;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class EnvironmentClass implements IEnvironmentClass {
   private final ClassVisitor output;
   private final IEnvironmentGlobal global;
   private final Map<String, IZenSymbol> local;

   public EnvironmentClass(ClassVisitor output, IEnvironmentGlobal global) {
      this.output = output;
      this.global = global;
      this.local = new HashMap();
   }

   public ClassVisitor getClassOutput() {
      return this.output;
   }

   public ZenType getType(Type type) {
      return this.global.getType(type);
   }

   public IZenCompileEnvironment getEnvironment() {
      return this.global.getEnvironment();
   }

   public TypeExpansion getExpansion(String name) {
      return this.global.getExpansion(name);
   }

   public ClassNameGenerator getClassNameGenerator() {
      return this.global.getClassNameGenerator();
   }

   public String makeClassName() {
      return this.global.makeClassName();
   }

   public String makeClassNameWithMiddleName(String middleName) {
      return this.global.makeClassNameWithMiddleName(middleName);
   }

   public boolean containsClass(String name) {
      return this.global.containsClass(name);
   }

   public void putClass(String name, byte[] data) {
      this.global.putClass(name, data);
   }

   public IPartialExpression getValue(String name, ZenPosition position) {
      return this.local.containsKey(name) ? ((IZenSymbol)this.local.get(name)).instance(position) : this.global.getValue(name, position);
   }

   public void putValue(String name, IZenSymbol value, ZenPosition position) {
      if (this.local.containsKey(name)) {
         this.error(position, "Value already defined in this scope: " + name);
      } else {
         this.local.put(name, value);
      }

   }

   public void error(ZenPosition position, String message) {
      this.global.error(position, message);
   }

   public void warning(ZenPosition position, String message) {
      this.global.warning(position, message);
   }

   public void info(ZenPosition position, String message) {
      this.global.info(position, message);
   }

   public Set<String> getClassNames() {
      return this.global.getClassNames();
   }

   public byte[] getClass(String name) {
      return this.global.getClass(name);
   }

   public void error(String message) {
      this.global.error(message);
   }

   public void error(String message, Throwable e) {
      this.global.error(message, e);
   }

   public void warning(String message) {
      this.global.warning(message);
   }

   public void info(String message) {
      this.global.info(message);
   }
}
