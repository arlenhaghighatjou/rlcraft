package stanhebben.zenscript.compiler;

import java.util.Set;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.IZenErrorLogger;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.util.ZenPosition;

public interface IEnvironmentGlobal extends ITypeRegistry, IZenErrorLogger {
   IZenCompileEnvironment getEnvironment();

   TypeExpansion getExpansion(String var1);

   ClassNameGenerator getClassNameGenerator();

   String makeClassName();

   String makeClassNameWithMiddleName(String var1);

   boolean containsClass(String var1);

   Set<String> getClassNames();

   byte[] getClass(String var1);

   void putClass(String var1, byte[] var2);

   IPartialExpression getValue(String var1, ZenPosition var2);

   void putValue(String var1, IZenSymbol var2, ZenPosition var3);
}
