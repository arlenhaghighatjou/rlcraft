package com.endertech.minecraft.forge.materials;

import com.endertech.common.Args;
import com.endertech.minecraft.forge.ForgeRecipe;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.data.Names;
import com.endertech.minecraft.forge.items.ForgeStack;
import com.endertech.minecraft.forge.units.UnitId;

public class SmeltingRecipe extends ForgeRecipe implements ISmeltable {
   public final UnitId inputId;
   public final UnitId outputId;
   public final SmeltingData smeltingData;
   protected final ForgeConfig config;

   public static SmeltingRecipe Create(ForgeRecipe.Type type, ForgeConfig config, String input, String output, int amount, float xp) {
      String category = Names.dotted().join(SmeltingRecipe.class.getSimpleName(), type.getName());
      return new SmeltingRecipe(config, category, input, output, amount, xp);
   }

   public static SmeltingRecipe CreateMain(ForgeConfig config, String input, String output, int amount, float xp) {
      return Create(ForgeRecipe.Type.MAIN, config, input, output, amount, xp);
   }

   public SmeltingRecipe(ForgeConfig config, String category, String input, String output, int amount, float xp) {
      this.config = config;
      this.inputId = ForgeConfig.getUnitId(config, category, "inputId", UnitId.from(input), "Input unit id.\nId is a basic unit (block or item) identifier in <modId:unitName:meta> format.\nModId can be omitted for vanilla items. Meta can be omitted too if it equals 0.\nUnitName must be lowercase, words separated by '_', words order - from private to common (example: black_iron_ore).\nUnit name will be automatically converted to ore dictionary name with reverse word order (example: oreIronBlack).\nUse '*' char as meta value to specify all possible values.\nTo define multiple block states, you can use block properties instead of meta. The format is <modId:blockName:[prop1=value1, prop2=value2]>\nAlso you may use just ore dictionary name as full id.\n");
      this.outputId = ForgeConfig.getUnitId(config, category, "outputId", UnitId.from(output), "Output unit id.\nId is a basic unit (block or item) identifier in <modId:unitName:meta> format.\nModId can be omitted for vanilla items. Meta can be omitted too if it equals 0.\nUnitName must be lowercase, words separated by '_', words order - from private to common (example: black_iron_ore).\nUnit name will be automatically converted to ore dictionary name with reverse word order (example: oreIronBlack).\nUse '*' char as meta value to specify all possible values.\nTo define multiple block states, you can use block properties instead of meta. The format is <modId:blockName:[prop1=value1, prop2=value2]>\nAlso you may use just ore dictionary name as full id.\n");
      this.smeltingData = new SmeltingData(config, category, amount, xp);
   }

   public SmeltingRecipe(String inputName, String outputName, int amount, float xp) {
      this((ForgeConfig)null, (String)null, inputName, outputName, amount, xp);
   }

   public boolean isValid() {
      if (this.inputId.isEmpty()) {
         this.statusDescription = "EMPTY input";
         return false;
      } else if (this.outputId.isEmpty()) {
         this.statusDescription = "EMPTY output";
         return false;
      } else if (this.getSmeltingData().getAmount() <= 0) {
         this.statusDescription = "Wrong amount";
         return false;
      } else {
         return true;
      }
   }

   public ForgeStack getInputStack() {
      return this.inputId.getItemState().toStack(1);
   }

   public ForgeStack getOutputStack() {
      return this.outputId.getItemState().toStack(this.getSmeltingData().getAmount());
   }

   public SmeltingData getSmeltingData() {
      return this.smeltingData;
   }

   public String toString() {
      return SmeltingRecipe.class.getSimpleName() + Args.group(Args.get("status", getStatus(this)), Args.get("input", this.getInputStack().exists() ? this.getInputStack() : this.inputId), Args.get("output", this.getOutputStack().exists() ? this.getOutputStack() : this.outputId), Args.get("smeltingData", this.getSmeltingData()));
   }

   public boolean isReadyToRegister() {
      return this.inputId.getItemState().exists() && this.outputId.getItemState().exists();
   }

   public ForgeConfig getConfig() {
      return this.config;
   }
}
