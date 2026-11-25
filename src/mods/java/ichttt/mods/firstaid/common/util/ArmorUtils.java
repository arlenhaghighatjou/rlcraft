package ichttt.mods.firstaid.common.util;

import com.google.common.math.DoubleMath;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nonnull;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.fml.common.Loader;

public class ArmorUtils {
   public static final boolean QUALITY_TOOLS_PRESENT = Loader.isModLoaded("qualitytools");

   public ArmorUtils() {
   }

   public static double getArmor(ItemStack stack, EntityEquipmentSlot slot, boolean includeQualityTools) {
      double attributeVal = getValueFromAttributes(SharedMonsterAttributes.ARMOR, slot, stack);
      if (includeQualityTools) {
         attributeVal += getValueFromQualityTools(SharedMonsterAttributes.ARMOR, stack);
      }

      return attributeVal;
   }

   public static double getArmorToughness(ItemStack stack, EntityEquipmentSlot slot, boolean includeQualityTools) {
      double attributeVal = getValueFromAttributes(SharedMonsterAttributes.ARMOR_TOUGHNESS, slot, stack);
      if (includeQualityTools) {
         attributeVal += getValueFromQualityTools(SharedMonsterAttributes.ARMOR_TOUGHNESS, stack);
      }

      return attributeVal;
   }

   public static double applyArmorModifier(EntityEquipmentSlot slot, double rawArmor) {
      if (rawArmor <= 0.0D) {
         return 0.0D;
      } else {
         rawArmor = rawArmor * getArmorMultiplier(slot);
         rawArmor = rawArmor + getArmorOffset(slot);
         return rawArmor;
      }
   }

   public static double applyToughnessModifier(EntityEquipmentSlot slot, double rawToughness) {
      if (rawToughness <= 0.0D) {
         return 0.0D;
      } else {
         rawToughness = rawToughness * getToughnessMultiplier(slot);
         rawToughness = rawToughness + getToughnessOffset(slot);
         return rawToughness;
      }
   }

   public static double getArmorMultiplier(EntityEquipmentSlot slot) {
      FirstAidConfig.LocationalArmor.Armor config = FirstAidConfig.locationalArmor.armor;
      switch(slot) {
      case HEAD:
         return config.headArmorMultiplier;
      case CHEST:
         return config.chestArmorMultiplier;
      case LEGS:
         return config.legsArmorMultiplier;
      case FEET:
         return config.feetArmorMultiplier;
      default:
         throw new IllegalArgumentException("Invalid slot " + slot);
      }
   }

   private static double getArmorOffset(EntityEquipmentSlot slot) {
      FirstAidConfig.LocationalArmor.Armor config = FirstAidConfig.locationalArmor.armor;
      switch(slot) {
      case HEAD:
         return config.headArmorOffset;
      case CHEST:
         return config.chestArmorOffset;
      case LEGS:
         return config.legsArmorOffset;
      case FEET:
         return config.feetArmorOffset;
      default:
         throw new IllegalArgumentException("Invalid slot " + slot);
      }
   }

   public static double getToughnessMultiplier(EntityEquipmentSlot slot) {
      FirstAidConfig.LocationalArmor.Toughness config = FirstAidConfig.locationalArmor.toughness;
      switch(slot) {
      case HEAD:
         return config.headToughnessMultiplier;
      case CHEST:
         return config.chestToughnessMultiplier;
      case LEGS:
         return config.legsToughnessMultiplier;
      case FEET:
         return config.feetToughnessMultiplier;
      default:
         throw new IllegalArgumentException("Invalid slot " + slot);
      }
   }

   private static double getToughnessOffset(EntityEquipmentSlot slot) {
      FirstAidConfig.LocationalArmor.Toughness config = FirstAidConfig.locationalArmor.toughness;
      switch(slot) {
      case HEAD:
         return config.headToughnessOffset;
      case CHEST:
         return config.chestToughnessOffset;
      case LEGS:
         return config.legsToughnessOffset;
      case FEET:
         return config.feetToughnessOffset;
      default:
         throw new IllegalArgumentException("Invalid slot " + slot);
      }
   }

   private static double getValueFromAttributes(IAttribute attribute, EntityEquipmentSlot slot, ItemStack stack) {
      return stack.getItem().getAttributeModifiers(slot, stack).get(attribute.getName()).stream().mapToDouble(AttributeModifier::getAmount).sum();
   }

