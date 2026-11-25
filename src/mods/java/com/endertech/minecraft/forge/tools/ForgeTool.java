package com.endertech.minecraft.forge.tools;

import com.endertech.common.FloatBounds;
import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.ForgeBounds;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.items.DamageableItem;
import com.endertech.minecraft.forge.materials.ForgeMaterial;
import com.endertech.minecraft.forge.materials.IMaterialBased;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Locale;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ForgeTool extends DamageableItem implements IMaterialBased {
   public static final String MODIFIER_NAME = "Tool modifier";
   public static final IntBounds DURABILITY_BOUNDS = IntBounds.from(Integer.valueOf(0), Integer.valueOf(2000));
   private final ForgeMaterial material;
   private final String toolClass;
   private final float attackDamage;
   private final float attackSpeed;

   public ForgeTool(ForgeMod mod, UnitConfig config, ForgeMaterial material, float attackDamage, float attackSpeed, int durability) {
      super(mod, config, material.getName(), CreativeTabs.TOOLS, durability, material.getRepairId());
      this.material = material;
      this.toolClass = getToolClass(this.getClass());
      this.attackDamage = ForgeConfig.getFloat(config, this.getUnitCategory(), "attackDamage", attackDamage, new FloatBounds(0.0F, Float.MAX_VALUE), "Defines attack damage for this item.");
      this.attackSpeed = ForgeConfig.getFloat(config, this.getUnitCategory(), "attackSpeed", attackSpeed, ForgeBounds.ATTACK_SPEED.getFloatBounds(), "Defines attack speed for this item.");
      this.setHarvestLevel(this.getToolClass(), this.getHarvestLevel());
      this.setFull3D();
   }

   public static float calculateAttackDamage(ForgeMaterial material, FloatBounds damageBounds) {
      return damageBounds.approxUp(material.getStrength()).floatValue();
   }

   public static float calculateAttackSpeed(ForgeMaterial material, FloatBounds speedBounds) {
      return speedBounds.approxDown(material.getHeaviness()).floatValue();
   }

   public static int calculateDurability(ForgeMaterial material, IntBounds durabilityBounds) {
      return durabilityBounds.approxUp(material.getStrength()).intValue();
   }

   public static float calculateEfficiency(float materialStrength) {
      return ForgeBounds.HARDNESS.getFloatBounds().approxUp(materialStrength).floatValue();
   }

   public static int calculateHarvestLevel(float materialStrength) {
      float harvestLevel = ForgeBounds.HARVEST_LEVEL.getFloatBounds().approxUp(materialStrength).floatValue();
      return (int)Math.ceil((double)harvestLevel);
   }

   public static String getToolClass(Class<?> classType) {
      return classType.getSimpleName().toLowerCase(Locale.ROOT);
   }

   public String getToolClass() {
      return this.toolClass;
   }

   public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
      Multimap<String, AttributeModifier> multimap = HashMultimap.create();
      if (slot == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, this.getModifierName(), (double)this.getAttackDamage(), 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, this.getModifierName(), (double)(-ForgeBounds.ATTACK_SPEED.getFloatBounds().getMax().floatValue() + this.getAttackSpeed()), 0));
      }

      return multimap;
   }

   public int getItemEnchantability() {
      return ForgeMaterial.calculateEnchantability(this.getMaterial());
   }

   public boolean isEffectiveOn(IBlockState state) {
      Block block = state.getBlock();
      boolean effective = block.isToolEffective(this.getToolClass(), state);
      if (!effective) {
         effective = this.getBlocksEffectiveOn().contains(state.getBlock());
      }

      if (!effective) {
         effective = this.getMaterialsEffectiveOn().contains(state.getMaterial());
      }

      return effective;
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      return this.isEffectiveOn(state) ? this.getEfficiency() : super.getDestroySpeed(stack, state);
   }

   public float getAttackDamage() {
      return this.attackDamage;
   }

   public float getAttackSpeed() {
      return this.attackSpeed;
   }

   public float getEfficiency() {
      return calculateEfficiency(this.getMaterial().getStrength());
   }

   public int getHarvestLevel() {
      return calculateHarvestLevel(this.getMaterial().getStrength());
   }

   protected String getModifierName() {
      return "Tool modifier";
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      stack.damageItem(2, attacker);
      return true;
   }

   public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
      int damage = (int)state.getBlockHardness(worldIn, pos);
      if (!this.isEffectiveOn(state)) {
         damage *= 3;
      }

      if (damage > 0) {
         stack.damageItem(damage, entityLiving);
      }

      return true;
   }

   public ForgeMaterial getMaterial() {
      return this.material;
   }

   protected abstract Set<Block> getBlocksEffectiveOn();

   protected abstract Set<Material> getMaterialsEffectiveOn();
}
