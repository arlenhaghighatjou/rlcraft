package com.teamacronymcoders.base.recipesystem.input;

import com.google.gson.annotations.JsonAdapter;
import com.teamacronymcoders.base.blocks.BlockStateMatcher;
import com.teamacronymcoders.base.json.deserializer.BlockPosDeserializer;
import com.teamacronymcoders.base.json.deserializer.BlockStateMatcherDeserializer;
import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import net.minecraft.util.math.BlockPos;

public class BlockStateInput implements IInput {
   @JsonAdapter(BlockStateMatcherDeserializer.class)
   private final BlockStateMatcher blockState;
   @JsonAdapter(BlockPosDeserializer.class)
   private final BlockPos offset;

   public BlockStateInput(BlockStateMatcher blockStateMatcher, BlockPos offset) {
      this.blockState = blockStateMatcher;
      this.offset = offset;
   }

   public boolean isMatched(RecipeContainer recipeContainer) {
      return this.blockState.matches(recipeContainer.getWorld().getBlockState(this.handlePos(recipeContainer)));
   }

   public void consume(RecipeContainer recipeContainer) {
      recipeContainer.getWorld().setBlockToAir(this.handlePos(recipeContainer));
   }

   private BlockPos handlePos(RecipeContainer recipeContainer) {
      return this.offset != null ? recipeContainer.getPos().add(this.offset) : recipeContainer.getPos();
   }
}
