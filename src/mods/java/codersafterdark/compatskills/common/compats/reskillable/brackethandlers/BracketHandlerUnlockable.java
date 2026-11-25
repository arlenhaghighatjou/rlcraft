package codersafterdark.compatskills.common.compats.reskillable.brackethandlers;

import codersafterdark.compatskills.common.compats.reskillable.playerexpansion.wrapper.CTUnlockable;
import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.unlockable.Unlockable;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.zenscript.GlobalRegistry;
import crafttweaker.zenscript.IBracketHandler;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;

@BracketHandler
@ZenRegister
public class BracketHandlerUnlockable implements IBracketHandler {
   private static final IJavaMethod method = JavaMethod.get(GlobalRegistry.getTypes(), BracketHandlerUnlockable.class, "getUnlockable", new Class[]{String.class});

   public BracketHandlerUnlockable() {
   }

   public static CTUnlockable getUnlockable(String name) {
      Unlockable unlockable = (Unlockable)ReskillableRegistries.UNLOCKABLES.getValue(new ResourceLocation(name.trim().substring(1)));
      return unlockable == null ? null : new CTUnlockable(unlockable);
   }

   public IZenSymbol resolve(IEnvironmentGlobal iEnvironmentGlobal, List<Token> list) {
      return list.size() > 2 && ((Token)list.get(0)).getValue().equalsIgnoreCase("unlockable") ? (zenPosition) -> {
         return new ExpressionCallStatic(zenPosition, iEnvironmentGlobal, method, new Expression[]{new ExpressionString(zenPosition, (String)list.subList(1, list.size()).stream().map(Token::getValue).collect(Collectors.joining()))});
      } : null;
   }
}
