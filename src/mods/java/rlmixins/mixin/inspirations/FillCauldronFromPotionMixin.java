package rlmixins.mixin.inspirations;

import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.recipe.FillCauldronFromPotion;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({FillCauldronFromPotion.class})
public abstract class FillCauldronFromPotionMixin {
   public FillCauldronFromPotionMixin() {
   }

   @Redirect(
      method = {"getState"},
      at = @At(
   value = "INVOKE",
   target = "Lknightminer/inspirations/library/recipe/cauldron/ICauldronRecipe$CauldronState;potion(Lnet/minecraft/potion/PotionType;)Lknightminer/inspirations/library/recipe/cauldron/ICauldronRecipe$CauldronState;",
   ordinal = 1
),
      remap = false
   )
   public CauldronState rlmixins_inspirationsFillCauldronFromPotion_getState(PotionType potion) {
      return CauldronState.potion(PotionTypes.MUNDANE);
   }
}
