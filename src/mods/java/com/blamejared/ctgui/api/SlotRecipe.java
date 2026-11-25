package com.blamejared.ctgui.api;

import crafttweaker.api.data.IData;
import crafttweaker.mc1120.data.NBTConverter;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class SlotRecipe extends Slot {
   private Map<String, Boolean> propertyMap = new HashMap();
   private Map<String, Integer> properties = new HashMap();

   public SlotRecipe(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
   }

   public ItemStack onTake(EntityPlayer playerIn, ItemStack stack) {
      this.propertyMap.clear();
      this.properties.clear();
      return super.onTake(playerIn, stack);
   }

   public int getSlotStackLimit() {
      return 1;
   }

   public boolean isItemValid(@Nullable ItemStack stack) {
      return true;
   }

   public String getItemString() {
      boolean matchAny = this.getPropertyFromMap("matchNotEmpty");
      boolean metaWildcard = this.getPropertyFromMap("anyMetadata");
      boolean oreDict = this.getPropertyFromMap("oreDictionary");
      if (this.getStack().isEmpty()) {
         return "null";
      } else {
         String stackName = ((ResourceLocation)Item.REGISTRY.getNameForObject(this.getStack().getItem())).toString();
         StringBuilder builder = new StringBuilder("<");
         if (oreDict) {
            int[] ids = OreDictionary.getOreIDs(this.getStack());
            if (ids.length != 0) {
               stackName = "ore:" + OreDictionary.getOreName(ids[0]);
            } else {
               oreDict = false;
            }
         }

         if (matchAny) {
            stackName = "*";
         }

         builder.append(stackName);
         if (!oreDict && !matchAny && (metaWildcard || this.getStack().getItemDamage() != 0)) {
            builder.append(':').append(!metaWildcard && this.getStack().getItemDamage() != 32767 ? this.getStack().getItemDamage() : "*");
         }

         builder.append('>');
         if (this.getPropertyFromMap("anyDamage")) {
            builder.append(".anyDamage()");
         }

         if (this.getPropertyFromMap("onlyDamage")) {
            builder.append(".onlyDamaged()");
         }

         if (this.getPropertyFromMap("gted")) {
            builder.append(".onlyDamageAtLeast(").append(this.getProperty("gted")).append(')');
         }

         if (this.getPropertyFromMap("ltd")) {
            builder.append(".onlyDamageAtMost(").append(this.getProperty("ltd")).append(')');
         }

         if (this.getPropertyFromMap("betweenDamage")) {
            builder.append(".onlyDamageBetween(").append(this.getProperty("betweenDamageX")).append(", ").append(this.getProperty("betweenDamageY")).append(')');
         }

         if (this.getPropertyFromMap("noreturn")) {
            builder.append(".noReturn()");
         }

         if (this.getPropertyFromMap("nbt")) {
            IData data = NBTConverter.from(this.getStack().getTagCompound(), false);
            if (data != null) {
               builder.append(String.format(".withTag(%s)", data.toString()));
               if (!(this instanceof SlotRecipeOutput)) {
                  builder.append(String.format(".onlyWithTag(%s)", data.toString()));
               }
            }
         }

         if (this.getStack().getCount() > 1) {
            builder.append(" * ").append(this.getStack().getCount());
         }

         return builder.toString();
      }
   }

   boolean getPropertyFromMap(String key) {
      return this.propertyMap.containsKey(key) ? ((Boolean)this.propertyMap.get(key)).booleanValue() : false;
   }

   int getProperty(String key) {
      return this.properties.containsKey(key) ? ((Integer)this.properties.get(key)).intValue() : 0;
   }

   public Map<String, Boolean> getPropertyMap() {
      return this.propertyMap;
   }

   public Map<String, Integer> getProperties() {
      return this.properties;
   }
}
