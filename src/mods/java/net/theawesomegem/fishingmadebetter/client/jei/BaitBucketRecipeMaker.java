package net.theawesomegem.fishingmadebetter.client.jei;

import java.util.ArrayList;
import java.util.List;
import net.theawesomegem.fishingmadebetter.common.item.ItemManager;

public final class BaitBucketRecipeMaker {
   public BaitBucketRecipeMaker() {
   }

   public static List<BaitBucketRecipeWrapper> getBaitBucketRecipes() {
      List<BaitBucketRecipeWrapper> recipes = new ArrayList();
      recipes.add(new BaitBucketRecipeWrapper(ItemManager.BAIT_BUCKET));
      return recipes;
   }
}
