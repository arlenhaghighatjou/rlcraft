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
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class EnvironmentScope implements IEnvironmentMethod {
   private final IEnvironmentMethod outer;
   private final Map<String, IZenSymbol> local;
   private final Map<SymbolLocal, Integer> locals;

   public EnvironmentScope(IEnvironmentMethod outer) {
      this.outer = outer;
      this.local = new HashMap();
      this.locals = new HashMap();
   }

   public MethodOutput getOutput() {
      return this.outer.getOutput();
   }

   public int getLocal(SymbolLocal variable) {
      return this.locals.containsKey(variable) ? ((Integer)this.locals.get(variable)).intValue() : this.outer.getLocal(variable);
   }

   public ClassVisitor getClassOutput() {
      return this.outer.getClassOutput();
   }

   public ZenType getType(Type type) {
      return this.outer.getType(type);
   }

   public IZenCompileEnvironment getEnvironment() {
      return this.outer.getEnvironment();
   }

   public TypeExpansion getExpansion(String name) {
      return this.outer.getExpansion(name);
   }

   public ClassNameGenerator getClassNameGenerator() {
      return this.outer.getClassNameGenerator();
   }

   public String makeClassName() {
      return this.outer.makeClassName();
   }

   public String makeClassNameWithMiddleName(String middleName) {
      return this.outer.makeClassNameWithMiddleName(middleName);
   }

   public boolean containsClass(String name) {
      return this.outer.containsClass(name);
   }

   public void putClass(String name, byte[] data) {
      this.outer.putClass(name, data);
   }

   public IPartialExpression getValue(String name, ZenPosition position) {
      return this.local.containsKey(name) ? ((IZenSymbol)this.local.get(name)).instance(position) : this.outer.getValue(name, position);
   }

   public void putValue(String name, IZenSymbol value, ZenPosition position) {
      if (this.local.containsKey(name)) {
         this.error(position, "Value already defined in this scope: " + name);
      } else {
         this.local.put(name, value);
      }

   }

   public void error(ZenPosition position, String message) {
      this.outer.error(position, message);
   }

   public void warning(ZenPosition position, String message) {
      this.outer.warning(position, message);
   }

   public void info(ZenPosition position, String message) {
      this.outer.info(position, message);
   }

   public Set<String> getClassNames() {
      return this.outer.getClassNames();
   }

   public byte[] getClass(String name) {
      return this.outer.getClass(name);
   }

   public void error(String message) {
      this.outer.error(message);
   }

   public void error(String message, Throwable e) {
      this.outer.error(message, e);
   }

   public void warning(String message) {
      this.outer.warning(message);
   }

   public void info(String message) {
      this.outer.info(message);
   }
}
