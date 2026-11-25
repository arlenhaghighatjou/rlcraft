package com.endertech.minecraft.forge.materials;

import com.endertech.common.Args;
import com.endertech.common.Fields;
import com.endertech.common.FloatBounds;
import com.endertech.minecraft.forge.ForgeBounds;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ColorARGB;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.economy.IValuable;
import com.endertech.minecraft.forge.items.ForgeItem;
import com.endertech.minecraft.forge.units.UnitId;
import net.minecraft.util.text.TextFormatting;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ForgeMaterial extends ForgeItem implements IValuable {
   private final ForgeMaterial.Type type;
   private final String name;
   private final float strength;
   private final float heaviness;
   private final float frequency;
   private final float magicability;
   private final ColorARGB color;

   public ForgeMaterial(ForgeMod mod, UnitConfig config, ForgeMaterial.Type type, String name, float strength, float heaviness, float frequency, float magicability, ColorARGB color) {
      super(mod, config, name, CreativeTabs.MATERIALS);
      this.type = type;
      this.name = name;
      FloatBounds bounds = ForgeBounds.FACTOR.getFloatBounds();
      this.strength = ForgeConfig.getFloat(config, this.getUnitCategory(), "strength", strength, bounds, "Defines how strong the material is. Affects: physical resistance, durability, weapons & tools power etc.");
      this.heaviness = ForgeConfig.getFloat(config, this.getUnitCategory(), "heaviness", heaviness, bounds, "Defines how heavy one gramm of the material is. Affects: damage, speed");
      this.frequency = ForgeConfig.getFloat(config, this.getUnitCategory(), "frequency", frequency, bounds, "Defines how often this material appears in the world. Affects: loot and ore generation.");
      this.magicability = ForgeConfig.getFloat(config, this.getUnitCategory(), "magicability", magicability, bounds, "Defines how magical the material is. Affects: enchantability, magic resistance.");
      this.color = ForgeConfig.getColorARGB(config, this.getUnitCategory(), "color", color, "Defines a color in ARGB (Alpha, Red, Green, Blue) hex format (0xaarrggbb)");
   }

   public static ForgeMaterial noRegistry(ForgeMod mod, ForgeMaterial.Type type, String name, float strength, float heaviness, float frequency, float magicability, ColorARGB color) {
      ForgeMaterial material = new ForgeMaterial(mod, (UnitConfig)null, type, name, strength, heaviness, frequency, magicability, color);
      mod.getRegistrator().removeUnit(material);
      return material;
   }

   public static int calculateEnchantability(ForgeMaterial material) {
      return ForgeBounds.ENCHANTABILITY.getIntBounds().approxUp(material.getMagicability()).intValue();
   }

   public static float calculateHardness(ForgeMaterial material) {
      return ForgeBounds.HARDNESS.getFloatBounds().approxUp(material.getStrength()).floatValue();
   }

   public static float calculateResistance(ForgeMaterial material) {
      return ForgeBounds.RESISTANCE.getFloatBounds().approxUp(material.getStrength()).floatValue();
   }

   public static int calculateHarvestLevel(ForgeMaterial material) {
      float harvestLevel = ForgeBounds.HARVEST_LEVEL.getFloatBounds().approxUp(material.getStrength()).floatValue();
      return (int)Math.floor((double)harvestLevel);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
      tooltip.add(TextFormatting.GREEN + "Strength: " + this.getStrength());
      tooltip.add(TextFormatting.YELLOW + "Heaviness: " + this.getHeaviness());
      tooltip.add(TextFormatting.LIGHT_PURPLE + "Magicability: " + this.getMagicability());
      super.addInformation(stack, world, tooltip, flag);
   }

   public String toString() {
      return ForgeMaterial.class.getSimpleName() + Args.group(Args.get("strength", Float.valueOf(this.getStrength())), Args.get("heaviness", Float.valueOf(this.getHeaviness())), Args.get("frequency", Float.valueOf(this.getFrequency())), Args.get("magicability", Float.valueOf(this.getMagicability()))) + Args.extend(super.toString());
   }

   public String getName() {
      return this.name;
   }

   public float getStrength() {
      return this.strength;
   }

   public float getHeaviness() {
      return this.heaviness;
   }

   public float getFrequency() {
      return this.frequency;
   }

   public float getMagicability() {
      return this.magicability;
   }

   public ColorARGB getColor() {
      return this.color;
   }

   public float getWorthness() {
      return this.getStrength() * 0.3F + ForgeBounds.invertFactor(this.getHeaviness()) * 0.1F + ForgeBounds.invertFactor(this.getFrequency()) * 0.4F + this.getMagicability() * 0.2F;
   }

   public ForgeMaterial.Type getType() {
      return this.type;
   }

   public UnitId getRepairId() {
      return UnitId.from(this.getType() == ForgeMaterial.Type.METAL ? ForgeMaterial.Type.INGOT.getDictNameFor(this.getName()) : this.getId().getDictName());
   }

   public static enum Type implements IForgeEnum {
      BLOCK,
      DUST,
      GEM,
      INGOT,
      METAL;

      private Type() {
      }

      public String getName() {
         return this.getLowerCase(this.name());
      }

      public String getDictNameFor(String materialName) {
         return UnitId.buildDictName(this.getName(), materialName);
      }
   }

   public static class VanillaMaterials {
      static final Map<Material, String> MATERIAL_NAMES = new HashMap();

      public VanillaMaterials() {
      }

      public static String getNameFor(Material material, String defaultName) {
         return (String)MATERIAL_NAMES.getOrDefault(material, defaultName);
      }

      static {
         for(Field field : Fields.getStaticFrom(Material.class)) {
            if (field.getType() == Material.class) {
               try {
                  Material material = (Material)field.get((Object)null);
                  MATERIAL_NAMES.put(material, field.getName());
               } catch (IllegalArgumentException var5) {
                  var5.printStackTrace();
               } catch (IllegalAccessException var6) {
                  var6.printStackTrace();
               }
            }
         }

      }
   }
}
