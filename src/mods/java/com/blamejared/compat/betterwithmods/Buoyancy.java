package com.blamejared.compat.betterwithmods;

import betterwithmods.module.hardcore.world.HCBuoy;
import betterwithmods.util.item.Stack;
import betterwithmods.util.item.StackMap;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseMapAddition;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import java.util.Map.Entry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterwithmods.Buoyancy")
@ModOnly("betterwithmods")
@ZenRegister
public class Buoyancy {
   public Buoyancy() {
   }

   @ZenMethod
   public static void set(IItemStack stack, float value) {
      StackMap<Float> map = new StackMap(1.0F);
      map.put(new Stack(InputHelper.toStack(stack)), Float.valueOf(value));
      ModTweaker.LATE_ADDITIONS.add(new Buoyancy.Set(map));
   }

   public static class Set extends BaseMapAddition<Stack, Float> {
      protected Set(StackMap<Float> map) {
         super("Set Item Buoyancy", HCBuoy.buoyancy, map);
      }

      protected String getRecipeInfo(Entry<Stack, Float> recipe) {
         return ((Stack)recipe.getKey()).toString() + " -> " + recipe.getValue();
      }
   }
}
