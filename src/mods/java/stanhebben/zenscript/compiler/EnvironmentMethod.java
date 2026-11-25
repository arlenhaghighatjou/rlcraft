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

public class EnvironmentMethod implements IEnvironmentMethod {
   private final MethodOutput output;
   private final HashMap<SymbolLocal, Integer> locals;
   final IEnvironmentClass environment;
   final Map<String, IZenSymbol> local;

   public EnvironmentMethod(MethodOutput output, IEnvironmentClass environment) {
      this.output = output;
      this.locals = new HashMap();
      this.environment = environment;
      this.local = new HashMap();
   }

   public MethodOutput getOutput() {
      return this.output;
   }

   public ClassVisitor getClassOutput() {
      return this.environment.getClassOutput();
   }

   public int getLocal(SymbolLocal variable) {
      if (!this.locals.containsKey(variable)) {
         this.locals.put(variable, Integer.valueOf(this.output.local(variable.getType().toASMType())));
      }

      return ((Integer)this.locals.get(variable)).intValue();
   }

   public ZenType getType(Type type) {
      return this.environment.getType(type);
   }

   public void error(ZenPosition position, String message) {
      this.environment.error(position, message);
   }

   public void warning(ZenPosition position, String message) {
      this.environment.warning(position, message);
   }

   public void info(ZenPosition position, String message) {
      this.environment.info(position, message);
   }

   public IZenCompileEnvironment getEnvironment() {
      return this.environment.getEnvironment();
   }

   public TypeExpansion getExpansion(String name) {
      return this.environment.getExpansion(name);
   }

   public ClassNameGenerator getClassNameGenerator() {
      return this.environment.getClassNameGenerator();
   }

   public String makeClassName() {
      return this.environment.makeClassName();
   }

   public String makeClassNameWithMiddleName(String middleName) {
      return this.environment.makeClassNameWithMiddleName(middleName);
   }

   public void putClass(String name, byte[] data) {
      this.environment.putClass(name, data);
   }

   public boolean containsClass(String name) {
      return this.environment.containsClass(name);
   }

   public IPartialExpression getValue(String name, ZenPosition position) {
      return this.local.containsKey(name) ? ((IZenSymbol)this.local.get(name)).instance(position) : this.environment.getValue(name, position);
   }

   public void putValue(String name, IZenSymbol value, ZenPosition position) {
      if (this.local.containsKey(name)) {
         this.error(position, "Value already defined in this scope: " + name);
      } else {
         this.local.put(name, value);
      }

   }

   public Set<String> getClassNames() {
      return this.environment.getClassNames();
   }

   public byte[] getClass(String name) {
      return this.environment.getClass(name);
   }

   public void error(String message) {
      this.environment.error(message);
   }

   public void error(String message, Throwable e) {
      this.environment.error(message, e);
   }

   public void warning(String message) {
      this.environment.warning(message);
   }

   public void info(String message) {
      this.environment.info(message);
   }
}
