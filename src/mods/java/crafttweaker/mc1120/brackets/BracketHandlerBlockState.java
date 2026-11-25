package crafttweaker.mc1120.brackets;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.mc1120.block.MCBlockState;
import crafttweaker.zenscript.IBracketHandler;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
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
public class BracketHandlerBlockState implements IBracketHandler {
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BracketHandlerBlockState.class, "getBlockState", String.class, String.class);

   public BracketHandlerBlockState() {
   }

   public static IBlockState getBlockState(String name, String properties) {
      IBlockState blockState = null;
      if (!ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(name))) {
         CraftTweakerAPI.logError("No block found with name '" + name + "'. Air block used instead.");
      }

      Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
      if (block != null) {
         blockState = new MCBlockState(block.getDefaultState());
         if (properties != null && properties.length() > 0) {
            for(String propertyPair : properties.split(",")) {
               String[] splitPair = propertyPair.split("=");
               if (splitPair.length != 2) {
                  CraftTweakerAPI.logWarning("Invalid blockstate property format '" + propertyPair + "'. Using default property value.");
               } else {
                  blockState = blockState.withProperty(splitPair[0], splitPair[1]);
               }
            }
         }
      }

      return blockState;
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      IZenSymbol zenSymbol = null;
      String bracketString = (String)tokens.stream().map(Token::getValue).reduce("", String::concat);
      String[] split = bracketString.split(":", 4);
      if (split.length > 1 && "blockstate".equalsIgnoreCase(split[0])) {
         String properties = "";
         String blockName;
         if (split.length > 2) {
            blockName = split[1] + ":" + split[2];
            if (split.length > 3) {
               properties = split[3];
            }
         } else {
            blockName = split[1];
         }

         if (!ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(blockName))) {
            return null;
         }

         zenSymbol = new BracketHandlerBlockState.BlockStateReferenceSymbol(environment, blockName, properties);
      }

      return zenSymbol;
   }

   public String getRegexMatchingString() {
      return "blockstate:.*";
   }

   public Class<?> getReturnedClass() {
      return IBlockState.class;
   }

   private class BlockStateReferenceSymbol implements IZenSymbol {
      private final IEnvironmentGlobal environment;
      private final String name;
      private final String properties;

      public BlockStateReferenceSymbol(IEnvironmentGlobal environment, String name, String properties) {
         this.environment = environment;
         this.name = name;
         this.properties = properties;
      }

      public IPartialExpression instance(ZenPosition position) {
         return new ExpressionCallStatic(position, this.environment, BracketHandlerBlockState.this.method, new Expression[]{new ExpressionString(position, this.name), new ExpressionString(position, this.properties)});
      }
   }
}
