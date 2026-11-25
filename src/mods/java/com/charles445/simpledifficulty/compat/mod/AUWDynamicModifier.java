package com.charles445.simpledifficulty.compat.mod;

import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierDynamicBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class AUWDynamicModifier extends ModifierDynamicBase {
   private final String TAG_XLINING = "carrots:ArmorXLining";
   private final String TAG_XLINING_VALUE = "carrots:ArmorXLining_value";
   private final String TAGVAL_OZZY = "TEMPERATURE_REGULATOR";

   public AUWDynamicModifier() {
      super("AUWDynamic");
   }

   public float applyDynamicPlayerInfluence(EntityPlayer player, float currentTemperature) {
      if (!ModConfig.server.compatibility.toggles.armorUnderwear) {
         return currentTemperature;
      } else {
         ItemStack chestplate = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
         ItemStack leggings = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
         boolean hasChest = false;
         boolean hasLeggings = false;
         int valueChest = 0;
         int valueLeggings = 0;
         if (!leggings.isEmpty() && leggings.hasTagCompound()) {
            NBTTagCompound compound = leggings.getTagCompound();
            String xLiner = compound.getString("carrots:ArmorXLining");
            if (xLiner.equals("TEMPERATURE_REGULATOR")) {
               hasLeggings = true;
               if (compound.hasKey("carrots:ArmorXLining_value")) {
                  valueLeggings = compound.getInteger("carrots:ArmorXLining_value");
               }
            }
         }

         if (!chestplate.isEmpty() && chestplate.hasTagCompound()) {
            NBTTagCompound compound = chestplate.getTagCompound();
            String xLiner = compound.getString("carrots:ArmorXLining");
            if (xLiner.equals("TEMPERATURE_REGULATOR")) {
               hasChest = true;
               if (compound.hasKey("carrots:ArmorXLining_value")) {
                  valueChest = compound.getInteger("carrots:ArmorXLining_value");
               }
            }
         }

         if (!hasChest && !hasLeggings) {
            return currentTemperature;
         } else {
            float baseStretch = (float)ModConfig.server.compatibility.auw.ozzyBaseRange;
            float valueStretchModifier = (float)ModConfig.server.compatibility.auw.ozzyExtraRange;
            float valueStretchDefault = valueStretchModifier / 3.0F;
            float lowStretch = 0.0F;
            float highStretch = 0.0F;
            if (hasChest) {
               lowStretch -= baseStretch;
               highStretch += baseStretch;
               switch(valueChest) {
               case -1:
                  lowStretch -= valueStretchModifier;
                  break;
               case 1:
                  highStretch += valueStretchModifier;
                  break;
               default:
                  lowStretch -= valueStretchDefault;
                  highStretch += valueStretchDefault;
               }
            }

            if (hasLeggings) {
               lowStretch -= baseStretch;
               highStretch += baseStretch;
               switch(valueLeggings) {
               case -1:
                  lowStretch -= valueStretchModifier;
                  break;
               case 1:
                  highStretch += valueStretchModifier;
                  break;
               default:
                  lowStretch -= valueStretchDefault;
                  highStretch += valueStretchDefault;
               }
            }

            float distanceToDefault = MathHelper.clamp(this.defaultTemperature - currentTemperature, lowStretch, highStretch);
            return currentTemperature + distanceToDefault;
         }
      }
   }
}
