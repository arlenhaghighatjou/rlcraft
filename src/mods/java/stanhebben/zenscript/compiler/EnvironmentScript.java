package stanhebben.zenscript.compiler;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class EnvironmentScript implements IEnvironmentGlobal {
   private final IEnvironmentGlobal parent;
   private final Map<String, IZenSymbol> imports;

   public EnvironmentScript(IEnvironmentGlobal parent) {
      this.parent = parent;
      this.imports = new HashMap();
   }

   public IZenCompileEnvironment getEnvironment() {
      return this.parent.getEnvironment();
   }

   public TypeExpansion getExpansion(String name) {
      return this.parent.getExpansion(name);
   }

   public ClassNameGenerator getClassNameGenerator() {
      return this.parent.getClassNameGenerator();
   }

   public String makeClassName() {
      return this.parent.makeClassName();
   }

   public String makeClassNameWithMiddleName(String middleName) {
      return this.parent.makeClassNameWithMiddleName(middleName);
   }

   public boolean containsClass(String name) {
      return this.parent.containsClass(name);
   }

   public void putClass(String name, byte[] data) {
      this.parent.putClass(name, data);
   }

   public IPartialExpression getValue(String name, ZenPosition position) {
      if (this.imports.containsKey(name)) {
         IZenSymbol imprt = (IZenSymbol)this.imports.get(name);
         if (imprt == null) {
            throw new RuntimeException("How could this happen?");
         } else {
            return imprt.instance(position);
         }
      } else {
         return this.parent.getValue(name, position);
      }
   }

   public void putValue(String name, IZenSymbol value, ZenPosition position) {
      if (value == null) {
         throw new IllegalArgumentException("value cannot be null");
      } else {
         if (this.imports.containsKey(name)) {
            this.error(position, "Value already defined in this scope: " + name);
         } else {
            this.imports.put(name, value);
         }

      }
   }

   public ZenType getType(Type type) {
      return this.parent.getType(type);
   }

   public void error(ZenPosition position, String message) {
      this.parent.error(position, message);
   }

   public void warning(ZenPosition position, String message) {
      this.parent.warning(position, message);
   }

   public void info(ZenPosition position, String message) {
      this.parent.info(position, message);
   }

   public Set<String> getClassNames() {
      return this.parent.getClassNames();
   }

   public byte[] getClass(String name) {
      return this.parent.getClass(name);
   }

   public void error(String message) {
      this.parent.error(message);
   }

   public void error(String message, Throwable e) {
      this.parent.error(message, e);
   }

   public void warning(String message) {
      this.parent.warning(message);
   }

   public void info(String message) {
      this.parent.info(message);
   }

   public IEnvironmentGlobal getParent() {
      return this.parent;
   }
}
