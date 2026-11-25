package stanhebben.zenscript.impl;

import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;

public interface IBracketHandler {
   IZenSymbol resolve(IEnvironmentGlobal var1, List<Token> var2);

   default String getRegexMatchingString() {
      return ".*";
   }

   default Class<?> getReturnedClass() {
      return null;
   }
}
