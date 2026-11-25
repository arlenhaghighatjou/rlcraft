package crafttweaker.mc1120.brackets;

import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.mc1120.liquid.MCLiquidStack;
import crafttweaker.zenscript.GlobalRegistry;
import crafttweaker.zenscript.IBracketHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;

@BracketHandler(
   priority = 100
)
@ZenRegister
public class BracketHandlerLiquid implements IBracketHandler {
   private static final Map<String, Fluid> fluidNames = new HashMap();
   private final IJavaMethod method = JavaMethod.get(GlobalRegistry.getTypes(), BracketHandlerLiquid.class, "getLiquid", String.class);

   public BracketHandlerLiquid() {
   }

   public static void rebuildLiquidRegistry() {
      fluidNames.clear();

      for(String fluidName : FluidRegistry.getRegisteredFluids().keySet()) {
         fluidNames.put(fluidName.replace(" ", ""), FluidRegistry.getFluid(fluidName));
      }

   }

   public static ILiquidStack getLiquid(String name) {
      Fluid fluid = (Fluid)fluidNames.get(name);
      return fluid != null ? new MCLiquidStack(new FluidStack(fluid, 1)) : null;
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      return tokens.size() > 2 && (((Token)tokens.get(0)).getValue().equals("liquid") || ((Token)tokens.get(0)).getValue().equals("fluid")) && ((Token)tokens.get(1)).getValue().equals(":") ? this.find(environment, tokens, 2, tokens.size()) : null;
   }

   private IZenSymbol find(IEnvironmentGlobal environment, List<Token> tokens, int startIndex, int endIndex) {
      StringBuilder valueBuilder = new StringBuilder();

      for(int i = startIndex; i < endIndex; ++i) {
         Token token = (Token)tokens.get(i);
         valueBuilder.append(token.getValue());
      }

      Fluid fluid = (Fluid)fluidNames.get(valueBuilder.toString());
      return fluid != null ? (position) -> {
         return new ExpressionCallStatic(position, environment, this.method, new Expression[]{new ExpressionString(position, valueBuilder.toString())});
      } : null;
   }

   public Class<?> getReturnedClass() {
      return ILiquidStack.class;
   }

   public String getRegexMatchingString() {
      return "(fluid|liquid):.*";
   }
}
