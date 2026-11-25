package com.teamacronymcoders.contenttweaker.modules.tinkers.traits;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import slimeknights.tconstruct.library.modifiers.ModifierNBT.IntegerNBT;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("mods.contenttweaker.tconstruct.TraitDataRepresentation")
@ZenRegister
@ModOnly("tconstruct")
public class TConTraitDataRepresentation {
   private final IntegerNBT data;

   public TConTraitDataRepresentation(IntegerNBT data) {
      this.data = data;
   }

   @ZenGetter("color")
   public int getColor() {
      return this.data.color;
   }

   @ZenSetter("color")
   public void setColor(int color) {
      this.data.color = color;
   }

   @ZenGetter("current")
   public int getCurrent() {
      return this.data.current;
   }

   @ZenSetter("current")
   public void setCurrent(int current) {
      this.data.current = current;
   }

   @ZenGetter("extraInfo")
   public String getExtraInfo() {
      return this.data.extraInfo;
   }

   @ZenSetter("extraInfo")
   public void setExtraInfo(String extraInfo) {
      this.data.extraInfo = extraInfo;
   }

   @ZenGetter("identifier")
   public String getIdentifier() {
      return this.data.identifier;
   }

   @ZenSetter("identifier")
   public void setIdentifier(String identifier) {
      this.data.identifier = identifier;
   }

   @ZenGetter("level")
   public int getLevel() {
      return this.data.level;
   }

   @ZenSetter("level")
   public void setLevel(int level) {
      this.data.level = level;
   }

   @ZenGetter("max")
   public int getMax() {
      return this.data.max;
   }

   @ZenSetter("max")
   public void setMax(int max) {
      this.data.max = max;
   }

   @ZenGetter("info")
   @ZenMethod
   public String calcInfo() {
      return this.data.calcInfo();
   }

   @ZenGetter("colorString")
   @ZenMethod
   public String getColorString() {
      return this.data.getColorString();
   }
}
