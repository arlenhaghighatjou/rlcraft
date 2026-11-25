package crafttweaker.zenscript;

import java.util.List;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.IZenErrorLogger;
import stanhebben.zenscript.IZenRegistry;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.TypeRegistry;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;

public class CrTCompileEnvironment implements IZenCompileEnvironment {
   public CrTCompileEnvironment() {
   }

   public IZenErrorLogger getErrorLogger() {
      return GlobalRegistry.getErrors();
   }

   public IZenSymbol getGlobal(String name) {
      return GlobalRegistry.getGlobals().containsKey(name) ? (IZenSymbol)GlobalRegistry.getGlobals().get(name) : GlobalRegistry.getRoot().get(name);
   }

   public IZenSymbol getBracketed(IEnvironmentGlobal environment, List<Token> tokens) {
      return GlobalRegistry.resolveBracket(environment, tokens);
   }

   public TypeRegistry getTypeRegistry() {
      return GlobalRegistry.getTypes();
   }

   public TypeExpansion getExpansion(String type) {
      return (TypeExpansion)GlobalRegistry.getExpansions().get(type);
   }

   public void setRegistry(IZenRegistry registry) {
      throw new UnsupportedOperationException("Registry is not local");
   }
}
