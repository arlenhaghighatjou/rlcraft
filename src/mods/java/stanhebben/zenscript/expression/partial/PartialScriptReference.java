package stanhebben.zenscript.expression.partial;

import java.util.Arrays;
import java.util.HashMap;
import stanhebben.zenscript.compiler.EnvironmentClass;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolScriptReference;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class PartialScriptReference implements IPartialExpression {
   private final HashMap<String, PartialScriptReference> subs = new HashMap();
   private EnvironmentClass environmentScript = null;

   public PartialScriptReference() {
   }

   public void addScriptOrDirectory(EnvironmentClass environmentScript, String[] names) {
      if (names.length == 0) {
         this.environmentScript = environmentScript;
      } else {
         String name = names[0];
         this.subs.putIfAbsent(name, new PartialScriptReference());
         ((PartialScriptReference)this.subs.get(name)).addScriptOrDirectory(environmentScript, (String[])Arrays.copyOfRange(names, 1, names.length));
      }
   }

   public Expression eval(IEnvironmentGlobal environment) {
      throw new UnsupportedOperationException("Cannot eval a script file!");
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      throw new UnsupportedOperationException("Cannot assign to a script file!");
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      IPartialExpression out = (IPartialExpression)this.subs.get(name);
      if (out == null && this.environmentScript != null) {
         out = this.environmentScript.getValue(name, position);
      }

      if (out != null) {
         return out;
      } else {
         environment.error(position, "Could not get Reference: " + name);
         return new ExpressionInvalid(position);
      }
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      throw new UnsupportedOperationException("Cannot call a script file!");
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return new ZenType[0];
   }

   public IZenSymbol toSymbol() {
      return new SymbolScriptReference(this);
   }

   public ZenType getType() {
      throw new UnsupportedOperationException("Cannot get a script file's type since it has no type!");
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      throw new UnsupportedOperationException("Cannot cast a script file!");
   }
}
