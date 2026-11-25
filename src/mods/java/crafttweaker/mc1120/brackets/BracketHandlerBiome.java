package crafttweaker.mc1120.brackets;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.world.IBiome;
import crafttweaker.zenscript.IBracketHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;

@BracketHandler(
   priority = 100
)
@ZenRegister
public class BracketHandlerBiome implements IBracketHandler {
   private static final Map<String, IBiome> biomeNames = new HashMap();
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerBiome.class, "getBiome", String.class);

   public BracketHandlerBiome() {
   }

   public static void rebuildBiomeRegistry() {
      biomeNames.clear();
      CraftTweakerAPI.game.getBiomes().forEach((iBiome) -> {
         IBiome var10000 = (IBiome)biomeNames.put(iBiome.getId().split(":")[1], iBiome);
      });
   }

   public static IBiome getBiome(String name) {
      return (IBiome)biomeNames.get(name);
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      return tokens.size() > 2 && ((Token)tokens.get(0)).getValue().equals("biome") && ((Token)tokens.get(1)).getValue().equals(":") ? this.find(environment, tokens, 2, tokens.size()) : null;
   }

   private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens, int startIndex, int endIndex) {
      StringBuilder valueBuilder = new StringBuilder();

      for(int i = startIndex; i < endIndex; ++i) {
         Token token = (Token)tokens.get(i);
         valueBuilder.append(token.getValue());
      }

      return (position) -> {
         return new ExpressionCallStatic(position, environment, this.method, new Expression[]{new ExpressionString(position, valueBuilder.toString())});
      };
   }

   public String getRegexMatchingString() {
      return "biome:.*";
   }

   public Class<?> getReturnedClass() {
      return IEntityDefinition.class;
   }
}
