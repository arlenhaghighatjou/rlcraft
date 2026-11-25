package stanhebben.zenscript;

import java.util.List;
import java.util.Map;
import java.util.Set;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.TypeRegistry;
import stanhebben.zenscript.impl.IBracketHandler;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolPackage;
import stanhebben.zenscript.util.Pair;

public interface IZenRegistry {
   void registerGlobal(String var1, IZenSymbol var2);

   void registerExpansion(Class<?> var1);

   void registerBracketHandler(IBracketHandler var1);

   void removeBracketHandler(IBracketHandler var1);

   void registerNativeClass(Class<?> var1);

   IZenSymbol getStaticFunction(Class var1, String var2, Class... var3);

   IZenSymbol getStaticField(Class var1, String var2);

   IZenSymbol resolveBracket(IEnvironmentGlobal var1, List<Token> var2);

   IEnvironmentGlobal makeGlobalEnvironment(Map<String, byte[]> var1);

   IZenCompileEnvironment getCompileEnvironment();

   Map<String, IZenSymbol> getGlobals();

   Set<Pair<Integer, IBracketHandler>> getBracketHandlers();

   TypeRegistry getTypes();

   SymbolPackage getRoot();

   Map<String, TypeExpansion> getExpansions();

   void setCompileEnvironment(IZenCompileEnvironment var1);

   void setGlobals(Map<String, IZenSymbol> var1);

   void setBracketHandlers(Set<Pair<Integer, IBracketHandler>> var1);

   void setTypes(TypeRegistry var1);

   void setRoot(SymbolPackage var1);

   void setExpansions(Map<String, TypeExpansion> var1);

   IZenErrorLogger getErrorLogger();

   void setErrorLogger(IZenErrorLogger var1);

   IZenLogger getLogger();

   void setLogger(IZenLogger var1);
}
