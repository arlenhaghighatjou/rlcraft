package com.oblivioussp.spartanweaponry.api.weaponproperty;

import com.oblivioussp.spartanweaponry.api.SpartanWeaponryAPI;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WeaponProperty {
   protected String type;
   protected String modId;
   protected int level;
   protected float magnitude;

   public WeaponProperty(String propType, String propModId, int propLevel, float propMagnitude) {
      this.type = propType;
      this.modId = propModId;
      this.level = propLevel;
      this.magnitude = propMagnitude;
   }

   public WeaponProperty(String propType, String propModId, int propLevel) {
      this(propType, propModId, propLevel, 0.0F);
   }

   public WeaponProperty(String propType, String propModId, float propMagnitude) {
      this(propType, propModId, 0, propMagnitude);
   }

   public WeaponProperty(String propType, String propModId) {
      this(propType, propModId, 0);
   }

   public String toString() {
      return String.format("WeaponProperty{Type: %s:%s - Level: %d - Magnitude: %f}", this.modId, this.type, this.level, this.magnitude);
   }

   public String getType() {
      return this.type;
   }

   public float getMagnitude() {
      return this.magnitude;
   }

   public void setMagnitude(float value) {
      this.magnitude = value;
   }

   public IPropertyCallback getCallback() {
      return null;
   }

   public WeaponProperty.PropertyQuality getQuality() {
      return WeaponProperty.PropertyQuality.POSITIVE;
   }

   public int getLevel() {
      return this.level;
   }

   @SideOnly(Side.CLIENT)
   public final void addTooltip(ItemStack stack, List<String> tooltip, boolean isShiftPressed) {
      this.addTooltipTitle(stack, tooltip);
      if (SpartanWeaponryAPI.internalHandler.hasTranslateKey(this.type + ".desc", "tooltip", this.modId) && isShiftPressed) {
         this.addTooltipDescription(stack, tooltip);
      }

   }

   @SideOnly(Side.CLIENT)
   protected void addTooltipTitle(ItemStack stack, List<String> tooltip) {
      if (this.level == 0) {
         tooltip.add(this.getQuality().getFormatting() + "- " + SpartanWeaponryAPI.internalHandler.translateString(this.type, "tooltip", this.modId));
      } else {
         tooltip.add(this.getQuality().getFormatting() + "- " + SpartanWeaponryAPI.internalHandler.translateFormattedString(this.type, "tooltip", this.modId, I18n.translateToLocal("enchantment.level." + this.level)));
      }

   }

   @SideOnly(Side.CLIENT)
   protected void addTooltipDescription(ItemStack stack, List<String> tooltip) {
      tooltip.add(TextFormatting.GRAY + "" + TextFormatting.ITALIC + "  " + SpartanWeaponryAPI.internalHandler.translateString(this.type + ".desc", "tooltip", this.modId));
   }

   public static enum PropertyQuality {
      POSITIVE(TextFormatting.GREEN),
      NEUTRAL(TextFormatting.YELLOW),
      NEGATIVE(TextFormatting.RED);

      private TextFormatting formatting;

      private PropertyQuality(TextFormatting formatting) {
         this.formatting = formatting;
      }

      public TextFormatting getFormatting() {
         return this.formatting;
      }
   }
}