   public static double getValueFromQualityTools(IAttribute attribute, ItemStack stack) {
      double ret = 0.0D;
      if (QUALITY_TOOLS_PRESENT) {
         NBTTagCompound tagCompound = stack.getTagCompound();
         if (tagCompound != null && tagCompound.hasKey("Quality", 10)) {
            NBTTagCompound quality = tagCompound.getCompoundTag("Quality");
            if (quality.hasKey("AttributeModifiers", 9)) {
               for(NBTBase attributeModifierRaw : quality.getTagList("AttributeModifiers", 10)) {
                  if (attributeModifierRaw instanceof NBTTagCompound) {
                     NBTTagCompound attributeModifier = (NBTTagCompound)attributeModifierRaw;
                     String attributeName = attributeModifier.getString("AttributeName");
                     if (attributeName.equalsIgnoreCase(attribute.getName())) {
                        ret += attributeModifier.getDouble("Amount");
                     }
                  }
               }
            }
         }
      }

      return ret;
   }

   private static double getGlobalRestAttribute(EntityPlayer player, IAttribute attribute) {
      double sumOfAllAttributes = 0.0D;

      for(EntityEquipmentSlot slot : CommonUtils.ARMOR_SLOTS) {
         ItemStack otherStack = player.getItemStackFromSlot(slot);
         sumOfAllAttributes = sumOfAllAttributes + getValueFromAttributes(attribute, slot, otherStack);
         sumOfAllAttributes = sumOfAllAttributes + getValueFromQualityTools(attribute, otherStack);
      }

      double all = player.getEntityAttribute(attribute).getAttributeValue();
      if (!DoubleMath.fuzzyEquals(sumOfAllAttributes, all, 0.001D)) {
         double diff = all - sumOfAllAttributes;
         if (FirstAidConfig.debug) {
            FirstAid.LOGGER.info("Attribute value for {} does not match sum! Diff is {}, distributing to all!", attribute.getName(), Double.valueOf(diff));
         }

         return diff;
      } else {
         return 0.0D;
      }
   }

