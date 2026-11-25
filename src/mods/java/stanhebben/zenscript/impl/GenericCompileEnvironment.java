package stanhebben.zenscript.impl;

import java.util.List;
import java.util.function.Function;
import stanhebben.zenscript.IZenCompileEnvironment;
import stanhebben.zenscript.IZenErrorLogger;
import stanhebben.zenscript.IZenRegistry;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.TypeRegistry;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;

public class GenericCompileEnvironment implements IZenCompileEnvironment {
   private IZenRegistry registry;

   public GenericCompileEnvironment() {
   }

   public IZenErrorLogger getErrorLogger() {
      return this.registry.getErrorLogger();
   }

   public IZenSymbol getGlobal(String name) {
      IZenSymbol symbol = (IZenSymbol)this.registry.getGlobals().get(name);
      return symbol != null ? symbol : this.registry.getRoot().get(name);
   }

   public IZenSymbol getBracketed(IEnvironmentGlobal environment, List<Token> tokens) {
      return this.registry.resolveBracket(environment, tokens);
   }

   public TypeRegistry getTypeRegistry() {
      return this.registry.getTypes();
   }

   public TypeExpansion getExpansion(String type) {
      return (TypeExpansion)this.registry.getExpansions().computeIfAbsent(type, TypeExpansion::new);
   }

   public IZenRegistry getRegistry() {
      return this.registry;
   }

   public void setRegistry(IZenRegistry registry) {
      this.registry = registry;
   }
}
