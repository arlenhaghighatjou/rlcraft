package com.blamejared.ctgui.client.gui;

import com.blamejared.ctgui.api.CheckButtonRecipe;
import com.blamejared.ctgui.api.GuiBase;
import com.blamejared.ctgui.api.Slider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

@SideOnly(Side.CLIENT)
public class GuiMenu {
   public Pair<CheckButtonRecipe, Slider[]> useOreDict;
   public Pair<CheckButtonRecipe, Slider[]> matchAnyMetadata;
   public Pair<CheckButtonRecipe, Slider[]> anyDamage;
   public Pair<CheckButtonRecipe, Slider[]> onlyDamage;
   public Pair<CheckButtonRecipe, Slider[]> greaterThanEqualDamage;
   public Pair<CheckButtonRecipe, Slider[]> lessThanDamage;
   public Pair<CheckButtonRecipe, Slider[]> betweenDamage;
   public Pair<CheckButtonRecipe, Slider[]> nbt;
   public Pair<CheckButtonRecipe, Slider[]> noReturn;
   private GuiBase parent;
   private int x;
   private int y;
   private List<Pair<CheckButtonRecipe, Slider[]>> buttons = new ArrayList();

   public GuiMenu() {
   }

   public void open(GuiBase parent, int x, int y) {
      this.parent = parent;
      this.x = x;
      this.y = y;
      int buttonCount = 0;
      this.useOreDict = new MutablePair(new CheckButtonRecipe(parent, 1, x, y + 15 * buttonCount++, "Ore Dictionary", false, "oreDictionary"), new Slider[0]);
      this.matchAnyMetadata = new MutablePair(new CheckButtonRecipe(parent, 2, x, y + 15 * buttonCount++, "Any Metadata", false, "anyMetadata"), new Slider[0]);
      this.anyDamage = new MutablePair(new CheckButtonRecipe(parent, 3, x, y + 15 * buttonCount++, "Any Damage", false, "anyDamage"), new Slider[0]);
      this.onlyDamage = new MutablePair(new CheckButtonRecipe(parent, 4, x, y + 15 * buttonCount++, "Only Damage", false, "onlyDamage"), new Slider[0]);
      this.greaterThanEqualDamage = new MutablePair(new CheckButtonRecipe(parent, 6, x, y + 15 * buttonCount++, "X>=Damage", false, "gted"), new Slider[]{new Slider("gted", 10, x - 2, parent.getGuiTop() + parent.getYSize() + 20, parent.getXSize() + 102, 20, "X= ", "", 0.0D, 0.0D, 0.0D, false, true, (slider) -> {
         if (parent.selectedSlot != null && !parent.selectedSlot.getStack().isEmpty()) {
            parent.selectedSlot.getProperties().put(((Slider)slider).getPropertyKey(), Integer.valueOf(slider.getValueInt()));
         }

      })});
      this.lessThanDamage = new MutablePair(new CheckButtonRecipe(parent, 7, x, y + 15 * buttonCount++, "Damage<X", false, "ltd"), new Slider[]{new Slider("ltd", 11, x - 2, parent.getGuiTop() + parent.getYSize() + 20, parent.getXSize() + 102, 20, "X= ", "", 0.0D, 0.0D, 0.0D, false, true, (slider) -> {
         if (parent.selectedSlot != null && !parent.selectedSlot.getStack().isEmpty()) {
            parent.selectedSlot.getProperties().put(((Slider)slider).getPropertyKey(), Integer.valueOf(slider.getValueInt()));
         }

      })});
      this.betweenDamage = new MutablePair(new CheckButtonRecipe(parent, 8, x, y + 15 * buttonCount++, "X<Damage<Y", false, "betweenDamage"), new Slider[]{new Slider("betweenDamageX", 12, x - 2, parent.getGuiTop() + parent.getYSize() + 20, parent.getXSize() + 102, 20, "X= ", "", 0.0D, 0.0D, 0.0D, false, true, (slider) -> {
         if (parent.selectedSlot != null && !parent.selectedSlot.getStack().isEmpty()) {
            parent.selectedSlot.getProperties().put(((Slider)slider).getPropertyKey(), Integer.valueOf(slider.getValueInt()));
         }

      }), new Slider("betweenDamageY", 13, x - 2, parent.getGuiTop() + parent.getYSize() + 44, parent.getXSize() + 102, 20, "Y= ", "", 0.0D, 0.0D, 0.0D, false, true, (slider) -> {
         if (parent.selectedSlot != null && !parent.selectedSlot.getStack().isEmpty()) {
            parent.selectedSlot.getProperties().put(((Slider)slider).getPropertyKey(), Integer.valueOf(slider.getValueInt()));
         }

      })});
      this.nbt = new MutablePair(new CheckButtonRecipe(parent, 8, x, y + 15 * buttonCount++, "NBT", false, "nbt"), new Slider[0]);
      this.noReturn = new MutablePair(new CheckButtonRecipe(parent, 0, x, y + 15 * buttonCount, "No Return", false, "noreturn"), new Slider[0]);
      this.buttons.add(this.useOreDict);
      this.buttons.add(this.matchAnyMetadata);
      this.buttons.add(this.anyDamage);
      this.buttons.add(this.onlyDamage);
      this.buttons.add(this.greaterThanEqualDamage);
      this.buttons.add(this.lessThanDamage);
      this.buttons.add(this.betweenDamage);
      this.buttons.add(this.nbt);
      this.buttons.add(this.noReturn);

      for(Pair<CheckButtonRecipe, Slider[]> but : this.buttons) {
         parent.getButtonList().add(but.getLeft());
      }

      ((CheckButtonRecipe)this.useOreDict.getLeft()).setIncompatible(Arrays.asList((CheckButtonRecipe)this.matchAnyMetadata.getLeft(), (CheckButtonRecipe)this.anyDamage.getLeft(), (CheckButtonRecipe)this.onlyDamage.getLeft(), (CheckButtonRecipe)this.greaterThanEqualDamage.getLeft(), (CheckButtonRecipe)this.lessThanDamage.getLeft(), (CheckButtonRecipe)this.betweenDamage.getLeft(), (CheckButtonRecipe)this.nbt.getLeft()));
      ((CheckButtonRecipe)this.matchAnyMetadata.getLeft()).setIncompatible(Arrays.asList((CheckButtonRecipe)this.useOreDict.getLeft(), (CheckButtonRecipe)this.anyDamage.getLeft(), (CheckButtonRecipe)this.onlyDamage.getLeft(), (CheckButtonRecipe)this.greaterThanEqualDamage.getLeft(), (CheckButtonRecipe)this.lessThanDamage.getLeft(), (CheckButtonRecipe)this.betweenDamage.getLeft()));
      ((CheckButtonRecipe)this.anyDamage.getLeft()).setIncompatible(Arrays.asList((CheckButtonRecipe)this.useOreDict.getLeft(), (CheckButtonRecipe)this.matchAnyMetadata.getLeft(), (CheckButtonRecipe)this.onlyDamage.getLeft(), (CheckButtonRecipe)this.greaterThanEqualDamage.getLeft(), (CheckButtonRecipe)this.lessThanDamage.getLeft(), (CheckButtonRecipe)this.betweenDamage.getLeft()));
      ((CheckButtonRecipe)this.onlyDamage.getLeft()).setIncompatible(Arrays.asList((CheckButtonRecipe)this.useOreDict.getLeft(), (CheckButtonRecipe)this.matchAnyMetadata.getLeft(), (CheckButtonRecipe)this.anyDamage.getLeft(), (CheckButtonRecipe)this.greaterThanEqualDamage.getLeft(), (CheckButtonRecipe)this.lessThanDamage.getLeft(), (CheckButtonRecipe)this.betweenDamage.getLeft()));
      ((CheckButtonRecipe)this.greaterThanEqualDamage.getLeft()).setIncompatible(Arrays.asList((CheckButtonRecipe)this.useOreDict.getLeft(), (CheckButtonRecipe)this.matchAnyMetadata.getLeft(), (CheckButtonRecipe)this.anyDamage.getLeft(), (CheckButtonRecipe)this.onlyDamage.getLeft(), (CheckButtonRecipe)this.lessThanDamage.getLeft(), (CheckButtonRecipe)this.betweenDamage.getLeft()));
      ((CheckButtonRecipe)this.lessThanDamage.getLeft()).setIncompatible(Arrays.asList((CheckButtonRecipe)this.useOreDict.getLeft(), (CheckButtonRecipe)this.matchAnyMetadata.getLeft(), (CheckButtonRecipe)this.anyDamage.getLeft(), (CheckButtonRecipe)this.onlyDamage.getLeft(), (CheckButtonRecipe)this.greaterThanEqualDamage.getLeft(), (CheckButtonRecipe)this.betweenDamage.getLeft()));
      ((CheckButtonRecipe)this.betweenDamage.getLeft()).setIncompatible(Arrays.asList((CheckButtonRecipe)this.useOreDict.getLeft(), (CheckButtonRecipe)this.matchAnyMetadata.getLeft(), (CheckButtonRecipe)this.anyDamage.getLeft(), (CheckButtonRecipe)this.onlyDamage.getLeft(), (CheckButtonRecipe)this.greaterThanEqualDamage.getLeft(), (CheckButtonRecipe)this.lessThanDamage.getLeft()));
      ((CheckButtonRecipe)this.nbt.getLeft()).setIncompatible(Arrays.asList((CheckButtonRecipe)this.useOreDict.getLeft()));
   }

