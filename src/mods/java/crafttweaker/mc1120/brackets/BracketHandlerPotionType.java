package crafttweaker.mc1120.brackets;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.potions.IPotionType;
import crafttweaker.zenscript.IBracketHandler;
import java.util.List;
import net.minecraft.potion.PotionType;
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
public class BracketHandlerPotionType implements IBracketHandler {
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerPotionType.class, "getFromString", String.class);

   public BracketHandlerPotionType() {
   }

   public static IPotionType getFromString(String id) {
      IPotionType result = CraftTweakerMC.getIPotionType(PotionType.getPotionTypeForName(id));
      if (result == null) {
         CraftTweakerAPI.logError("Could not find PotionType for the id " + id);
      }

      return result;
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      return tokens.size() > 2 && ((Token)tokens.get(0)).getValue().equalsIgnoreCase("potiontype") && ((Token)tokens.get(1)).getValue().equals(":") ? this.find(environment, tokens, tokens.size()) : null;
   }

   private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens, int endIndex) {
      StringBuilder valueBuilder = new StringBuilder();

      for(int i = 2; i < endIndex; ++i) {
         Token token = (Token)tokens.get(i);
         valueBuilder.append(token.getValue());
      }

      return (position) -> {
         return new ExpressionCallStatic(position, environment, this.method, new Expression[]{new ExpressionString(position, valueBuilder.toString())});
      };
   }

   public String getRegexMatchingString() {
      return "potiontype:.*";
   }

   public Class<?> getReturnedClass() {
      return IPotionType.class;
   }
}
