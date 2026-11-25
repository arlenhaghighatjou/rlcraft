package com.teamacronymcoders.contenttweaker.modules.vanilla.items.food;

import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.contenttweaker.ContentTweaker;
import com.teamacronymcoders.contenttweaker.modules.vanilla.functions.IItemFoodEaten;
import com.teamacronymcoders.contenttweaker.modules.vanilla.items.ItemRepresentation;
import crafttweaker.annotations.ZenRegister;
import net.minecraft.item.EnumAction;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenRegister
@ZenClass("mods.contenttweaker.ItemFood")
public class ItemFoodRepresentation extends ItemRepresentation {
   @ZenProperty
   public int healAmount;
   @ZenProperty
   public boolean alwaysEditable = false;
   @ZenProperty
   public boolean wolfFood = false;
   @ZenProperty
   public float saturation = 0.6F;
   @ZenProperty
   public boolean alwaysEdible = false;
   @ZenProperty
   public IItemFoodEaten onItemFoodEaten = null;

   public ItemFoodRepresentation() {
      this.itemUseAction = EnumAction.EAT.toString();
   }

   @ZenMethod
   public int getHealAmount() {
      return this.healAmount;
   }

   @ZenMethod
   public void setHealAmount(int healAmount) {
      this.healAmount = healAmount;
   }

   @ZenMethod
   public boolean isAlwaysEditable() {
      return this.alwaysEditable;
   }

   @ZenMethod
   public void setAlwaysEditable(boolean alwaysEditable) {
      this.alwaysEditable = alwaysEditable;
   }

   @ZenMethod
   public boolean isWolfFood() {
      return this.wolfFood;
   }

   @ZenMethod
   public void setWolfFood(boolean wolfFood) {
      this.wolfFood = wolfFood;
   }

   @ZenMethod
   public float getSaturation() {
      return this.saturation;
   }

   @ZenMethod
   public void setSaturation(float saturation) {
      this.saturation = saturation;
   }

   @ZenMethod
   public boolean isAlwaysEdible() {
      return this.alwaysEdible;
   }

   @ZenMethod
   public void setAlwaysEdible(boolean alwaysEdible) {
      this.alwaysEdible = alwaysEdible;
   }

   @ZenMethod
   public IItemFoodEaten getOnItemFoodEaten() {
      return this.onItemFoodEaten;
   }

   @ZenMethod
   public void setOnItemFoodEaten(IItemFoodEaten onItemFoodEaten) {
      this.onItemFoodEaten = onItemFoodEaten;
   }

   public String getTypeName() {
      return "ItemFood";
   }

   @ZenMethod
   public void register() {
      if (this.alwaysEditable) {
         this.alwaysEdible = true;
      }

      ((ItemRegistry)ContentTweaker.instance.getRegistry(ItemRegistry.class, "ITEM")).register(new ItemContentFood(this));
   }
}
