package com.teamacronymcoders.contenttweaker.modules.vanilla.resources;

import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.mc1120.brackets.BracketHandlerLiquid;
import crafttweaker.zenscript.GlobalRegistry;
import crafttweaker.zenscript.IBracketHandler;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;

@BracketHandler(
   priority = 10
)
@ZenRegister
public class LiquidBracketHandler implements IBracketHandler {
   private static final IJavaMethod method = JavaMethod.get(GlobalRegistry.getTypes(), LiquidBracketHandler.class, "getFromString", new Class[]{String.class});

   public LiquidBracketHandler() {
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      return tokens.size() > 2 && (((Token)tokens.get(0)).getValue().equals("liquid") || ((Token)tokens.get(0)).getValue().equals("fluid")) && ((Token)tokens.get(1)).getValue().equals(":") ? (position) -> {
         return new ExpressionCallStatic(position, environment, method, new Expression[]{new ExpressionString(position, String.join("", (Iterable)tokens.subList(2, tokens.size()).stream().map(Token::getValue).collect(Collectors.toList())))});
      } : null;
   }

   public static ILiquidStack getFromString(String name) {
      ILiquidStack out = BracketHandlerLiquid.getLiquid(name);
      return (ILiquidStack)(out == null ? new GhostLiquidStack(name) : out);
   }
}
