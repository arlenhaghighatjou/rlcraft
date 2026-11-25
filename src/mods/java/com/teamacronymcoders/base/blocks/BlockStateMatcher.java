package com.teamacronymcoders.base.blocks;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class BlockStateMatcher {
   private final Block block;
   private final Map<IProperty<?>, List<?>> allowedValues;

   public BlockStateMatcher(Block block, Map<IProperty<?>, List<?>> allowedValues) {
      this.block = block;
      this.allowedValues = allowedValues;
   }

   public BlockStateMatcher(Block block) {
      this(block, Maps.newHashMap());
   }

   public boolean matches(IBlockState blockState) {
      if (this.block == blockState.getBlock()) {
         for(IProperty<?> property : blockState.getPropertyKeys()) {
            List<?> allowedValue = (List)this.allowedValues.get(property);
            if (allowedValue != null && !allowedValue.contains(blockState.getValue(property))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
