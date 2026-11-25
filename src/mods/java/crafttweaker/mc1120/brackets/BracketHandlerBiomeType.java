package crafttweaker.mc1120.brackets;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.world.IBiomeType;
import crafttweaker.mc1120.world.MCBiomeType;
import crafttweaker.zenscript.IBracketHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraftforge.common.BiomeDictionary.Type;
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
public class BracketHandlerBiomeType implements IBracketHandler {
   public static final Map<String, IBiomeType> biomeTypes = new HashMap();
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerBiomeType.class, "getBiomeType", String.class);

   public BracketHandlerBiomeType() {
   }

   public static void rebuildBiomeTypeRegistry() {
      biomeTypes.clear();

      for(Type t : Type.getAll()) {
         biomeTypes.put(t.getName().toLowerCase(), new MCBiomeType(t));
      }

   }

   public static IBiomeType getBiomeType(String name) {
      return (IBiomeType)biomeTypes.get(name.toLowerCase());
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      return tokens.size() > 2 && ((Token)tokens.get(0)).getValue().equals("biomeTypes") && ((Token)tokens.get(1)).getValue().equals(":") ? this.find(environment, tokens, 2, tokens.size()) : null;
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
      return "biomeTypes:.*";
   }

   public Class<?> getReturnedClass() {
      return IEntityDefinition.class;
   }
}