   public void close(GuiBase parent) {
      this.parent = parent;
      if (!parent.getButtonList().isEmpty()) {
         this.parent.getButtonList().remove(this.useOreDict.getLeft());
         this.parent.getButtonList().remove(this.matchAnyMetadata.getLeft());
         this.parent.getButtonList().remove(this.anyDamage.getLeft());
         this.parent.getButtonList().remove(this.onlyDamage.getLeft());
         this.parent.getButtonList().remove(this.greaterThanEqualDamage.getLeft());
         this.parent.getButtonList().remove(this.lessThanDamage.getLeft());
         this.parent.getButtonList().remove(this.betweenDamage.getLeft());
         this.parent.getButtonList().remove(this.noReturn.getLeft());
      }

   }

   public void setState(boolean active) {
      ((CheckButtonRecipe)this.useOreDict.getLeft()).setIsChecked(active);
      ((CheckButtonRecipe)this.matchAnyMetadata.getLeft()).setIsChecked(active);
      ((CheckButtonRecipe)this.anyDamage.getLeft()).setIsChecked(active);
      ((CheckButtonRecipe)this.onlyDamage.getLeft()).setIsChecked(active);
      ((CheckButtonRecipe)this.greaterThanEqualDamage.getLeft()).setIsChecked(active);
      ((CheckButtonRecipe)this.lessThanDamage.getLeft()).setIsChecked(active);
      ((CheckButtonRecipe)this.betweenDamage.getLeft()).setIsChecked(active);
      ((CheckButtonRecipe)this.noReturn.getLeft()).setIsChecked(active);
      ((CheckButtonRecipe)this.nbt.getLeft()).setIsChecked(active);
   }

