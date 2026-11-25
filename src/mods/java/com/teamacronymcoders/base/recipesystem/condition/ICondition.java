package com.teamacronymcoders.base.recipesystem.condition;

import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;

public interface ICondition {
   boolean isMet(RecipeContainer var1, @Nullable EntityPlayer var2);
}