   public static float applyArmor(@Nonnull EntityPlayer entity, @Nonnull ItemStack itemStack, @Nonnull DamageSource source, double damage, @Nonnull EntityEquipmentSlot slot) {
      NonNullList<ItemStack> inventory = entity.inventory.armorInventory;
      double totalArmor = 0.0D;
      double totalToughness = 0.0D;
      Item item = itemStack.getItem();
      ArmorProperties prop = null;
      boolean unblockable = source.isUnblockable();
      if (unblockable && item instanceof ISpecialArmor) {
         unblockable = ((ISpecialArmor)item).handleUnblockableDamage(entity, itemStack, source, damage, slot.getIndex());
      }

      if (unblockable) {
         return (float)damage;
      } else {
         if (item instanceof ISpecialArmor) {
            ISpecialArmor armor = (ISpecialArmor)item;
            prop = armor.getProperties(entity, itemStack, source, damage, slot.getIndex()).copy();
            totalArmor += prop.Armor;
            totalToughness += prop.Toughness;
         } else if (item instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)item;
            prop = new ArmorProperties(0, 0.0D, Integer.MAX_VALUE);
            prop.Armor = (double)armor.damageReduceAmount;
            prop.Toughness = (double)armor.toughness;
         }

         if (item instanceof ItemArmor) {
            totalArmor += getArmor(itemStack, slot, true);
            totalToughness += getArmorToughness(itemStack, slot, true);
         }

         if (prop != null) {
            totalArmor = applyArmorModifier(slot, totalArmor);
            totalToughness = applyToughnessModifier(slot, totalToughness);
            prop.Slot = slot.getIndex();
            double ratio = prop.AbsorbRatio * getArmorMultiplier(slot);
            double absorb = damage * ratio;
            if (absorb > 0.0D) {
               ItemStack stack = (ItemStack)inventory.get(prop.Slot);
               int itemDamage = (int)Math.max(1.0D, absorb);
               if (stack.getItem() instanceof ISpecialArmor) {
                  ((ISpecialArmor)stack.getItem()).damageArmor(entity, stack, source, itemDamage, prop.Slot);
               } else {
                  stack.damageItem(itemDamage, entity);
               }
            }

            damage -= damage * ratio;
         }

         totalArmor = totalArmor + getGlobalRestAttribute(entity, SharedMonsterAttributes.ARMOR);
         totalToughness = totalToughness + getGlobalRestAttribute(entity, SharedMonsterAttributes.ARMOR_TOUGHNESS);
         if (damage > 0.0D && (totalArmor > 0.0D || totalToughness > 0.0D)) {
            double armorDamage = Math.max(1.0D, damage);
            if (item instanceof ItemArmor) {
               itemStack.damageItem((int)armorDamage, entity);
            }

            damage = (double)CombatRules.getDamageAfterAbsorb((float)damage, (float)totalArmor, (float)totalToughness);
         }

         return (float)damage;
      }
   }

   public static float applyGlobalPotionModifiers(EntityPlayer player, DamageSource source, float damage) {
      if (source.isDamageAbsolute()) {
         return damage;
      } else {
         if (player.isPotionActive(MobEffects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD) {
            int i = (player.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * FirstAidConfig.externalHealing.resistanceReductionPercentPerLevel;
            int j = 100 - i;
            float f = damage * (float)j;
            damage = f / 100.0F;
         }

         return damage <= 0.0F ? 0.0F : damage;
      }
   }

   public static float applyEnchantmentModifiers(EntityPlayer player, EntityEquipmentSlot slot, DamageSource source, float damage) {
      if (source.isDamageAbsolute()) {
         return damage;
      } else {
         int k;
         if (FirstAidConfig.enchantmentHandling.armorEnchantmentMode == FirstAidConfig.EnchantmentHandling.ArmorEnchantmentMode.LOCAL_ENCHANTMENTS) {
            ItemStack itemStackFromSlot = player.getItemStackFromSlot(slot);
            k = calculateEnchantmentModifierLocal(itemStackFromSlot, source);
         } else {
            if (FirstAidConfig.enchantmentHandling.armorEnchantmentMode != FirstAidConfig.EnchantmentHandling.ArmorEnchantmentMode.GLOBAL_ENCHANTMENTS) {
               throw new RuntimeException("What dark magic is " + FirstAidConfig.enchantmentHandling.armorEnchantmentMode);
            }

            k = EnchantmentHelper.getEnchantmentModifierDamage(player.getArmorInventoryList(), source);
         }

         if (k > 0) {
            damage = CombatRules.getDamageAfterMagicAbsorb(damage, (float)k);
         }

         return damage;
      }
   }

   private static int calculateEnchantmentModifierLocal(ItemStack stack, DamageSource source) {
      int ret = 0;
      if (!stack.isEmpty()) {
         NBTTagList nbttaglist = stack.getEnchantmentTagList();

         for(int i = 0; i < nbttaglist.tagCount(); ++i) {
            int j = nbttaglist.getCompoundTagAt(i).getShort("id");
            int k = nbttaglist.getCompoundTagAt(i).getShort("lvl");
            if (Enchantment.getEnchantmentByID(j) != null) {
               Enchantment enchantment = Enchantment.getEnchantmentByID(j);
               int val = enchantment.calcModifierDamage(k, source);
               String[] resourceLocation = FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation;
               int[] multiplierOverride = FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride;
               String enchantRlAsString = enchantment.getRegistryName().toString();
               int multiplier = FirstAidConfig.enchantmentHandling.enchantmentMultiplier;
               if (FirstAidConfig.debug) {
                  FirstAid.LOGGER.info("Searching for enchantment multiplier override for {}, base is {}", enchantRlAsString, Integer.valueOf(multiplier));
               }

               for(int l = 0; l < Math.min(resourceLocation.length, multiplierOverride.length); ++l) {
                  String s = resourceLocation[l];
                  if (s.equals(enchantRlAsString)) {
                     multiplier = multiplierOverride[l];
                     if (FirstAidConfig.debug) {
                        FirstAid.LOGGER.info("Found enchantment multiplier override for {}, new value is {}", enchantRlAsString, Integer.valueOf(multiplier));
                     }
                     break;
                  }
               }

               ret += val * multiplier;
            }
         }
      }

      return ret;
   }

   static {
      if (QUALITY_TOOLS_PRESENT) {
         FirstAid.LOGGER.info("Quality Tools mod present. Enabling compat...");
      }

   }
}
