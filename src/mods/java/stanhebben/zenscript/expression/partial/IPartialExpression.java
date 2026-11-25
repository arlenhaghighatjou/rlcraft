package stanhebben.zenscript.expression.partial;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public interface IPartialExpression {
   Expression eval(IEnvironmentGlobal var1);

   Expression assign(ZenPosition var1, IEnvironmentGlobal var2, Expression var3);

   IPartialExpression getMember(ZenPosition var1, IEnvironmentGlobal var2, String var3);

   Expression call(ZenPosition var1, IEnvironmentMethod var2, Expression... var3);

   ZenType[] predictCallTypes(int var1);

   IZenSymbol toSymbol();

   ZenType getType();

   ZenType toType(IEnvironmentGlobal var1);
}
