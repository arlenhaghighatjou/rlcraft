package crafttweaker.mc1120.brackets;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.mc1120.enchantments.MCEnchantmentDefinition;
import crafttweaker.zenscript.IBracketHandler;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.enchantment.Enchantment;
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
public class BracketHandlerEnchantments implements IBracketHandler {
   public static final HashMap<String, IEnchantmentDefinition> enchantments = new HashMap();
   private final IJavaMethod method;

   public BracketHandlerEnchantments() {
      updateEnchantmentRegistry();
      this.method = CraftTweakerAPI.getJavaMethod(BracketHandlerEnchantments.class, "getEnchantment", String.class);
   }

   public static IEnchantmentDefinition getEnchantment(String id) {
      IEnchantmentDefinition result = (IEnchantmentDefinition)enchantments.get(id);
      if (result == null) {
         CraftTweakerAPI.logError("Could not find Enchantment for the id " + id);
      }

      return result;
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      if (tokens.size() < 3) {
         return null;
      } else {
         return ((Token)tokens.get(0)).getValue().equalsIgnoreCase("enchantment") && ((Token)tokens.get(1)).getValue().equals(":") ? (position) -> {
            return new ExpressionCallStatic(position, environment, this.method, new Expression[]{new ExpressionString(position, (String)tokens.subList(2, tokens.size()).stream().map(Token::getValue).collect(Collectors.joining()))});
         } : null;
      }
   }

   public String getRegexMatchingString() {
      return "enchantment:.*";
   }

   public Class<?> getReturnedClass() {
      return IEnchantmentDefinition.class;
   }

   public static void updateEnchantmentRegistry() {
      Enchantment.REGISTRY.getKeys().forEach((key) -> {
         IEnchantmentDefinition var10000 = (IEnchantmentDefinition)enchantments.computeIfAbsent(key.toString(), (name) -> {
            return new MCEnchantmentDefinition((Enchantment)Enchantment.REGISTRY.getObject(key));
         });
      });
   }
}
