package com.teamacronymcoders.contenttweaker.modules.vanilla.resources;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.ICTBlockState;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.MCBlockState;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.BracketHandler;
import crafttweaker.mc1120.block.MCSpecificBlock;
import crafttweaker.zenscript.IBracketHandler;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionInt;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.util.ZenPosition;

@BracketHandler
public class BlockBracketHandler implements IBracketHandler {
   private final IJavaMethod method = CraftTweakerAPI.getJavaMethod(BlockBracketHandler.class, "getBlockState", new Class[]{String.class, Integer.TYPE});

   public BlockBracketHandler() {
   }

   public static ICTBlockState getBlockState(String name, int meta) {
      ICTBlockState iBlock = null;
      Block block = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
      if (block != null) {
         iBlock = new MCBlockState(new MCSpecificBlock(block, meta));
      }

      return iBlock;
   }

   public IZenSymbol resolve(IEnvironmentGlobal environment, List<Token> tokens) {
      IZenSymbol zenSymbol = null;
      if (tokens.size() > 2 && "block".equalsIgnoreCase(((Token)tokens.get(0)).getValue())) {
         int meta = 0;
         String blockName;
         if (tokens.size() >= 5) {
            blockName = ((Token)tokens.get(2)).getValue() + ":" + ((Token)tokens.get(4)).getValue();
            if (tokens.size() >= 7) {
               meta = Integer.parseInt(((Token)tokens.get(6)).getValue());
            }
         } else {
            blockName = ((Token)tokens.get(2)).getValue();
         }

         zenSymbol = new BlockBracketHandler.BlockReferenceSymbol(environment, blockName, meta);
      }

      return zenSymbol;
   }

   private class BlockReferenceSymbol implements IZenSymbol {
      private final IEnvironmentGlobal environment;
      private final String name;
      private final int meta;

      public BlockReferenceSymbol(IEnvironmentGlobal environment, String name, int meta) {
         this.environment = environment;
         this.name = name;
         this.meta = meta;
      }

      public IPartialExpression instance(ZenPosition position) {
         return new ExpressionCallStatic(position, this.environment, BlockBracketHandler.this.method, new Expression[]{new ExpressionString(position, this.name), new ExpressionInt(position, (long)this.meta, ZenType.INT)});
      }
   }
}
