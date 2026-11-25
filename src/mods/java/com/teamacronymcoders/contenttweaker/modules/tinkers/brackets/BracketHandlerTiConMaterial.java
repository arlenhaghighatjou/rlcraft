package com.teamacronymcoders.contenttweaker.modules.tinkers.brackets;

import com.teamacronymcoders.contenttweaker.modules.tinkers.materials.TConMaterialRepresentation;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
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

@BracketHandler
@ModOnly("tconstruct")
@ZenRegister
public class BracketHandlerTiConMaterial implements IBracketHandler {
   public final IJavaMethod method = JavaMethod.get(GlobalRegistry.getTypes(), TConMaterialRepresentation.class, "getFromString", new Class[]{String.class});

   public BracketHandlerTiConMaterial() {
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      return tokens != null && tokens.size() >= 3 && ((Token)tokens.get(0)).getValue().equalsIgnoreCase("ticonmaterial") ? (position) -> {
         return new ExpressionCallStatic(position, environment, this.method, new Expression[]{new ExpressionString(position, String.join("", (Iterable)tokens.subList(2, tokens.size()).stream().map(Token::getValue).collect(Collectors.toList())))});
      } : null;
   }
}
