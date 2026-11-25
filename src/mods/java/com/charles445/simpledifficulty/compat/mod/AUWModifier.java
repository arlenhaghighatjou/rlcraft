package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class AUWModifier extends ModifierBase {
   private final String TAG_TLINING = "carrots:ArmorTLining";
   private final String TAGVAL_HORD_INSTRUCTIONS = "_Hord_Instructions";

   public AUWModifier() {
      super("AUW");
   }

   public float getPlayerInfluence(EntityPlayer player) {
      return !ModConfig.server.compatibility.toggles.armorUnderwear ? 0.0F : this.doLinedArmor(player) + this.doGooPak(player);
   }

   private float doLinedArmor(EntityPlayer player) {
      float value = 0.0F;
      value = value + this.checkLinedSlot(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
      value = value + this.checkLinedSlot(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
      value = value + this.checkLinedSlot(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
      value = value + this.checkLinedSlot(player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
      return (float)ModConfig.server.compatibility.auw.linerMultiplier * value;
   }

   private float checkLinedSlot(ItemStack stack) {
      if (!stack.isEmpty() && stack.hasTagCompound()) {
         NBTTagCompound compound = stack.getTagCompound();
         if (compound.hasKey("carrots:ArmorTLining")) {
            String val = compound.getString("carrots:ArmorTLining");
            byte var5 = -1;
            switch(val.hashCode()) {
            case -1741683214:
               if (val.equals("WARMER")) {
                  var5 = 4;
               }
               break;
            case 2074441:
               if (val.equals("COOL")) {
                  var5 = 2;
               }
               break;
            case 2656901:
               if (val.equals("WARM")) {
                  var5 = 3;
               }
               break;
            case 1670198653:
               if (val.equals("COOLEST")) {
                  var5 = 0;
               }
               break;
            case 1842395329:
               if (val.equals("WARMEST")) {
                  var5 = 5;
               }
               break;
            case 1993540022:
               if (val.equals("COOLER")) {
                  var5 = 1;
               }
            }

            switch(var5) {
            case 0:
               return -3.0F;
            case 1:
               return -2.0F;
            case 2:
               return -1.0F;
            case 3:
               return 1.0F;
            case 4:
               return 2.0F;
            case 5:
               return 3.0F;
            default:
               return 0.0F;
            }
         }
      }

      return 0.0F;
   }

   private float doGooPak(EntityPlayer player) {
      float value = 0.0F;
      value = value + this.checkPakSlot(player.getHeldItemOffhand());
      int i = 0;

      while(true) {
         InventoryPlayer var10001 = player.inventory;
         if (i >= InventoryPlayer.getHotbarSize()) {
            float maxPower = (float)ModConfig.server.compatibility.auw.goopakTemperatureModifier * (float)ModConfig.server.compatibility.auw.goopakMaximumActive;
            return MathHelper.clamp(value, -1.0F * maxPower, maxPower);
         }

         value += this.checkPakSlot(player.inventory.getStackInSlot(i));
         ++i;
      }
   }

   private float checkPakSlot(ItemStack stack) {
      float strength = (float)ModConfig.server.compatibility.auw.goopakTemperatureModifier;
      if (!stack.isEmpty() && stack.hasTagCompound()) {
         NBTTagCompound compound = stack.getTagCompound();
         if (compound.hasKey("_Hord_Instructions")) {
            String regName = stack.getItem().getRegistryName().toString();
            if (regName.equals("armorunder:goopak_cool")) {
               return -1.0F * strength;
            }

            if (regName.equals("armorunder:goopak_heat")) {
               return strength;
            }
         }
      }

      return 0.0F;
   }
}
