package com.oblivioussp.spartanweaponry.item;

import net.lax1dude.eaglercraft.v1_8.HString;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.api.IWeaponPropertyContainer;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.api.weaponproperty.IPropertyCallback;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.client.gui.CreativeTabsSW;
import com.oblivioussp.spartanweaponry.compat.sme.SMEHelper;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownWeapon;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemSwordBase extends ItemSword implements IWeaponPropertyContainer<ItemSwordBase> {
   protected float attackDamage = 1.0F;
   protected double attackSpeed = 0.0D;
   protected List<WeaponProperty> properties;
   protected ToolMaterialEx materialEx;
   protected String modId = null;
   protected String displayName = null;
   protected boolean doCraftCheck = true;
   protected boolean canBeCrafted = true;

   public ItemSwordBase(String unlocName, ToolMaterialEx material, float weaponBaseDamage, float weaponDamageMultiplier, double weaponSpeed, WeaponProperty... weaponProperties) {
      super(material.getMaterial());
      this.setCreativeTab(CreativeTabsSW.TAB_SW);
      this.setRegistryName(unlocName);
      this.setUnlocalizedName(unlocName);
      this.materialEx = material;
      this.attackDamage = Math.max(0.5F, this.materialEx.getAttackDamage() * weaponDamageMultiplier + weaponBaseDamage - 1.0F);
      this.attackSpeed = weaponSpeed;
      this.properties = new ArrayList();
      this.properties.addAll(Arrays.asList(weaponProperties));
   }

   public String getUnlocalizedName() {
      return this.modId != null ? StringHelper.getItemUnlocalizedName(super.getUnlocalizedName(), this.modId) : StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return this.modId != null ? StringHelper.getItemUnlocalizedName(super.getUnlocalizedName(), this.modId) : StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      if (entity instanceof EntityLivingBase) {
         EntityLivingBase living = (EntityLivingBase)entity;
         if (this.properties != null) {
            for(WeaponProperty property : this.properties) {
               IPropertyCallback callback = property.getCallback();
               if (callback != null) {
                  callback.onItemUpdate(this.materialEx, stack, world, living, itemSlot, isSelected);
               }
            }
         }

         if (this.materialEx.hasAnyWeaponProperty()) {
            for(WeaponProperty property : this.materialEx.getAllWeaponProperties()) {
               IPropertyCallback callback = property.getCallback();
               if (callback != null) {
                  callback.onItemUpdate(this.materialEx, stack, world, living, itemSlot, isSelected);
               }
            }
         }
      }

   }

   public float getAttackDamage() {
      return this.materialEx.getAttackDamage();
   }

   public int getItemEnchantability() {
      return this.materialEx.getEnchantability();
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      if (!toRepair.isEmpty() && !repair.isEmpty()) {
         return this.materialEx.doesOreDictMatch(repair) ? true : super.getIsRepairable(toRepair, repair);
      } else {
         return false;
      }
   }

   public String getToolMaterialName() {
      return this.materialEx.getMaterial().toString();
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      for(String type : this.getToolClasses(stack)) {
         if (state.getBlock().isToolEffective(type, state)) {
            return this.materialEx.getEfficiency();
         }
      }

      return super.getDestroySpeed(stack, state);
   }

   public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = HashMultimap.create();
      if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.getDirectAttackDamage(), 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed - 4.0D, 0));
      }

      return multimap;
   }

   public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
      return this.hasWeaponProperty(WeaponProperties.SHIELD_BREACH);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      boolean isShiftPressed = GuiScreen.isShiftKeyDown();
      if (this.doCraftCheck && worldIn != null) {
         if (this.materialEx.getModId() == "spartanweaponry") {
            List<ItemStack> ores = OreDictionary.getOres(this.materialEx.getOreDictForRepairMaterial(), false);
            if (ores == null || ores.isEmpty()) {
               this.canBeCrafted = false;
            }
         }

         this.doCraftCheck = false;
      }

      if (!ConfigHandler.forceDisableUncraftableTooltips && !this.canBeCrafted) {
         tooltip.add(TextFormatting.RED + StringHelper.translateFormattedString("cantCraftMissingMaterial", "tooltip", "spartanweaponry", StringHelper.translateString(this.materialEx.getOreDictForRepairMaterial(), "material", this.materialEx.getModId())));
      }

      if (!this.properties.isEmpty() || this.materialEx.hasAnyWeaponProperty()) {
         tooltip.add(TextFormatting.GOLD + StringHelper.translateFormattedString("properties", "tooltip", "spartanweaponry", isShiftPressed ? TextFormatting.DARK_GRAY + StringHelper.translateString("showingDetails", "tooltip", "spartanweaponry") : TextFormatting.DARK_GRAY + StringHelper.translateFormattedString("showDetails", "tooltip", "spartanweaponry", TextFormatting.AQUA + "SHIFT" + TextFormatting.DARK_GRAY)));
      }

      for(WeaponProperty property : this.properties) {
         property.addTooltip(stack, tooltip, isShiftPressed);
      }

      if (this.materialEx.hasAnyWeaponProperty()) {
         tooltip.add(TextFormatting.DARK_AQUA + "Material Bonus:");

         for(WeaponProperty matProp : this.materialEx.getAllWeaponProperties()) {
            matProp.addTooltip(stack, tooltip, isShiftPressed);
         }
      }

      tooltip.add("");
      super.addInformation(stack, worldIn, tooltip, flagIn);
   }

   public String getItemStackDisplayName(ItemStack stack) {
      if (this.displayName != null) {
         String name = I18n.translateToLocalFormatted(HString.format("item.%s:%s.name", "spartanweaponry", this.displayName), new Object[]{I18n.translateToLocal(this.materialEx.getFullUnlocName())});
         return name;
      } else {
         return super.getItemStackDisplayName(stack);
      }
   }

   public float getDirectAttackDamage() {
      return this.attackDamage;
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      for(WeaponProperty prop : this.properties) {
         IPropertyCallback callback = prop.getCallback();
         if (callback != null) {
            callback.onHitEntity(this.materialEx, stack, target, attacker, (Entity)null);
         }
      }

      for(WeaponProperty prop : this.materialEx.getAllWeaponProperties()) {
         IPropertyCallback callback = prop.getCallback();
         if (callback != null) {
            callback.onHitEntity(this.materialEx, stack, target, attacker, (Entity)null);
         }
      }

      return super.hitEntity(stack, target, attacker);
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
      if (this.getFirstWeaponPropertyWithType("throwable") != null) {
         ItemStack stack = playerIn.getHeldItem(hand);
         playerIn.setActiveHand(hand);
         return new ActionResult(EnumActionResult.SUCCESS, stack);
      } else {
         return super.onItemRightClick(worldIn, playerIn, hand);
      }
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      if (this.getFirstWeaponPropertyWithType("throwable") != null && entityLiving instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLiving;
         int charge = this.getMaxItemUseDuration(stack) - timeLeft;
         if (charge >= 5) {
            charge = 5;
         }

         if (!worldIn.isRemote && charge > 2) {
            EntityThrownWeapon thrown = new EntityThrownWeapon(worldIn, player);
            thrown.setWeapon(stack);
            thrown.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F * ((float)charge / 10.0F + 0.5F), 0.5F);
            thrown.setDamage((double)this.getDirectAttackDamage() + 1.0D);
            double damageModifier = 0.0D;
            if (Loader.isModLoaded("somanyenchantments")) {
               Enchantment supSharpness = (Enchantment)ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation("somanyenchantments:supremesharpness"));
               Enchantment advSharpness = (Enchantment)ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation("somanyenchantments:advancedsharpness"));
               Enchantment lesSharpness = (Enchantment)ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation("somanyenchantments:lessersharpness"));
               int j;
               if ((j = EnchantmentHelper.getEnchantmentLevel(supSharpness, stack)) > 0) {
                  damageModifier = 4.0D + (double)j * 1.6D;
               } else if ((j = EnchantmentHelper.getEnchantmentLevel(advSharpness, stack)) > 0) {
                  damageModifier = 1.25D + (double)j * 0.95D;
               } else if ((j = EnchantmentHelper.getEnchantmentLevel(lesSharpness, stack)) > 0) {
                  damageModifier = 0.25D + (double)j * 0.25D;
               }
            }

            int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
            if (j > 0) {
               damageModifier = 0.5D + (double)j * 0.5D;
            }

            if (damageModifier > 0.0D) {
               thrown.setDamage(thrown.getDamage() + damageModifier);
            }

            int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack);
            if (k > 0) {
               thrown.setKnockbackStrength(k);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) > 0) {
               thrown.setFire(100);
            }

            if (player.capabilities.isCreativeMode) {
               thrown.pickupStatus = PickupStatus.CREATIVE_ONLY;
            } else if (thrown.isValidThrowingWeapon()) {
               stack.setCount(stack.getCount() - 1);
               if (stack.getCount() <= 0) {
                  player.inventory.deleteStack(stack);
               }
            }

            if (thrown.isValidThrowingWeapon()) {
               worldIn.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
               worldIn.spawnEntity(thrown);
            }

            player.addStat(StatList.getObjectUseStats(this));
         }
      }

      super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return this.getFirstWeaponPropertyWithType("throwable") != null ? 72000 : super.getMaxItemUseDuration(stack);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return this.getFirstWeaponPropertyWithType("throwable") != null ? EnumAction.BOW : super.getItemUseAction(stack);
   }

   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      for(WeaponProperty prop : this.properties) {
         IPropertyCallback callback = prop.getCallback();
         if (callback != null) {
            callback.onCreateItem(this.materialEx, stack);
         }
      }

      for(WeaponProperty prop : this.materialEx.getAllWeaponProperties()) {
         IPropertyCallback callback = prop.getCallback();
         if (callback != null) {
            callback.onCreateItem(this.materialEx, stack);
         }
      }

      super.onCreated(stack, worldIn, playerIn);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         ItemStack stack = new ItemStack(this);

         for(WeaponProperty prop : this.properties) {
            IPropertyCallback callback = prop.getCallback();
            if (callback != null) {
               callback.onCreateItem(this.materialEx, stack);
            }
         }

         for(WeaponProperty prop : this.materialEx.getAllWeaponProperties()) {
            IPropertyCallback callback = prop.getCallback();
            if (callback != null) {
               callback.onCreateItem(this.materialEx, stack);
            }
         }

         items.add(stack);
      }

   }

   @Nullable
   public String getCreatorModId(ItemStack itemStack) {
      return this.modId != null ? this.modId : super.getCreatorModId(itemStack);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      WeaponProperty prop = this.getFirstWeaponPropertyWithType("sweep_damage");
      boolean isSweepCompatible = prop != null && prop.getLevel() == 1;
      return this.hasWeaponProperty(WeaponProperties.VERSATILE) && ModSpartanWeaponry.isSMELoaded && SMEHelper.isCombatAxeEnchantment(enchantment.type) || enchantment == Enchantments.SWEEPING && isSweepCompatible || enchantment != Enchantments.SWEEPING && super.canApplyAtEnchantingTable(stack, enchantment);
   }

   public ItemSwordBase addWeaponProperty(WeaponProperty prop) {
      this.properties.add(prop);
      return this;
   }

   public boolean hasWeaponProperty(WeaponProperty prop) {
      return this.properties.contains(prop) || this.materialEx.getAllWeaponProperties().contains(prop);
   }

   public WeaponProperty getFirstWeaponPropertyWithType(String type) {
      for(WeaponProperty property : this.properties) {
         if (property.getType() == type) {
            return property;
         }
      }

      for(WeaponProperty property : this.materialEx.getAllWeaponProperties()) {
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

      for(WeaponProperty property : this.materialEx.getAllWeaponProperties()) {
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
      return new ArrayList(this.properties);
   }

   public ToolMaterialEx getMaterialEx() {
      return this.materialEx;
   }
}
