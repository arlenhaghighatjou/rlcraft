package com.endertech.minecraft.forge.combat;

import com.endertech.common.FloatBounds;
import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.ForgeBounds;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.items.ForgeItem;
import com.endertech.minecraft.forge.materials.ForgeMaterial;
import com.endertech.minecraft.forge.materials.IMaterialBased;
import com.endertech.minecraft.forge.units.IForgeUnit;
import com.endertech.minecraft.forge.units.ItemState;
import com.endertech.minecraft.forge.units.UnitEnabled;
import com.endertech.minecraft.forge.units.UnitId;
import com.endertech.minecraft.forge.units.UnitVariant;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ForgeArmor extends ItemArmor implements IForgeUnit, IMaterialBased {
   public static final FloatBounds DAMAGE_REDUCTION = FloatBounds.from(Float.valueOf(0.0F), Float.valueOf(50.0F));
   public static final IntBounds DURABILITY = IntBounds.from(Integer.valueOf(0), Integer.valueOf(2000));
   protected final UnitId repair;
   private final ForgeConfig config;
   private final UnitId id;
   private final ForgeMod mod;
   private final UnitEnabled unitEnabled;
   private final UnitVariant defaultVariant;
   private Map<Object, UnitVariant> variants = new HashMap();
   private final ForgeMaterial material;

   public ForgeArmor(ForgeMod mod, UnitConfig config, ForgeMaterial material, ArmorMaterial armorMaterial, EntityEquipmentSlot equipmentSlot) {
      super(armorMaterial, ArmorMaterial.IRON.ordinal(), equipmentSlot);
      this.repair = material.getRepairId();
      this.mod = mod;
      this.id = this.createId(mod.getId(), material.getName());
      this.config = config;
      this.unitEnabled = new UnitEnabled(config, this.getUnitCategory(), true);
      this.material = material;
      this.defaultVariant = UnitVariant.from(this.id, IForgeUnit.Variants.INVENTORY);
      this.setRegistryName(this.getId().toResLoc());
      this.setUnlocalizedName(this.getId().getRegName());
      this.register();
   }

   public static float calculateDamageReduction(float materialStrength) {
      return DAMAGE_REDUCTION.approxUp(materialStrength).floatValue();
   }

   public static int calculateDurability(float materialStrength) {
      return DURABILITY.approxUp(materialStrength).intValue();
   }

   protected static int[] calculateReductionAmounts(ForgeMaterial material) {
      float reduction = calculateDamageReduction(material.getStrength());
      int[] amounts = new int[]{(int)(0.15F * reduction), (int)(0.3F * reduction), (int)(0.4F * reduction), (int)(0.15F * reduction)};
      return amounts;
   }

   public static float calculateToughness(ForgeMaterial material) {
      return ForgeBounds.TOUGHNESS.getFloatBounds().approxUp(material.getHeaviness()).floatValue();
   }

   public static ArmorMaterial createArmorMaterial(ForgeMaterial material) {
      int durability = calculateDurability(material.getStrength());
      int[] reductionAmounts = calculateReductionAmounts(material);
      int enchantability = ForgeMaterial.calculateEnchantability(material);
      float toughness = calculateToughness(material);
      String textureName = material.getId().toResLoc().toString();
      ArmorMaterial armorMaterial = EnumHelper.addArmorMaterial(material.getName(), textureName, durability / 10, reductionAmounts, enchantability, SoundEvents.ITEM_ARMOR_EQUIP_IRON, toughness);
      armorMaterial.setRepairItem(new ItemStack(material));
      return armorMaterial;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      ItemState state = ItemState.from(repair);

      for(ItemState repairState : this.repair.getAllItemStates()) {
         if (repairState.matches(state)) {
            return true;
         }
      }

      return false;
   }

   public boolean isReadyToRegister() {
      return true;
   }

   public ForgeConfig getConfig() {
      return this.config;
   }

   public UnitId getId() {
      return this.id;
   }

   public boolean isEnabled() {
      return this.unitEnabled.isEnabled();
   }

   public boolean isRegistered() {
      return ForgeItem.isRegistered(this);
   }

   public void onInit() {
   }

   public ForgeMaterial getMaterial() {
      return this.material;
   }

   public ForgeMod getMod() {
      return this.mod;
   }

   @SideOnly(Side.CLIENT)
   public void onInitClient() {
   }

   public Map<Object, UnitVariant> getUnitVariants() {
      return this.variants;
   }

   public UnitVariant getDefaultVariant() {
      return this.defaultVariant;
   }

   public <E extends Enum<E> & IForgeEnum> void setUnitVariants(Class<E> clazz) {
      this.variants = this.createUnitVariants(clazz);
   }
}
