package com.blamejared.compat.betterwithmods;

import betterwithmods.common.registry.BellowsManager;
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

@ZenClass("mods.betterwithmods.Bellows")
@ModOnly("betterwithmods")
@ZenRegister
public class Bellows {
   public Bellows() {
   }

   @ZenMethod
   public static void set(IItemStack stack, float value) {
      StackMap<Float> map = new StackMap(1.0F);
      map.put(new Stack(InputHelper.toStack(stack)), Float.valueOf(value));
      ModTweaker.LATE_ADDITIONS.add(new Bellows.Set(map));
   }

   public static class Set extends BaseMapAddition<Stack, Float> {
      protected Set(StackMap<Float> map) {
         super("Set Bellows Item Weight", BellowsManager.bellowing, map);
      }

      protected String getRecipeInfo(Entry<Stack, Float> recipe) {
         return ((Stack)recipe.getKey()).toString() + " -> " + recipe.getValue();
      }
   }
}
