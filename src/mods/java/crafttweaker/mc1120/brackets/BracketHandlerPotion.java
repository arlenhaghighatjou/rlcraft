package crafttweaker.mc1120.brackets;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.potions.IPotion;
import crafttweaker.mc1120.potions.MCPotion;
import crafttweaker.zenscript.IBracketHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.util.ZenPosition;

@BracketHandler(
   priority = 100
)
@ZenRegister
public class BracketHandlerPotion implements IBracketHandler {
   private static final Map<String, Potion> potionNames = new HashMap();
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerPotion.class, "getPotion", String.class);

   public BracketHandlerPotion() {
   }

   public static Map<String, Potion> getPotionNames() {
      return potionNames;
   }

   public static void rebuildRegistry() {
      potionNames.clear();

      for(Potion potion : ForgeRegistries.POTIONS) {
         potionNames.put(potion.getRegistryName().toString(), potion);
      }

   }

   public static IPotion getPotion(String name) {
      Potion pot = (Potion)potionNames.get(name);
      return pot == null ? null : new MCPotion(pot);
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      return tokens.size() > 2 && ((Token)tokens.get(0)).getValue().equals("potion") && ((Token)tokens.get(1)).getValue().equals(":") ? this.find(environment, tokens, 2, tokens.size()) : null;
   }

   private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens, int startIndex, int endIndex) {
      StringBuilder valueBuilder = new StringBuilder();

      for(int i = startIndex; i < endIndex; ++i) {
         Token token = (Token)tokens.get(i);
         valueBuilder.append(token.getValue());
      }

      return new BracketHandlerPotion.PotionReferenceSymbol(environment, valueBuilder.toString());
   }

   public Class<?> getReturnedClass() {
      return IPotion.class;
   }

   public String getRegexMatchingString() {
      return "potion:.*";
   }

   private class PotionReferenceSymbol implements IZenSymbol {
      private final IEnvironmentGlobal environment;
      private final String name;

      public PotionReferenceSymbol(IEnvironmentGlobal environment, String name) {
         this.environment = environment;
         this.name = name;
      }

      public IPartialExpression instance(ZenPosition position) {
         return new ExpressionCallStatic(position, this.environment, BracketHandlerPotion.this.method, new Expression[]{new ExpressionString(position, this.name)});
      }
   }
}
