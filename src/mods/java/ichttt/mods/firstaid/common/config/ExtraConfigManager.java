package ichttt.mods.firstaid.common.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ExtraConfigManager {
   private static List<String> toDeleteEntries = new ArrayList();

   public ExtraConfigManager() {
   }

   public static void scheduleDelete(String name) {
      if (toDeleteEntries != null) {
         toDeleteEntries.add(name);
      } else {
         throw new IllegalStateException("Already cleaned up configs");
      }
   }

   public static void postProcessConfigs() {
      if (!toDeleteEntries.isEmpty()) {
         Configuration config = ConfigManager.getConfiguration("firstaid", "firstaid");
         if (config == null) {
            FirstAid.LOGGER.warn("Skipping post processing due to null config");
         } else {
            for(String s : toDeleteEntries) {
               String[] path = s.split("\\.");
               String catString = "general" + (path.length > 1 ? "." + s.substring(0, s.length() - (path[path.length - 1].length() + 1)) : "");
               if (config.hasCategory(catString)) {
                  ConfigCategory cat = config.getCategory(catString);
                  if (cat.containsKey(path[path.length - 1])) {
                     FirstAid.LOGGER.info("Removing prop " + s);
                     cat.remove(path[path.length - 1]);
                  }
               } else {
                  FirstAid.LOGGER.warn("Unable to find config category {} for removal of old config options", catString);
               }
            }

            if (config.hasChanged()) {
               config.save();
            }

            toDeleteEntries = null;
         }
      }
   }

   public static List<ConfigEntry<ExtraConfig.Sync>> getSyncedEntries() {
      Builder<ConfigEntry<ExtraConfig.Sync>> listBuilder = ImmutableList.builder();
      listBuilder.add(new ConfigEntry<ExtraConfig.Sync>(() -> FirstAidConfig.damageSystem, (buf) -> {
         buf.writeInt(FirstAidConfig.damageSystem.maxHealthHead);
         buf.writeBoolean(FirstAidConfig.damageSystem.causeDeathHead);
         buf.writeInt(FirstAidConfig.damageSystem.maxHealthLeftArm);
         buf.writeInt(FirstAidConfig.damageSystem.maxHealthLeftLeg);
         buf.writeInt(FirstAidConfig.damageSystem.maxHealthLeftFoot);
         buf.writeInt(FirstAidConfig.damageSystem.maxHealthBody);
         buf.writeBoolean(FirstAidConfig.damageSystem.causeDeathBody);
         buf.writeInt(FirstAidConfig.damageSystem.maxHealthRightArm);
         buf.writeInt(FirstAidConfig.damageSystem.maxHealthRightLeg);
         buf.writeInt(FirstAidConfig.damageSystem.maxHealthRightFoot);
      }, (buf) -> {
         FirstAidConfig.damageSystem.maxHealthHead = buf.readInt();
         FirstAidConfig.damageSystem.causeDeathHead = buf.readBoolean();
         FirstAidConfig.damageSystem.maxHealthLeftArm = buf.readInt();
         FirstAidConfig.damageSystem.maxHealthLeftLeg = buf.readInt();
         FirstAidConfig.damageSystem.maxHealthLeftFoot = buf.readInt();
         FirstAidConfig.damageSystem.maxHealthBody = buf.readInt();
         FirstAidConfig.damageSystem.causeDeathBody = buf.readBoolean();
         FirstAidConfig.damageSystem.maxHealthRightArm = buf.readInt();
         FirstAidConfig.damageSystem.maxHealthRightLeg = buf.readInt();
         FirstAidConfig.damageSystem.maxHealthRightFoot = buf.readInt();
      }, true, new UniqueProperty("damageSystem", "firstaid.config.damagesystem", UniqueProperty.Type.CATEGORY)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Sync>(() -> FirstAidConfig.internalHealing, (buf) -> {
         buf.writeInt(FirstAidConfig.internalHealing.bandage.totalHeals);
         buf.writeInt(FirstAidConfig.internalHealing.bandage.secondsPerHeal);
         buf.writeInt(FirstAidConfig.internalHealing.bandage.applyTime);
         buf.writeInt(FirstAidConfig.internalHealing.plaster.totalHeals);
         buf.writeInt(FirstAidConfig.internalHealing.plaster.secondsPerHeal);
         buf.writeInt(FirstAidConfig.internalHealing.plaster.applyTime);
      }, (buf) -> {
         FirstAidConfig.internalHealing.bandage.totalHeals = buf.readInt();
         FirstAidConfig.internalHealing.bandage.secondsPerHeal = buf.readInt();
         FirstAidConfig.internalHealing.bandage.applyTime = buf.readInt();
         FirstAidConfig.internalHealing.plaster.totalHeals = buf.readInt();
         FirstAidConfig.internalHealing.plaster.secondsPerHeal = buf.readInt();
         FirstAidConfig.internalHealing.plaster.applyTime = buf.readInt();
      }, true, new UniqueProperty("internalHealing", "firstaid.config.internalhealing", UniqueProperty.Type.CATEGORY)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Sync>(() -> FirstAidConfig.externalHealing, (buf) -> {
         buf.writeBoolean(FirstAidConfig.externalHealing.allowNaturalRegeneration);
         buf.writeBoolean(FirstAidConfig.externalHealing.allowOtherHealingItems);
         buf.writeDouble(FirstAidConfig.externalHealing.sleepHealPercentage);
         buf.writeDouble(FirstAidConfig.externalHealing.otherRegenMultiplier);
         buf.writeDouble(FirstAidConfig.externalHealing.naturalRegenMultiplier);
         buf.writeInt(FirstAidConfig.externalHealing.resistanceReductionPercentPerLevel);
      }, (buf) -> {
         FirstAidConfig.externalHealing.allowNaturalRegeneration = buf.readBoolean();
         FirstAidConfig.externalHealing.allowOtherHealingItems = buf.readBoolean();
         FirstAidConfig.externalHealing.sleepHealPercentage = buf.readDouble();
         FirstAidConfig.externalHealing.otherRegenMultiplier = buf.readDouble();
         FirstAidConfig.externalHealing.naturalRegenMultiplier = buf.readDouble();
         FirstAidConfig.externalHealing.resistanceReductionPercentPerLevel = buf.readInt();
      }, true, new UniqueProperty("externalHealing", "firstaid.config.externalhealing", UniqueProperty.Type.CATEGORY)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Sync>(() -> FirstAidConfig.locationalArmor, (buf) -> {
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.headArmorMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.chestArmorMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.legsArmorMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.feetArmorMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.headArmorOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.chestArmorOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.legsArmorOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.feetArmorOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.headToughnessMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.chestToughnessMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.legsToughnessMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.feetToughnessMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.headToughnessOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.chestToughnessOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.legsToughnessOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.feetToughnessOffset);
      }, (buf) -> {
         FirstAidConfig.locationalArmor.armor.headArmorMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.chestArmorMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.legsArmorMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.feetArmorMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.headArmorOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.chestArmorOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.legsArmorOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.feetArmorOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.headToughnessMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.chestToughnessMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.legsToughnessMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.feetToughnessMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.headToughnessOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.chestToughnessOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.legsToughnessOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.feetToughnessOffset = buf.readDouble();
      }, true, new UniqueProperty("locationalArmor", "firstaid.config.locationalarmor", UniqueProperty.Type.CATEGORY)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Sync>(() -> FirstAidConfig.enchantmentHandling, (buf) -> {
         buf.writeInt(FirstAidConfig.enchantmentHandling.enchantmentMultiplier);
         buf.writeByte(FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation.length);
         for (int i = 0; i < FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation.length; ++i) {
            ByteBufUtils.writeUTF8String(buf, FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation[i]);
         }
         buf.writeByte(FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride.length);
         for (int i = 0; i < FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride.length; ++i) {
            buf.writeInt(FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride[i]);
         }
      }, (buf) -> {
         FirstAidConfig.enchantmentHandling.enchantmentMultiplier = buf.readInt();
         {
            byte len = buf.readByte();
            String[] arr = new String[len];
            for (int i = 0; i < len; ++i) {
               arr[i] = ByteBufUtils.readUTF8String(buf);
            }
            FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation = arr;
         }
         {
            byte len = buf.readByte();
            int[] arr = new int[len];
            for (int i = 0; i < len; ++i) {
               arr[i] = buf.readInt();
            }
            FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride = arr;
         }
      }, true, new UniqueProperty("enchantmentHandling", "firstaid.config.enchantmenthandling", UniqueProperty.Type.CATEGORY)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Sync>(() -> FirstAidConfig.scaleMaxHealth, (buf) -> {
         buf.writeBoolean(FirstAidConfig.scaleMaxHealth);
      }, (buf) -> {
         FirstAidConfig.scaleMaxHealth = buf.readBoolean();
      }, true, new UniqueProperty("scaleMaxHealth", "firstaid.config.scalemaxhealth", UniqueProperty.Type.BOOLEAN)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Sync>(() -> FirstAidConfig.capMaxHealth, (buf) -> {
         buf.writeBoolean(FirstAidConfig.capMaxHealth);
      }, (buf) -> {
         FirstAidConfig.capMaxHealth = buf.readBoolean();
      }, true, new UniqueProperty("capMaxHealth", "firstaid.config.capmaxhealth", UniqueProperty.Type.BOOLEAN)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Sync>(() -> FirstAidConfig.vanillaHealthCalculation, (buf) -> {
      }, (buf) -> {
      }, true, new UniqueProperty("vanillaHealthCalculation", "firstaid.general.vanillahealthcalculation", UniqueProperty.Type.STRING)));
      List<ConfigEntry<ExtraConfig.Sync>> list = listBuilder.build();
      FirstAid.LOGGER.debug("Found {} annotations of the type {} for the {}", Integer.valueOf(list.size()), ExtraConfig.Sync.class, FirstAidConfig.class);
      return list;
   }

   public static List<ConfigEntry<ExtraConfig.Advanced>> getAdvancedEntries() {
      Builder<ConfigEntry<ExtraConfig.Advanced>> listBuilder = ImmutableList.builder();
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.overlay.xOffset, (buf) -> {
         buf.writeInt(FirstAidConfig.overlay.xOffset);
      }, (buf) -> {
         FirstAidConfig.overlay.xOffset = buf.readInt();
      }, false, new UniqueProperty("xOffset", "firstaid.config.xoffset", UniqueProperty.Type.INTEGER)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.overlay.yOffset, (buf) -> {
         buf.writeInt(FirstAidConfig.overlay.yOffset);
      }, (buf) -> {
         FirstAidConfig.overlay.yOffset = buf.readInt();
      }, false, new UniqueProperty("yOffset", "firstaid.config.yoffset", UniqueProperty.Type.INTEGER)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.overlay.alpha, (buf) -> {
         buf.writeInt(FirstAidConfig.overlay.alpha);
      }, (buf) -> {
         FirstAidConfig.overlay.alpha = buf.readInt();
      }, false, new UniqueProperty("alpha", "firstaid.config.alpha", UniqueProperty.Type.INTEGER)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.overlay.armorTooltipMode, (buf) -> {
      }, (buf) -> {
      }, false, new UniqueProperty("armorTooltipMode", "firstaid.general.overlay.armortooltipmode", UniqueProperty.Type.STRING)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.overlay.enableEasterEggs, (buf) -> {
         buf.writeBoolean(FirstAidConfig.overlay.enableEasterEggs);
      }, (buf) -> {
         FirstAidConfig.overlay.enableEasterEggs = buf.readBoolean();
      }, false, new UniqueProperty("enableEasterEggs", "firstaid.general.overlay.enableeastereggs", UniqueProperty.Type.BOOLEAN)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.externalHealing.otherRegenMultiplier, (buf) -> {
         buf.writeDouble(FirstAidConfig.externalHealing.otherRegenMultiplier);
      }, (buf) -> {
         FirstAidConfig.externalHealing.otherRegenMultiplier = buf.readDouble();
      }, false, new UniqueProperty("otherRegenMultiplier", "firstaid.config.otherregenmultiplier", UniqueProperty.Type.DOUBLE)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.externalHealing.naturalRegenMultiplier, (buf) -> {
         buf.writeDouble(FirstAidConfig.externalHealing.naturalRegenMultiplier);
      }, (buf) -> {
         FirstAidConfig.externalHealing.naturalRegenMultiplier = buf.readDouble();
      }, false, new UniqueProperty("naturalRegenMultiplier", "firstaid.config.naturalregenmultiplier", UniqueProperty.Type.DOUBLE)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.debuffs, (buf) -> {
         buf.writeBoolean(FirstAidConfig.debuffs.head.blindness);
         throw new UnsupportedOperationException("Cannot write to buf: Class not implemented for sync: " + FirstAidConfig.debuffs.head.blindnessConditions.damageTaken.getClass());
      }, (buf) -> {
         FirstAidConfig.debuffs.head.blindness = buf.readBoolean();
         throw new UnsupportedOperationException("Cannot read from buf: Class not implemented for sync: " + FirstAidConfig.debuffs.head.blindnessConditions.damageTaken.getClass());
      }, false, new UniqueProperty("debuffs", "firstaid.config.debuffs", UniqueProperty.Type.CATEGORY)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.locationalArmor, (buf) -> {
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.headArmorMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.chestArmorMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.legsArmorMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.feetArmorMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.headArmorOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.chestArmorOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.legsArmorOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.armor.feetArmorOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.headToughnessMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.chestToughnessMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.legsToughnessMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.feetToughnessMultiplier);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.headToughnessOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.chestToughnessOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.legsToughnessOffset);
         buf.writeDouble(FirstAidConfig.locationalArmor.toughness.feetToughnessOffset);
      }, (buf) -> {
         FirstAidConfig.locationalArmor.armor.headArmorMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.chestArmorMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.legsArmorMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.feetArmorMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.headArmorOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.chestArmorOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.legsArmorOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.armor.feetArmorOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.headToughnessMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.chestToughnessMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.legsToughnessMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.feetToughnessMultiplier = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.headToughnessOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.chestToughnessOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.legsToughnessOffset = buf.readDouble();
         FirstAidConfig.locationalArmor.toughness.feetToughnessOffset = buf.readDouble();
      }, false, new UniqueProperty("locationalArmor", "firstaid.config.locationalarmor", UniqueProperty.Type.CATEGORY)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.enchantmentHandling, (buf) -> {
         buf.writeInt(FirstAidConfig.enchantmentHandling.enchantmentMultiplier);
         buf.writeByte(FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation.length);
         for (int i = 0; i < FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation.length; ++i) {
            ByteBufUtils.writeUTF8String(buf, FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation[i]);
         }
         buf.writeByte(FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride.length);
         for (int i = 0; i < FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride.length; ++i) {
            buf.writeInt(FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride[i]);
         }
      }, (buf) -> {
         FirstAidConfig.enchantmentHandling.enchantmentMultiplier = buf.readInt();
         {
            byte len = buf.readByte();
            String[] arr = new String[len];
            for (int i = 0; i < len; ++i) {
               arr[i] = ByteBufUtils.readUTF8String(buf);
            }
            FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation = arr;
         }
         {
            byte len = buf.readByte();
            int[] arr = new int[len];
            for (int i = 0; i < len; ++i) {
               arr[i] = buf.readInt();
            }
            FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride = arr;
         }
      }, false, new UniqueProperty("enchantmentHandling", "firstaid.config.enchantmenthandling", UniqueProperty.Type.CATEGORY)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.enchantmentHandling.overrideEntries, (buf) -> {
         buf.writeByte(FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation.length);
         for (int i = 0; i < FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation.length; ++i) {
            ByteBufUtils.writeUTF8String(buf, FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation[i]);
         }
         buf.writeByte(FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride.length);
         for (int i = 0; i < FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride.length; ++i) {
            buf.writeInt(FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride[i]);
         }
      }, (buf) -> {
         {
            byte len = buf.readByte();
            String[] arr = new String[len];
            for (int i = 0; i < len; ++i) {
               arr[i] = ByteBufUtils.readUTF8String(buf);
            }
            FirstAidConfig.enchantmentHandling.overrideEntries.resourceLocation = arr;
         }
         {
            byte len = buf.readByte();
            int[] arr = new int[len];
            for (int i = 0; i < len; ++i) {
               arr[i] = buf.readInt();
            }
            FirstAidConfig.enchantmentHandling.overrideEntries.multiplierOverride = arr;
         }
      }, false, new UniqueProperty("overrideEntries", "firstaid.general.enchantmenthandling.overrideentries", UniqueProperty.Type.CATEGORY)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.enableSoundSystem, (buf) -> {
         buf.writeBoolean(FirstAidConfig.enableSoundSystem);
      }, (buf) -> {
         FirstAidConfig.enableSoundSystem = buf.readBoolean();
      }, false, new UniqueProperty("enableSoundSystem", "firstaid.config.enablesoundsystem", UniqueProperty.Type.BOOLEAN)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.watchSetHealth, (buf) -> {
         buf.writeBoolean(FirstAidConfig.watchSetHealth);
      }, (buf) -> {
         FirstAidConfig.watchSetHealth = buf.readBoolean();
      }, false, new UniqueProperty("watchSetHealth", "firstaid.config.sethealth", UniqueProperty.Type.BOOLEAN)));
      listBuilder.add(new ConfigEntry<ExtraConfig.Advanced>(() -> FirstAidConfig.debug, (buf) -> {
         buf.writeBoolean(FirstAidConfig.debug);
      }, (buf) -> {
         FirstAidConfig.debug = buf.readBoolean();
      }, false, new UniqueProperty("debug", "firstaid.config.debug", UniqueProperty.Type.BOOLEAN)));
      List<ConfigEntry<ExtraConfig.Advanced>> list = listBuilder.build();
      FirstAid.LOGGER.debug("Found {} annotations of the type {} for the {}", Integer.valueOf(list.size()), ExtraConfig.Advanced.class, FirstAidConfig.class);
      return list;
   }
}
