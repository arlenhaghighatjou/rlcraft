package com.oblivioussp.spartanweaponry.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.oblivioussp.spartanweaponry.api.IWeaponPropertyContainer;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.weaponproperty.IPropertyCallback;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.client.gui.CreativeTabsSW;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownWeapon;
import com.oblivioussp.spartanweaponry.init.EnchantmentRegistrySW;
import com.oblivioussp.spartanweaponry.init.SoundRegistry;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.Log;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemThrowingWeapon extends Item implements IWeaponPropertyContainer<ItemThrowingWeapon> {
   protected float attackDamage = 1.0F;
   protected double attackSpeed = 0.0D;
   protected List<WeaponProperty> properties;
   protected ToolMaterialEx materialEx;
   protected String modId = null;
   protected String displayName = null;
   protected boolean doCraftCheck = true;
   protected boolean canBeCrafted = true;
   protected int maxAmmo = 1;
   protected int maxChargeTicks = 5;
   protected float throwVelocity;
   protected float throwDamageMultiplier;
   public static final String NBT_AMMO = "Ammo";
   public static final String NBT_AMMO_USED = "AmmoUsed";
   public static final String NBT_UUID = "UUID";
   public static final String NBT_ORIGINAL = "Original";

   public ItemThrowingWeapon(String unlocName, ToolMaterialEx material, float weaponDamageBase, float weaponDamageMultiplier, float weaponSpeed, int maxAmmoCapacity, WeaponProperty... weaponProperties) {
      this.setCreativeTab(CreativeTabsSW.TAB_SW);
      this.setRegistryName(unlocName);
      this.setUnlocalizedName(unlocName);
      this.setMaxStackSize(1);
      this.materialEx = material;
      this.setMaxDamage(this.materialEx.getMaxUses() / 4);
      this.attackDamage = Math.max(0.5F, this.materialEx.getAttackDamage() * weaponDamageMultiplier + weaponDamageBase - 1.0F);
      this.attackSpeed = (double)weaponSpeed;
      this.maxAmmo = maxAmmoCapacity;
      this.properties = new ArrayList();
      this.properties.addAll(Arrays.asList(weaponProperties));
      this.throwVelocity = 1.0F;
      this.throwDamageMultiplier = 1.0F;
      this.addPropertyOverride(new ResourceLocation("empty"), new IItemPropertyGetter() {
         public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
            return stack.hasTagCompound() && stack.getTagCompound().getInteger("AmmoUsed") == ItemThrowingWeapon.this.getMaxAmmo(stack) ? 1.0F : 0.0F;
         }
      });
   }

   public String getUnlocalizedName() {
      return this.modId != null ? StringHelper.getItemUnlocalizedName(super.getUnlocalizedName(), this.modId) : StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return this.modId != null ? StringHelper.getItemUnlocalizedName(super.getUnlocalizedName(), this.modId) : StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }

   public EntityThrownWeapon createThrownWeaponEntity(World world, EntityPlayer player) {
      return new EntityThrownWeapon(world, player);
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
      ItemStack stack = playerIn.getHeldItem(hand);
      if (stack.hasTagCompound() && stack.getTagCompound().getInteger("AmmoUsed") == this.getMaxAmmo(stack)) {
         return new ActionResult(EnumActionResult.FAIL, stack);
      } else {
         playerIn.setActiveHand(hand);
         return new ActionResult(EnumActionResult.SUCCESS, stack);
      }
   }

   public int getItemEnchantability() {
      return this.materialEx.getEnchantability();
   }

   public boolean isEnchantable(ItemStack stack) {
      return stack.hasTagCompound() ? (stack.getTagCompound().hasKey("Original") ? stack.getTagCompound().getBoolean("Original") : true) : super.isEnchantable(stack);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return this.isEnchantable(stack) ? super.canApplyAtEnchantingTable(stack, enchantment) : false;
   }

   public boolean isRepairable() {
      return false;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      if (!toRepair.isEmpty() && !repair.isEmpty()) {
         return this.materialEx.doesOreDictMatch(repair) ? true : super.getIsRepairable(toRepair, repair);
      } else {
         return false;
      }
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      for(String type : this.getToolClasses(stack)) {
         if (state.getBlock().isToolEffective(type, state)) {
            return this.materialEx.getEfficiency() * 0.8F;
         }
      }

      return 1.0F;
   }

   public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
      if (!world.isRemote && state.getBlockHardness(world, pos) != 0.0F) {
         this.damageItem(stack, 2, entityLiving);
      }

      return false;
   }

   public void damageItem(ItemStack stack, int damage, EntityLivingBase entity) {
      if (stack.isItemStackDamageable() && stack.hasTagCompound() && stack.getTagCompound().getInteger("AmmoUsed") < this.getMaxAmmo(stack) && (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.isCreativeMode) && stack.attemptDamageItem(damage, entity.getRNG(), entity instanceof EntityPlayerMP ? (EntityPlayerMP)entity : null)) {
         entity.renderBrokenItemStack(stack);
         if (stack.hasTagCompound()) {
            int ammo = stack.getTagCompound().getInteger("AmmoUsed");
            NBTTagCompound var10000 = stack.getTagCompound();
            ++ammo;
            var10000.setInteger("AmmoUsed", ammo);
         }

         if (entity instanceof EntityPlayer) {
            ((EntityPlayer)entity).addStat(StatList.getObjectBreakStats(stack.getItem()));
         }

         stack.setItemDamage(0);
      }

   }

   public float getDirectAttackDamage() {
      return this.attackDamage;
   }

   public float getThrownDamageMultiplier() {
      return this.throwDamageMultiplier;
   }

   public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = HashMultimap.create();
      if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double)this.getDirectAttackDamage(), 0));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.attackSpeed - 4.0D, 0));
      }

      return multimap;
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      if (stack.hasTagCompound() && stack.getTagCompound().getInteger("AmmoUsed") < this.getMaxAmmo(stack)) {
         this.damageItem(stack, 2, attacker);
      }

      return true;
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      if (entityLiving instanceof EntityPlayer) {
         int maxAmmo = this.getMaxAmmo(stack);
         int ammoCount = maxAmmo - stack.getTagCompound().getInteger("AmmoUsed");
         if (ammoCount > 0) {
            EntityPlayer player = (EntityPlayer)entityLiving;
            int maxCharge = this.getMaxChargeTicks(stack);
            int charge = Math.min(this.getMaxItemUseDuration(stack) - timeLeft, maxCharge);
            if (!worldIn.isRemote) {
               EntityThrownWeapon thrown = this.createThrownWeaponEntity(worldIn, player);
               float chargePerc = (float)charge / (float)maxCharge;
               if (thrown == null) {
                  return;
               }

               thrown.setWeapon(stack);
               int velocityBonus = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_RANGE, stack);
               thrown.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, this.throwVelocity * ((float)velocityBonus * 0.2F + 1.0F) * (chargePerc * 0.9F + 0.1F), 0.5F);
               double damageMultiplier = (double)((this.throwDamageMultiplier - 1.0F) * chargePerc + 1.0F);
               thrown.setDamage(((double)this.getDirectAttackDamage() + 1.0D) * damageMultiplier);
               int j = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_DAMAGE, stack);
               if (j > 0) {
                  thrown.setDamage(thrown.getDamage() + (double)j * 0.5D + 0.5D);
               }

               int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack);
               if (k > 0) {
                  thrown.setKnockbackStrength(k);
               }

               if (EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_FIRE, stack) > 0) {
                  thrown.setFire(100);
               }

               if (player.capabilities.isCreativeMode) {
                  thrown.pickupStatus = PickupStatus.CREATIVE_ONLY;
               } else if (thrown.isValidThrowingWeapon()) {
                  --ammoCount;
                  stack.getTagCompound().setInteger("AmmoUsed", maxAmmo - ammoCount);
                  if (ammoCount == 0 && !stack.getTagCompound().getBoolean("Original")) {
                     stack.shrink(1);
                     if (stack.getCount() <= 0) {
                        player.inventory.deleteStack(stack);
                     }
                  }
               }

               if (thrown.isValidThrowingWeapon()) {
                  stack.setItemDamage(0);
                  worldIn.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundRegistry.THROWING_WEAPON_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
                  worldIn.spawnEntity(thrown);
               }

               player.addStat(StatList.getObjectUseStats(this));
            }
         }
      }

      super.onPlayerStoppedUsing(stack, worldIn, entityLiving, timeLeft);
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BOW;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 72000;
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
      this.initNBT(stack, true);
      if (this.properties != null && entity instanceof EntityLivingBase) {
         EntityLivingBase living = (EntityLivingBase)entity;

         for(WeaponProperty property : this.properties) {
            IPropertyCallback callback = property.getCallback();
            if (callback != null) {
               callback.onItemUpdate(this.materialEx, stack, world, living, itemSlot, isSelected);
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

      this.initNBT(stack, true);
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

         this.initNBT(stack, false);
         items.add(stack);
      }

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

      if (stack.hasTagCompound() && !stack.getTagCompound().hasKey("enchChecked")) {
         if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack) != 0 || EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) != 0 || EnchantmentHelper.getEnchantmentLevel(Enchantments.LOOTING, stack) != 0 || EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, stack) != 0 || EnchantmentHelper.getEnchantmentLevel(Enchantments.SMITE, stack) != 0 || EnchantmentHelper.getEnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS, stack) != 0 || EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING, stack) != 0) {
            stack.getTagCompound().setBoolean("enchantmentsInvalid", true);
         }

         stack.getTagCompound().setBoolean("enchChecked", true);
      }

      if (!ConfigHandler.forceDisableUncraftableTooltips && !this.canBeCrafted) {
         tooltip.add(TextFormatting.RED + StringHelper.translateFormattedString("cantCraftMissingMaterial", "tooltip", "spartanweaponry", StringHelper.translateString(this.materialEx.getOreDictForRepairMaterial(), "material", this.materialEx.getModId())));
      }

      if (stack.hasTagCompound()) {
         NBTTagCompound tag = stack.getTagCompound();
         if (tag.getBoolean("enchantmentsInvalid")) {
            tooltip.add(TextFormatting.RED + StringHelper.translateString("invalidEnchantments", "tooltip"));
         }

         if (tag.hasKey("Original") && !tag.getBoolean("Original")) {
            tooltip.add(TextFormatting.DARK_RED + StringHelper.translateString("throwable.not_original", "tooltip", "spartanweaponry"));
         }

         if (tag.hasKey("AmmoUsed")) {
            int mxAmmo = this.getMaxAmmo(stack);
            tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("throwable.ammo", "tooltip", "spartanweaponry", TextFormatting.GRAY + StringHelper.translateFormattedString("throwable.ammo.value", "tooltip", "spartanweaponry", mxAmmo - tag.getInteger("AmmoUsed"), mxAmmo)));
         }
      }

      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("throwable.charge_time", "tooltip", "spartanweaponry", TextFormatting.GRAY + StringHelper.translateFormattedString("throwable.charge_time.value", "tooltip", (float)this.getMaxChargeTicks(stack) / 20.0F)));
      if (stack.hasTagCompound()) {
         NBTTagCompound tag = stack.getTagCompound();
         if (tag.hasUniqueId("UUID") && flagIn == TooltipFlags.ADVANCED) {
            tooltip.add(TextFormatting.DARK_PURPLE + "UUID: " + TextFormatting.GRAY + "" + tag.getUniqueId("UUID").toString());
         }
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
         String name = I18n.translateToLocalFormatted(String.format("item.%s:%s.name", "spartanweaponry", this.displayName), new Object[]{I18n.translateToLocal(this.materialEx.getFullUnlocName())});
         return name;
      } else {
         return super.getItemStackDisplayName(stack);
      }
   }

   @Nullable
   public String getCreatorModId(ItemStack itemStack) {
      return this.modId != null ? this.modId : super.getCreatorModId(itemStack);
   }

   public ItemThrowingWeapon addWeaponProperty(WeaponProperty prop) {
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

   protected void initNBT(ItemStack stack, boolean initUUID) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      NBTTagCompound tag = stack.getTagCompound();
      if (!tag.hasKey("AmmoUsed")) {
         if (tag.hasKey("Ammo")) {
            Log.info("Found old ammo in item" + stack.getItem().getRegistryName().toString() + "! Converted to new ammo system using a new ammo stack");
            tag.setInteger("AmmoUsed", this.getMaxAmmo(stack) - tag.getInteger("Ammo"));
            tag.removeTag("Ammo");
         }

         tag.setInteger("AmmoUsed", 0);
      }

      if (initUUID && !tag.hasUniqueId("UUID")) {
         stack.getTagCompound().setUniqueId("UUID", UUID.randomUUID());
         stack.getTagCompound().setBoolean("Original", true);
      }

   }

   public int getMaxAmmo(ItemStack stack) {
      int level = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_AMMO, stack);
      int increasePerLevel = Math.max((int)((float)this.maxAmmo * 0.25F), 1);
      return this.maxAmmo + increasePerLevel * level;
   }

   public int getMaxAmmoBase() {
      return this.maxAmmo;
   }

   public int getMaxChargeTicks(ItemStack stack) {
      return (int)((float)this.maxChargeTicks * (1.0F - (float)EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_CHARGE, stack) * 0.2F));
   }
}
