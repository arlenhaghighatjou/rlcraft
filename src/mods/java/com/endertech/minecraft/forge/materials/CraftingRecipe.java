package com.endertech.minecraft.forge.materials;

import com.endertech.common.Args;
import com.endertech.common.CommonString;
import com.endertech.minecraft.forge.ForgeBounds;
import com.endertech.minecraft.forge.ForgeRecipe;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IHaveConfig;
import com.endertech.minecraft.forge.items.ForgeStack;
import com.endertech.minecraft.forge.units.ItemState;
import com.endertech.minecraft.forge.units.UnitId;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class CraftingRecipe extends ForgeRecipe {
   private final String pattern;
   private final UnitId outputId;
   private final int amount;
   private final List<UnitId> components;
   private final ForgeConfig config;

   public static CraftingRecipe Create(ForgeRecipe.Type type, ForgeConfig config, String pattern, String output, int amount, String... components) {
      String category = IHaveConfig.getClassCategory(CraftingRecipe.class, type.getName());
      return new CraftingRecipe(config, category, pattern, output, amount, components);
   }

   public static CraftingRecipe Create(String headCategory, ForgeRecipe.Type type, ForgeConfig config, String pattern, String output, int amount, String... components) {
      String category = IHaveConfig.expandClassCategory(CraftingRecipe.class, headCategory, type.getName());
      return new CraftingRecipe(config, category, pattern, output, amount, components);
   }

   public static CraftingRecipe CreateMain(ForgeConfig config, String pattern, String output, int amount, String... components) {
      return Create(ForgeRecipe.Type.MAIN, config, pattern, output, amount, components);
   }

   public static CraftingRecipe CreateMain(String headCategory, ForgeConfig config, String pattern, String output, int amount, String... components) {
      return Create(headCategory, ForgeRecipe.Type.MAIN, config, pattern, output, amount, components);
   }

   public static CraftingRecipe CreateReserve(ForgeConfig config, String pattern, String output, int amount, String... components) {
      return Create(ForgeRecipe.Type.RESERVE, config, pattern, output, amount, components);
   }

   public static CraftingRecipe CreateReserve(String headCategory, ForgeConfig config, String pattern, String output, int amount, String... components) {
      return Create(headCategory, ForgeRecipe.Type.RESERVE, config, pattern, output, amount, components);
   }

   public CraftingRecipe(String pattern, String output, int amount, String... components) {
      this((ForgeConfig)null, (String)null, pattern, output, amount, components);
   }

   protected CraftingRecipe(ForgeConfig config, String category, String pattern, String output, int amount, String... components) {
      this.components = new ArrayList();
      this.config = config;
      this.pattern = ForgeConfig.getStr(config, category, "pattern", pattern, "Shaped recipe pattern in format <nn nn> or <nnn nnn nnn>.\nWhere 'n' - number of recipe component (from 1 to 9).\nZero value (0) is for empty slot.\nIf pattern is empty, then recipe will be treated as shapeless.");
      this.outputId = ForgeConfig.getUnitId(config, category, "output", UnitId.from(output), "Output unit id.\nId is a basic unit (block or item) identifier in <modId:unitName:meta> format.\nModId can be omitted for vanilla items. Meta can be omitted too if it equals 0.\nUnitName must be lowercase, words separated by '_', words order - from private to common (example: black_iron_ore).\nUnit name will be automatically converted to ore dictionary name with reverse word order (example: oreIronBlack).\nUse '*' char as meta value to specify all possible values.\nTo define multiple block states, you can use block properties instead of meta. The format is <modId:blockName:[prop1=value1, prop2=value2]>\nAlso you may use just ore dictionary name as full id.\n");
      this.amount = ForgeConfig.getInt(config, category, "amount", amount, ForgeBounds.STACK_SIZE.getIntBounds(), "Amount of output units.");
      String[] componentsArray = ForgeConfig.getStrArray(config, category, "components", components, "Recipe components list.\nComponents should be represented by its ids.\nId is a basic unit (block or item) identifier in <modId:unitName:meta> format.\nModId can be omitted for vanilla items. Meta can be omitted too if it equals 0.\nUnitName must be lowercase, words separated by '_', words order - from private to common (example: black_iron_ore).\nUnit name will be automatically converted to ore dictionary name with reverse word order (example: oreIronBlack).\nUse '*' char as meta value to specify all possible values.\nTo define multiple block states, you can use block properties instead of meta. The format is <modId:blockName:[prop1=value1, prop2=value2]>\nAlso you may use just ore dictionary name as full id.\n");
      if (componentsArray != null) {
         for(int i = 0; i < componentsArray.length; ++i) {
            this.components.add(UnitId.from(componentsArray[i]));
         }
      }

   }

   public UnitId getId() {
      return this.outputId;
   }

   public ForgeStack getOutputStack() {
      return this.outputId.getItemState().toStack(this.amount);
   }

   public Object[] getParams() {
      List<Object> objList = new ArrayList();
      if (!this.isShapeless() && this.components != null) {
         String purePattern = this.getPattern(true);
         int gridSize = (int)Math.sqrt((double)purePattern.length());

         assert gridSize * gridSize == purePattern.length();

         for(int i = 0; i < gridSize; ++i) {
            int beginIndex = i * gridSize;
            int endIndex = beginIndex + gridSize;
            objList.add(purePattern.substring(beginIndex, endIndex).replace("0", " "));
         }
      }

      if (this.components != null) {
         for(int i = 0; i < this.components.size(); ++i) {
            if (!this.isShapeless()) {
               objList.add(Character.valueOf(String.valueOf(i + 1).charAt(0)));
            }

            UnitId id = (UnitId)this.components.get(i);
            List<ItemState> states = id.getAllItemStates();
            ItemStack[] stacks = new ItemStack[states.size()];

            for(int j = 0; j < stacks.length; ++j) {
               stacks[j] = ((ItemState)states.get(j)).toItemStack(1);
            }

            Ingredient ingredient = Ingredient.fromStacks(stacks);
            objList.add(ingredient);
         }
      }

      return objList.toArray();
   }

   public String getPattern(boolean noSpaces) {
      return noSpaces && this.pattern != null ? this.pattern.replace(" ", "") : this.pattern;
   }

   public boolean isValid() {
      this.statusDescription = "";
      if (this.outputId.isEmpty()) {
         this.statusDescription = "EMPTY output";
         return false;
      } else if (this.components.isEmpty()) {
         this.statusDescription = "EMPTY component list";
         return false;
      } else {
         int LENGTH_SHORT = 4;
         int LENGHT_FULL = 9;
         if (!this.isShapeless()) {
            int length = this.getPattern(true).length();
            if (length != 4 && length != 9) {
               this.statusDescription = String.format("pattern length is %d, but should be %d or %d", length, Integer.valueOf(4), Integer.valueOf(9));
               return false;
            }

            for(char ch : this.getPattern(true).toCharArray()) {
               String s = String.valueOf(ch);
               int i = 0;

               try {
                  i = Integer.parseInt(s);
               } catch (Exception var11) {
                  this.statusDescription = String.format("pattern has unacceptable char '%s'", s);
                  return false;
               }

               if (i != 0) {
                  --i;
                  if (i < 0 || i >= this.components.size()) {
                     this.statusDescription = String.format("pattern '%d' value refers to not existing component", i);
                     return false;
                  }
               }
            }
         }

         for(UnitId id : this.components) {
            if (id.isEmpty()) {
               this.statusDescription = "component is EMPTY";
               return false;
            }
         }

         if (this.isShapeless()) {
            List<UnitId> list = new ArrayList();

            for(UnitId id : this.components) {
               if (list.contains(id)) {
                  this.statusDescription = "DUPLICATE components";
                  return false;
               }

               list.add(id);
            }
         }

         return true;
      }
   }

   public String toString() {
      return CraftingRecipe.class.getSimpleName() + Args.group(Args.get("status", getStatus(this)), Args.get("pattern", this.pattern), Args.get("output", this.getOutputStack().exists() ? this.getOutputStack() : this.outputId), Args.get("components", this.components));
   }

   public boolean isReadyToRegister() {
      if (!this.getOutputStack().exists()) {
         this.statusDescription = "output stack does not exist!";
         return false;
      } else {
         for(UnitId id : this.components) {
            if (!id.getItemState().exists()) {
               this.statusDescription = "unknown component: " + id;
               return false;
            }
         }

         return true;
      }
   }

   public ForgeConfig getConfig() {
      return this.config;
   }

   public boolean isShapeless() {
      return CommonString.isNullOrEmpty(this.pattern);
   }
}
