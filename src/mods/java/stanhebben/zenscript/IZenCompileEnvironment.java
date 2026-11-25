package stanhebben.zenscript;

import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.TypeRegistry;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;

public interface IZenCompileEnvironment {
   IZenErrorLogger getErrorLogger();

   IZenSymbol getGlobal(String var1);

   IZenSymbol getBracketed(IEnvironmentGlobal var1, List<Token> var2);

   TypeRegistry getTypeRegistry();

   TypeExpansion getExpansion(String var1);

   void setRegistry(IZenRegistry var1);
}
