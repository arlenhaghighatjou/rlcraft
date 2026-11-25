package com.endertech.minecraft.forge.items;

import com.endertech.common.Args;
import com.endertech.common.CommonMath;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IForgeEnum;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.units.IForgeUnit;
import com.endertech.minecraft.forge.units.UnitEnabled;
import com.endertech.minecraft.forge.units.UnitId;
import com.endertech.minecraft.forge.units.UnitVariant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ForgeItem extends Item implements IForgeUnit {
   private final UnitEnabled unitEnabled;
   private final ForgeConfig config;
   private final UnitId id;
   private final ForgeMod mod;
   private Map<Object, UnitVariant> variants;
   private final UnitVariant defaultVariant;

   public ForgeItem(ForgeMod mod, UnitConfig config, CreativeTabs tab) {
      this(mod, config, "", tab);
   }

   public ForgeItem(ForgeMod mod, UnitConfig config, String name, CreativeTabs tab) {
      this.variants = new HashMap();
      this.mod = mod;
      this.id = this.createId(mod.getId(), name);
      this.config = config;
      this.unitEnabled = new UnitEnabled(config, this.getUnitCategory(), true);
      this.defaultVariant = UnitVariant.from(this.id, IForgeUnit.Variants.INVENTORY);
      this.setRegistryName(this.getId().toResLoc());
      this.setUnlocalizedName(this.getId().getRegName());
      this.setCreativeTab(tab);
      this.register();
   }

   public static EaglercraftUUID getAttackDamageModifierUUID() {
      return ATTACK_DAMAGE_MODIFIER;
   }

   public static EaglercraftUUID getAttackSpeedModifierUUID() {
      return ATTACK_SPEED_MODIFIER;
   }

   public static List<String> getOreDictNamesFor(ItemStack stack) {
      if (stack.isEmpty()) {
         return Collections.emptyList();
      } else {
         int[] oreIds = OreDictionary.getOreIDs(stack);
         List<String> oreNames = new ArrayList(oreIds.length);

         for(int i = 0; i < oreIds.length; ++i) {
            oreNames.add(OreDictionary.getOreName(oreIds[i]));
         }

         return oreNames;
      }
   }

   /** @deprecated */
   @Nullable
   @Deprecated
   public static ItemStack getItemStackOrNull(IForgeUnit unit, int amount) {
      String CANT_CREATE_STACK = "Can't create stack: {}";
      Logger logger = ForgeMain.instance.getLogger();
      if (unit != null) {
         if (unit.isRegistered()) {
            if (unit instanceof Block) {
               return new ItemStack((Block)unit, amount);
            }

            if (unit instanceof Item) {
               return new ItemStack((Item)unit, amount);
            }

            logger.error("Can't create stack: {}", "specified unit is neither block nor item!");
         } else {
            logger.error("Can't create stack: {}", "unit is not registered!");
         }
      } else {
         logger.error("Can't create stack: {}", "unit is NULL!");
      }

      return null;
   }

   @Nullable
   public static ItemStack findItemStackInOreDict(String oreDictName, int amount) {
      if (oreDictName != null) {
         List<ItemStack> list = OreDictionary.getOres(oreDictName, false);
         if (list != null && !list.isEmpty()) {
            ItemStack stack = (ItemStack)list.get(0);
            if (stack != null) {
               ItemStack copy = stack.copy();
               copy.setCount(amount);
               return copy;
            }
         }
      }

      return null;
   }

   public static String getStringId(Item item) {
      return UnitId.from(item, 0).toString();
   }

   public String toString() {
      return ForgeItem.class.getSimpleName() + Args.group(Args.get("status", IForgeUnit.getStatus(this)), Args.get("regName", this.getId().getRegName()), Args.get("enabled", Boolean.valueOf(this.isEnabled())));
   }

   public boolean isEnabled() {
      return this.unitEnabled.isEnabled();
   }

   public boolean isRegistered() {
      return isRegistered(this);
   }

   public void onInit() {
      this.saveConfig();
   }

   public boolean isReadyToRegister() {
      return true;
   }

   public UnitId getId() {
      return this.id;
   }

   public ForgeConfig getConfig() {
      return this.config;
   }

   public static void damageItem(int damage, ItemStack stack, @Nullable EntityLivingBase damager, boolean destroyBroken) {
      EntityPlayerMP player = damager instanceof EntityPlayerMP ? (EntityPlayerMP)damager : null;
      if (player != null && !player.isCreative()) {
         Item item = stack.getItem();
         if (item instanceof DamageableItem) {
            ((DamageableItem)item).updateDurability(stack, player);
         }

         if (stack != null && stack.isItemStackDamageable()) {
            if (stack.attemptDamageItem(damage, CommonMath.RANDOM, player)) {
               stack.setItemDamage(stack.getMaxDamage());
            }

            if (destroyBroken && isBroken(stack)) {
               damager.renderBrokenItemStack(stack);
               stack.shrink(1);
               stack.setItemDamage(0);
               if (player != null) {
                  player.addStat(StatList.getObjectBreakStats(stack.getItem()));
               }
            }
         }

      }
   }

   public static boolean isBroken(ItemStack stack) {
      return stack.isItemStackDamageable() && stack.getItemDamage() >= stack.getMaxDamage();
   }

   public static boolean notBroken(ItemStack stack) {
      return !isBroken(stack);
   }

   public static boolean isRegistered(Item item) {
      return item != null && ForgeRegistries.ITEMS.containsValue(item);
   }

   public static boolean hasNBT(ItemStack stack) {
      return stack != null && stack.hasTagCompound();
   }

   public static void ensureHasNBT(ItemStack stack) {
      if (stack != null && !hasNBT(stack)) {
         stack.setTagCompound(new NBTTagCompound());
      }

   }

   public ForgeMod getMod() {
      return this.mod;
   }

   public Map<Object, UnitVariant> getUnitVariants() {
      return this.variants;
   }

   @SideOnly(Side.CLIENT)
   public void onInitClient() {
   }

   @Nullable
   public CreativeTabs getCreativeTab() {
      return this.displayOnCreativeTab() ? super.getCreativeTab() : null;
   }

   public UnitVariant getDefaultVariant() {
      return this.defaultVariant;
   }

   public <E extends Enum<E> & IForgeEnum> void setUnitVariants(Class<E> clazz) {
      this.variants = this.createUnitVariants(clazz);
   }
}
