package mezz.jei.plugins.vanilla.brewing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import mezz.jei.Internal;
import mezz.jei.collect.SetMultiMap;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

public class BrewingRecipeUtil {
   public static final ItemStack POTION = new ItemStack(Items.POTIONITEM);
   public static final ItemStack WATER_BOTTLE = PotionUtils.addPotionToItemStack(POTION.copy(), PotionTypes.WATER);
   private final Map<String, Integer> brewingStepCache = new HashMap();
   private final SetMultiMap<String, String> potionMap = new SetMultiMap<String, String>();

   public BrewingRecipeUtil() {
      this.clearCache();
   }

   public void addRecipe(ItemStack inputPotion, ItemStack outputPotion) {
      String potionInputUid = Internal.getStackHelper().getUniqueIdentifierForStack(inputPotion);
      String potionOutputUid = Internal.getStackHelper().getUniqueIdentifierForStack(outputPotion);
      this.potionMap.put(potionOutputUid, potionInputUid);
      this.clearCache();
   }

   public int getBrewingSteps(ItemStack outputPotion) {
      String potionInputUid = Internal.getStackHelper().getUniqueIdentifierForStack(outputPotion);
      return this.getBrewingSteps(potionInputUid, new HashSet());
   }

   private void clearCache() {
      if (this.brewingStepCache.size() != 1) {
         this.brewingStepCache.clear();
         String waterBottleUid = Internal.getStackHelper().getUniqueIdentifierForStack(WATER_BOTTLE);
         this.brewingStepCache.put(waterBottleUid, Integer.valueOf(0));
      }

   }

   private int getBrewingSteps(String potionOutputUid, Set<String> previousSteps) {
      Integer brewingSteps = (Integer)this.brewingStepCache.get(potionOutputUid);
      if (brewingSteps == null) {
         previousSteps.add(potionOutputUid);
         Collection<String> prevPotions = this.potionMap.get(potionOutputUid);
         if (!prevPotions.isEmpty()) {
            int minPrevSteps = Integer.MAX_VALUE;

            for(String prevPotion : prevPotions) {
               if (!previousSteps.contains(prevPotion)) {
                  int prevSteps = this.getBrewingSteps(prevPotion, previousSteps);
                  minPrevSteps = Math.min(minPrevSteps, prevSteps);
               }
            }

            if (minPrevSteps < Integer.MAX_VALUE) {
               brewingSteps = minPrevSteps + 1;
               this.brewingStepCache.put(potionOutputUid, brewingSteps);
            }
         }
      }

      return brewingSteps == null ? Integer.MAX_VALUE : brewingSteps.intValue();
   }
}
