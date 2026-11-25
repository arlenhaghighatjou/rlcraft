package crafttweaker.mc1120.brackets;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.damage.IDamageSource;
import crafttweaker.mc1120.damage.expand.MCDamageSourceExpand;
import crafttweaker.zenscript.IBracketHandler;
import java.util.List;
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
public class BracketHandlerDamageSource implements IBracketHandler {
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerDamageSource.class, "getFromString", String.class);

   public BracketHandlerDamageSource() {
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      if (tokens != null && tokens.size() >= 3 && ((Token)tokens.get(0)).getValue().equalsIgnoreCase("damageSource")) {
         String name = ((Token)tokens.get(2)).getValue();
         byte var5 = -1;
         switch(name.hashCode()) {
         case -1839154047:
            if (name.equals("STARVE")) {
               var5 = 8;
            }
            break;
         case -1795561552:
            if (name.equals("CRAMMING")) {
               var5 = 6;
            }
            break;
         case -1772574996:
            if (name.equals("FIREWORKS")) {
               var5 = 19;
            }
            break;
         case -1734240269:
            if (name.equals("WITHER")) {
               var5 = 15;
            }
            break;
         case -1608741616:
            if (name.equals("IN_FIRE")) {
               var5 = 0;
            }
            break;
         case -1608243036:
            if (name.equals("IN_WALL")) {
               var5 = 5;
            }
            break;
         case -1533238518:
            if (name.equals("LIGHTNING_BOLT")) {
               var5 = 1;
            }
            break;
         case -1053761699:
            if (name.equals("FLY_INTO_WALL")) {
               var5 = 11;
            }
            break;
         case -1016983206:
            if (name.equals("HOT_FLOOR")) {
               var5 = 4;
            }
            break;
         case -578686826:
            if (name.equals("ON_FIRE")) {
               var5 = 2;
            }
            break;
         case -560849908:
            if (name.equals("DRAGON_BREATH")) {
               var5 = 18;
            }
            break;
         case 2150267:
            if (name.equals("FALL")) {
               var5 = 10;
            }
            break;
         case 2329312:
            if (name.equals("LAVA")) {
               var5 = 3;
            }
            break;
         case 62437548:
            if (name.equals("ANVIL")) {
               var5 = 16;
            }
            break;
         case 65320984:
            if (name.equals("DROWN")) {
               var5 = 7;
            }
            break;
         case 73118093:
            if (name.equals("MAGIC")) {
               var5 = 14;
            }
            break;
         case 628972693:
            if (name.equals("FALLING_BLOCK")) {
               var5 = 17;
            }
            break;
         case 637834679:
            if (name.equals("GENERIC")) {
               var5 = 13;
            }
            break;
         case 700204283:
            if (name.equals("OUT_OF_WORLD")) {
               var5 = 12;
            }
            break;
         case 1980261421:
            if (name.equals("CACTUS")) {
               var5 = 9;
            }
         }

         switch(var5) {
         case 0:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 16:
         case 17:
         case 18:
         case 19:
            return this.makeSymbolSwitched(name, environment);
         default:
            return this.makeSymbol(name, environment);
         }
      } else {
         return null;
      }
   }

   private IZenSymbol makeSymbolSwitched(String name, IEnvironmentGlobal environment) {
      return (position) -> {
         return new ExpressionCallStatic(position, environment, JavaMethod.get(environment.getEnvironment().getTypeRegistry(), MCDamageSourceExpand.class, name), new Expression[0]);
      };
   }

   private IZenSymbol makeSymbol(String name, IEnvironmentGlobal environment) {
      return (position) -> {
         return new ExpressionCallStatic(position, environment, this.method, new Expression[]{new ExpressionString(position, name)});
      };
   }

   public static IDamageSource getFromString(String name) {
      return MCDamageSourceExpand.createOfType(name);
   }

   public String getRegexMatchingString() {
      return "damageSource:.*";
   }

   public Class<?> getReturnedClass() {
      return IDamageSource.class;
   }
}
