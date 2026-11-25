package com.yungnickyoung.minecraft.bettermineshafts.util;

import com.yungnickyoung.minecraft.bettermineshafts.BetterMineshafts;
import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Map.Entry;
import java.util.function.BinaryOperator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class BlockSetSelector {
   private Map<IBlockState, Float> entries = new HashMap();
   private IBlockState defaultBlock = Blocks.AIR.getDefaultState();

   public BlockSetSelector(IBlockState defaultBlock) {
      this.defaultBlock = defaultBlock;
   }

   public BlockSetSelector() {
   }

   public static BlockSetSelector from(IBlockState... blockStates) {
      BlockSetSelector selector = new BlockSetSelector();
      float chance = 1.0F / (float)blockStates.length;

      for(IBlockState state : blockStates) {
         selector.addBlock(state, chance);
      }

      return selector;
   }

   public BlockSetSelector addBlock(IBlockState blockState, float chance) {
      for(Entry<IBlockState, Float> entry : this.entries.entrySet()) {
         if (entry.getKey() == blockState) {
            BetterMineshafts.LOGGER.warn(String.format("WARNING: duplicate block %s added to BlockSelector!", blockState.toString()));
            return this;
         }
      }

      float currTotal = ((Float)this.entries.values().stream().reduce(Float::sum).orElse(Float.valueOf(0.0F))).floatValue();
      float newTotal = currTotal + chance;
      if (newTotal > 1.0F) {
         BetterMineshafts.LOGGER.warn(String.format("WARNING: block %s added to BlockSelector exceeds max probabiltiy of 1!", blockState.toString()));
         return this;
      } else {
         this.entries.put(blockState, Float.valueOf(chance));
         return this;
      }
   }

   public IBlockState get(EaglercraftRandom random) {
      float target = random.nextFloat();
      float currBottom = 0.0F;

      for(Entry<IBlockState, Float> entry : this.entries.entrySet()) {
         float chance = ((Float)entry.getValue()).floatValue();
         if (currBottom <= target && target < currBottom + chance) {
            return (IBlockState)entry.getKey();
         }

         currBottom += chance;
      }

      return this.defaultBlock;
   }

   public void setDefaultBlock(IBlockState blockState) {
      this.defaultBlock = blockState;
   }
}