   public void toggle(boolean active) {
      ((CheckButtonRecipe)this.useOreDict.getLeft()).enabled = active;
      ((CheckButtonRecipe)this.matchAnyMetadata.getLeft()).enabled = active;
      ((CheckButtonRecipe)this.anyDamage.getLeft()).enabled = active;
      ((CheckButtonRecipe)this.onlyDamage.getLeft()).enabled = active;
      ((CheckButtonRecipe)this.greaterThanEqualDamage.getLeft()).enabled = active;
      ((CheckButtonRecipe)this.lessThanDamage.getLeft()).enabled = active;
      ((CheckButtonRecipe)this.betweenDamage.getLeft()).enabled = active;
      ((CheckButtonRecipe)this.noReturn.getLeft()).enabled = active;
      ((CheckButtonRecipe)this.nbt.getLeft()).enabled = active;
   }

   public void getFromMap(Map<String, Boolean> propertyMap, Map<String, Integer> properties) {
      ((CheckButtonRecipe)this.useOreDict.getLeft()).setIsChecked(this.getPropertyFromMap(propertyMap, ((CheckButtonRecipe)this.useOreDict.getLeft()).getPropertyKey()));
      ((CheckButtonRecipe)this.matchAnyMetadata.getLeft()).setIsChecked(this.getPropertyFromMap(propertyMap, ((CheckButtonRecipe)this.matchAnyMetadata.getLeft()).getPropertyKey()));
      ((CheckButtonRecipe)this.anyDamage.getLeft()).setIsChecked(this.getPropertyFromMap(propertyMap, ((CheckButtonRecipe)this.anyDamage.getLeft()).getPropertyKey()));
      ((CheckButtonRecipe)this.onlyDamage.getLeft()).setIsChecked(this.getPropertyFromMap(propertyMap, ((CheckButtonRecipe)this.onlyDamage.getLeft()).getPropertyKey()));
      ((CheckButtonRecipe)this.greaterThanEqualDamage.getLeft()).setIsChecked(this.getPropertyFromMap(propertyMap, ((CheckButtonRecipe)this.greaterThanEqualDamage.getLeft()).getPropertyKey()));
      ((CheckButtonRecipe)this.lessThanDamage.getLeft()).setIsChecked(this.getPropertyFromMap(propertyMap, ((CheckButtonRecipe)this.lessThanDamage.getLeft()).getPropertyKey()));
      ((CheckButtonRecipe)this.betweenDamage.getLeft()).setIsChecked(this.getPropertyFromMap(propertyMap, ((CheckButtonRecipe)this.betweenDamage.getLeft()).getPropertyKey()));
      ((CheckButtonRecipe)this.noReturn.getLeft()).setIsChecked(this.getPropertyFromMap(propertyMap, ((CheckButtonRecipe)this.noReturn.getLeft()).getPropertyKey()));
      ((CheckButtonRecipe)this.nbt.getLeft()).setIsChecked(this.getPropertyFromMap(propertyMap, ((CheckButtonRecipe)this.nbt.getLeft()).getPropertyKey()));
   }

   boolean getPropertyFromMap(Map<String, Boolean> propertyMap, String key) {
      return propertyMap.containsKey(key) ? ((Boolean)propertyMap.get(key)).booleanValue() : false;
   }

   Object getProperty(Map<String, Integer> propertyMap, String key) {
      return propertyMap.containsKey(key) ? propertyMap.get(key) : false;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public Slider[] getSliders(CheckButtonRecipe check) {
      for(Pair<CheckButtonRecipe, Slider[]> pair : this.buttons) {
         if (((CheckButtonRecipe)pair.getLeft()).getPropertyKey().equals(check.getPropertyKey())) {
            return (Slider[])pair.getValue();
         }
      }

      return new Slider[0];
   }

   public List<Pair<CheckButtonRecipe, Slider[]>> getButtons() {
      return this.buttons;
   }
}
