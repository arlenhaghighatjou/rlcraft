package codersafterdark.compatskills.common.compats.reskillable.brackethandlers;

import codersafterdark.compatskills.common.compats.reskillable.playerexpansion.wrapper.CTSkill;
import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.skill.Skill;
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
public class BracketHandlerSkill implements IBracketHandler {
   private static final IJavaMethod method = JavaMethod.get(GlobalRegistry.getTypes(), BracketHandlerSkill.class, "getSkill", new Class[]{String.class});

   public BracketHandlerSkill() {
   }

   public static CTSkill getSkill(String name) {
      Skill skill = (Skill)ReskillableRegistries.SKILLS.getValue(new ResourceLocation(name));
      return skill == null ? null : new CTSkill(skill);
   }

   public IZenSymbol resolve(IEnvironmentGlobal iEnvironmentGlobal, List<Token> list) {
      return list.size() > 2 && ((Token)list.get(0)).getValue().equalsIgnoreCase("skill") ? (zenPosition) -> {
         return new ExpressionCallStatic(zenPosition, iEnvironmentGlobal, method, new Expression[]{new ExpressionString(zenPosition, (String)list.subList(2, list.size()).stream().map(Token::getValue).collect(Collectors.joining()))});
      } : null;
   }
}
