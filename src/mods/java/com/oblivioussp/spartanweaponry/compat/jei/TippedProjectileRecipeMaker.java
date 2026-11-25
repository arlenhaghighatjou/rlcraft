package com.oblivioussp.spartanweaponry.compat.jei;

import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;

public class TippedProjectileRecipeMaker {
   private TippedProjectileRecipeMaker() {
   }

   public static List<TippedProjectileRecipeWrapper> getTippedBoltRecipes() {
      List<TippedProjectileRecipeWrapper> list = new ArrayList();

      for(ResourceLocation res : PotionType.REGISTRY.getKeys()) {
         PotionType type = (PotionType)PotionType.REGISTRY.getObject(res);
         if (PotionType.REGISTRY.getIDForObject(type) >= 5) {
            list.add(new TippedProjectileRecipeWrapper(type, ItemRegistrySW.bolt, ItemRegistrySW.boltTipped));
         }
      }

      return list;
   }

   public static List<TippedProjectileRecipeWrapper> getTippedBoltDiamondRecipes() {
      List<TippedProjectileRecipeWrapper> list = new ArrayList();

      for(ResourceLocation res : PotionType.REGISTRY.getKeys()) {
         PotionType type = (PotionType)PotionType.REGISTRY.getObject(res);
         if (PotionType.REGISTRY.getIDForObject(type) >= 5) {
            list.add(new TippedProjectileRecipeWrapper(type, ItemRegistrySW.boltDiamond, ItemRegistrySW.boltTippedDiamond));
         }
      }

      return list;
   }

   public static List<TippedProjectileRecipeWrapper> getTippedArrowWoodRecipes() {
      List<TippedProjectileRecipeWrapper> list = new ArrayList();

      for(ResourceLocation res : PotionType.REGISTRY.getKeys()) {
         PotionType type = (PotionType)PotionType.REGISTRY.getObject(res);
         if (PotionType.REGISTRY.getIDForObject(type) >= 5) {
            list.add(new TippedProjectileRecipeWrapper(type, ItemRegistrySW.arrowWood, ItemRegistrySW.arrowTippedWood));
         }
      }

      return list;
   }

   public static List<TippedProjectileRecipeWrapper> getTippedArrowIronRecipes() {
      List<TippedProjectileRecipeWrapper> list = new ArrayList();

      for(ResourceLocation res : PotionType.REGISTRY.getKeys()) {
         PotionType type = (PotionType)PotionType.REGISTRY.getObject(res);
         if (PotionType.REGISTRY.getIDForObject(type) >= 5) {
            list.add(new TippedProjectileRecipeWrapper(type, ItemRegistrySW.arrowIron, ItemRegistrySW.arrowTippedIron));
         }
      }

      return list;
   }

   public static List<TippedProjectileRecipeWrapper> getTippedArrowDiamondRecipes() {
      List<TippedProjectileRecipeWrapper> list = new ArrayList();

      for(ResourceLocation res : PotionType.REGISTRY.getKeys()) {
         PotionType type = (PotionType)PotionType.REGISTRY.getObject(res);
         if (PotionType.REGISTRY.getIDForObject(type) >= 5) {
            list.add(new TippedProjectileRecipeWrapper(type, ItemRegistrySW.arrowDiamond, ItemRegistrySW.arrowTippedDiamond));
         }
      }

      return list;
   }
}
