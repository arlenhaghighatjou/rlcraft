package com.oblivioussp.spartanweaponry.api;

import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

public class ToolMaterialEx {
   public static final ToolMaterialEx WOOD = new ToolMaterialEx("wood", ToolMaterial.WOOD, "plankWood");
   public static final ToolMaterialEx STONE = new ToolMaterialEx("stone", ToolMaterial.STONE, "cobblestone");
   public static final ToolMaterialEx IRON = new ToolMaterialEx("iron", ToolMaterial.IRON, "ingotIron");
   public static final ToolMaterialEx GOLD = new ToolMaterialEx("gold", ToolMaterial.GOLD, "ingotGold");
   public static final ToolMaterialEx DIAMOND = new ToolMaterialEx("diamond", ToolMaterial.DIAMOND, "gemDiamond");
   public static final ToolMaterialEx LEATHER = new ToolMaterialEx("leather", EnumHelper.addToolMaterial("sw_leather", 1, 128, 2.0F, 0.0F, 5).setRepairItem(new ItemStack(Items.LEATHER)), "leather");
   protected ToolMaterial material;
   protected String unlocName;
   protected String modId;
   protected String repairOreDict;
   protected float baseDamage;
   protected int colourPrimary;
   protected int colourSecondary;
   protected List<WeaponProperty> properties;

   public ToolMaterialEx(String unlocalizedName, String repairMaterialOreDict, int primaryColour, int secondaryColour, int harvestLevel, int maxUses, float efficiency, float baseDamage, int enchantability) {
      this(unlocalizedName, repairMaterialOreDict, "spartanweaponry", primaryColour, secondaryColour, harvestLevel, maxUses, efficiency, baseDamage, enchantability);
   }

   public ToolMaterialEx(String unlocalizedName, String repairMaterialOreDict, String externalModId, int primaryColour, int secondaryColour, int harvestLevel, int maxUses, float efficiency, float baseDamage, int enchantability) {
      this.modId = "spartanweaponry";
      this.colourPrimary = 8355711;
      this.colourSecondary = 16777215;
      this.properties = new ArrayList();
      this.unlocName = unlocalizedName;
      this.repairOreDict = repairMaterialOreDict;
      this.colourPrimary = primaryColour;
      this.colourSecondary = secondaryColour;
      this.modId = externalModId;
      this.baseDamage = baseDamage;
      this.material = EnumHelper.addToolMaterial(externalModId + ":" + unlocalizedName, harvestLevel, maxUses, efficiency, baseDamage, enchantability);
   }

   public ToolMaterialEx(String unlocalizedName, String repairMaterialOreDict, String externalModId, int primaryColour, int secondaryColour, int harvestLevel, int maxUses, float efficiency, float baseDamage, int enchantability, WeaponProperty... weaponProperties) {
      this(unlocalizedName, repairMaterialOreDict, externalModId, primaryColour, secondaryColour, harvestLevel, maxUses, efficiency, baseDamage, enchantability);

      for(WeaponProperty property : weaponProperties) {
         this.properties.add(property);
      }

   }

   public ToolMaterialEx(String unlocalizedName, ToolMaterial toolMaterial, String repairMaterialOreDict, String externalModId, float baseDamage, WeaponProperty... weaponProperties) {
      this(unlocalizedName, toolMaterial, repairMaterialOreDict, externalModId, baseDamage);

      for(WeaponProperty property : weaponProperties) {
         this.properties.add(property);
      }

   }

   public ToolMaterialEx(String unlocalizedName, ToolMaterial toolMaterial, String repairMaterialOreDict, String externalModId, float baseDamage) {
      this(unlocalizedName, toolMaterial, repairMaterialOreDict, externalModId);
      this.baseDamage = baseDamage;
   }

   public ToolMaterialEx(String unlocalizedName, ToolMaterial toolMaterial, String repairMaterialOreDict, String externalModId) {
      this(unlocalizedName, toolMaterial, repairMaterialOreDict);
      this.modId = externalModId;
   }

   protected ToolMaterialEx(String unlocalizedName, ToolMaterial toolMaterialBase, String repairMaterialOreDict) {
      this.modId = "spartanweaponry";
      this.colourPrimary = 8355711;
      this.colourSecondary = 16777215;
      this.properties = new ArrayList();
      this.unlocName = unlocalizedName;
      this.material = toolMaterialBase;
      this.repairOreDict = repairMaterialOreDict;
      this.baseDamage = toolMaterialBase.getAttackDamage();
   }

   public ToolMaterial getMaterial() {
      return this.material;
   }

   public String getUnlocName() {
      return this.unlocName;
   }

   public String getFullUnlocName() {
      return String.format("material.%s:%s", this.modId, this.unlocName);
   }

   public String getOreDictForRepairMaterial() {
      return this.repairOreDict;
   }

   public boolean doesOreDictMatch(ItemStack stack) {
      if (!OreDictionary.doesOreNameExist(this.repairOreDict)) {
         return false;
      } else {
         for(ItemStack ore : OreDictionary.getOres(this.repairOreDict, false)) {
            if (OreDictionary.itemMatches(ore, stack, false)) {
               return true;
            }
         }

         return false;
      }
   }

   public int getPrimaryColour() {
      return this.colourPrimary;
   }

   public int getSecondaryColour() {
      return this.colourSecondary;
   }

   public String getModId() {
      return this.modId;
   }

   public int getMaxUses() {
      return this.material.getMaxUses();
   }

   public float getEfficiency() {
      return this.material.getEfficiency();
   }

   public float getAttackDamage() {
      return this.baseDamage;
   }

   public int getHarvestLevel() {
      return this.material.getHarvestLevel();
   }

   public int getEnchantability() {
      return this.material.getEnchantability();
   }

   public boolean hasAnyWeaponProperty() {
      return !this.properties.isEmpty();
   }

   public boolean hasWeaponProperty(WeaponProperty prop) {
      return this.properties.contains(prop);
   }

   public WeaponProperty getFirstWeaponPropertyWithType(String type) {
      for(WeaponProperty property : this.properties) {
         if (property.getType() == type) {
            return property;
         }
      }

      return null;
   }

   public List<WeaponProperty> getAllWeaponPropertiesWithType(String type) {
      List<WeaponProperty> result = new ArrayList();

      for(WeaponProperty property : this.properties) {
         if (property.getType() == type) {
            result.add(property);
         }
      }

      if (result.isEmpty()) {
         return null;
      } else {
         return result;
      }
   }

   public List<WeaponProperty> getAllWeaponProperties() {
      return this.properties;
   }
}
