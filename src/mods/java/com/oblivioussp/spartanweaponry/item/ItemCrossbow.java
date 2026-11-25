package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.IWeaponCallback;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBolt;
import com.oblivioussp.spartanweaponry.init.EnchantmentRegistrySW;
import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import com.oblivioussp.spartanweaponry.init.SoundRegistry;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.NBTHelper;
import com.oblivioussp.spartanweaponry.util.Quaternion;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Enchantments;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemCrossbow extends ItemSW implements IHudLoadState {
   protected ToolMaterialEx material;
   protected String modId;
   protected String displayName;
   protected boolean doCraftCheck;
   protected boolean canBeCrafted;
   protected IWeaponCallback callback;
   public static final String NBT_IS_LOADED = "isLoaded";
   public static final String nbtAmmoStack = "ammoStack";

   public ItemCrossbow(String unlocName, ToolMaterialEx toolMaterial) {
      super(unlocName);
      this.modId = null;
      this.displayName = null;
      this.doCraftCheck = true;
      this.canBeCrafted = true;
      this.callback = null;
      this.material = toolMaterial;
      this.maxStackSize = 1;
      this.setMaxDamage((int)((float)toolMaterial.getMaxUses() * 1.5F));
      this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (entityIn == null && !(entityIn instanceof EntityPlayer)) {
               return 0.0F;
            } else {
               ItemStack itemstack = entityIn.getActiveItemStack();
               if (NBTHelper.getBoolean(stack, "isLoaded")) {
                  return 1.0F;
               } else {
                  return itemstack != null && itemstack.isItemEqual(stack) && itemstack.getItem() instanceof ItemCrossbow ? ItemCrossbow.this.getLoadProgress(stack, entityIn) : 0.0F;
               }
            }
         }
      });
      this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
         }
      });
   }

   public ItemCrossbow(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
      this.displayName = "crossbow_custom";
   }

   public ItemCrossbow(String unlocName, String externalModId, ToolMaterialEx material, IWeaponCallback weaponCallback) {
      this(unlocName, externalModId, material);
      this.callback = weaponCallback;
   }

   public String getUnlocalizedName() {
      return this.modId != null ? StringHelper.getItemUnlocalizedName(super.getUnlocalizedName(), this.modId) : super.getUnlocalizedName();
   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return this.modId != null ? StringHelper.getItemUnlocalizedName(super.getUnlocalizedName(), this.modId) : super.getUnlocalizedName();
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return !ItemStack.areItemStackTagsEqual(oldStack, newStack) ? true : super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
   }

   protected ItemStack findAmmo(EntityPlayer player) {
      if (this.isBolt(player.getHeldItem(EnumHand.OFF_HAND))) {
         return player.getHeldItem(EnumHand.OFF_HAND);
      } else if (this.isBolt(player.getHeldItem(EnumHand.MAIN_HAND))) {
         return player.getHeldItem(EnumHand.MAIN_HAND);
      } else {
         for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            if (this.isBolt(itemstack)) {
               return itemstack;
            }
         }

         return ItemStack.EMPTY;
      }
   }

   protected boolean isBolt(@Nullable ItemStack stack) {
      return stack != null && stack.getItem() instanceof ItemBolt;
   }

   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      if (!NBTHelper.getBoolean(stack, "isLoaded") && entityLiving instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLiving;
         ItemStack bolt = this.findAmmo(player);
         if (bolt.isEmpty()) {
            bolt = new ItemStack(ItemRegistrySW.bolt);
         }

         boolean isCreativeOrArrowInfinite = player.capabilities.isCreativeMode || bolt.getItem() instanceof ItemBolt && ((ItemBolt)bolt.getItem()).isInfinite(bolt, stack, player);
         int count = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.CROSSBOW_SPREADSHOT, stack) > 0 ? 3 : 1;
         ItemStack boltToStore = bolt.copy();
         boltToStore.setCount(count);
         NBTTagCompound nbtBolt = new NBTTagCompound();
         boltToStore.writeToNBT(nbtBolt);
         NBTHelper.setTagCompound(stack, "ammoStack", nbtBolt);
         if (!isCreativeOrArrowInfinite) {
            bolt.shrink(1);
            if (bolt.isEmpty()) {
               player.inventory.deleteStack(bolt);
            }
         }

         worldIn.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundRegistry.CROSSBOW_LOAD, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) * 0.5F);
         player.getCooldownTracker().setCooldown(this, ConfigHandler.crossbowTicksCooldown);
         NBTHelper.setBoolean(stack, "isLoaded", true);
      }

      return stack;
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      if (entityLiving instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)entityLiving;
         boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
         ItemStack itemstack = ItemStack.EMPTY;
         NBTTagCompound tag = NBTHelper.getTagCompound(stack, "ammoStack");
         if (tag != null) {
            itemstack = new ItemStack(tag);
         }

         int i = this.getMaxItemUseDuration(stack) - timeLeft;
         if (i < 0 || !NBTHelper.getBoolean(stack, "isLoaded")) {
            return;
         }

         if (!itemstack.isEmpty() || flag) {
            if (itemstack.isEmpty()) {
               itemstack = new ItemStack(ItemRegistrySW.bolt);
            }

            float vel = this.getBoltSpeed();
            boolean flag1 = entityplayer.capabilities.isCreativeMode || itemstack.getItem() instanceof ItemBolt && ((ItemBolt)itemstack.getItem()).isInfinite(itemstack, stack, entityplayer);
            if (!worldIn.isRemote) {
               ItemBolt itemBolt = (ItemBolt)(itemstack.getItem() instanceof ItemBolt ? itemstack.getItem() : ItemRegistrySW.bolt);
               int aimTicks = this.getAimTicks(stack);
               int inaccuracy = aimTicks - i;
               float inaccuracyModifier = 0.0F;
               if (i >= aimTicks) {
                  inaccuracy = 0;
               }

               if (inaccuracy != 0) {
                  inaccuracyModifier = 10.0F * ((float)inaccuracy / (float)aimTicks);
               }

               this.spawnProjectile(stack, itemBolt, itemstack, worldIn, entityplayer, flag1, inaccuracyModifier, 0.0F);
               if (itemstack.getCount() > 1) {
                  this.spawnProjectile(stack, itemBolt, itemstack, worldIn, entityplayer, true, inaccuracyModifier, -10.0F);
                  this.spawnProjectile(stack, itemBolt, itemstack, worldIn, entityplayer, true, inaccuracyModifier, 10.0F);
               }

               int damage = itemstack.getCount() > 1 ? 3 : 1;
               stack.damageItem(damage, entityplayer);
               NBTHelper.setBoolean(stack, "isLoaded", false);
               NBTHelper.setTagCompound(stack, "ammoStack", new NBTTagCompound());
            }

            worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundRegistry.CROSSBOW_FIRE, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + vel * 0.5F);
            entityplayer.addStat(StatList.getObjectUseStats(this));
         }
      }

   }

   public float getBoltSpeed() {
      return ConfigHandler.crossbowBaseBoltSpeed + (float)this.material.getHarvestLevel() * ConfigHandler.crossbowMaterialBoltSpeedModifier;
   }

   protected void spawnProjectile(ItemStack crossbow, ItemBolt boltItem, ItemStack boltStack, World world, EntityPlayer player, boolean noPickup, float inaccuracyModifier, float projectileAngle) {
      EntityBolt bolt = boltItem.createBolt(world, boltStack, player);
      bolt.setIsCritical(true);
      Vec3d lookVec = player.getLook(1.0F);
      Vec3d vector = new Vec3d(lookVec.x, lookVec.y, lookVec.z);
      if (projectileAngle != 0.0F) {
         Vec3d playerUpVec = this.calculateEntityViewVector(player.rotationPitch - 90.0F, player.rotationYaw);
         Quaternion quat = new Quaternion(playerUpVec, projectileAngle, true);
         vector = quat.transformVector(lookVec);
      }

      bolt.shoot(vector.x, vector.y, vector.z, this.getBoltSpeed() * 3.0F, inaccuracyModifier);
      int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, crossbow);
      if (j > 0) {
         bolt.setDamage(bolt.getDamage() + (double)j * 0.5D + 0.5D);
      }

      int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, crossbow);
      if (k > 0) {
         bolt.setKnockbackStrength(k);
      }

      if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, crossbow) > 0) {
         bolt.setFire(100);
      }

      if (noPickup || projectileAngle != 0.0F) {
         bolt.pickupStatus = PickupStatus.CREATIVE_ONLY;
      }

      world.spawnEntity(bolt);
   }

   private Vec3d calculateEntityViewVector(float pitch, float yaw) {
      float degToRad = 0.017453292F;
      float yawCos = MathHelper.cos(-yaw * degToRad - 3.1415927F);
      float yawSin = MathHelper.sin(-yaw * degToRad - 3.1415927F);
      float pitchCos = -MathHelper.cos(-pitch * degToRad - 3.1415927F);
      float pitchSin = MathHelper.sin(-pitch * degToRad - 3.1415927F);
      return new Vec3d((double)(yawSin * pitchCos), (double)pitchSin, (double)(yawCos * pitchCos));
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return !NBTHelper.getBoolean(stack, "isLoaded") ? this.getFullLoadTicks(stack) : 72000;
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return !NBTHelper.getBoolean(stack, "isLoaded") ? EnumAction.NONE : EnumAction.BOW;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack stack = playerIn.getHeldItem(handIn);
      boolean flag = false;
      flag = !this.findAmmo(playerIn).isEmpty();
      if (!playerIn.capabilities.isCreativeMode && !NBTHelper.getBoolean(stack, "isLoaded") && !flag && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) <= 0) {
         return !flag ? new ActionResult(EnumActionResult.FAIL, stack) : new ActionResult(EnumActionResult.PASS, stack);
      } else {
         playerIn.setActiveHand(handIn);
         return new ActionResult(EnumActionResult.SUCCESS, stack);
      }
   }

   public int getItemEnchantability(ItemStack stack) {
      return this.material == null ? 1 : this.material.getEnchantability();
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      if (!toRepair.isEmpty() && !repair.isEmpty()) {
         return this.material.doesOreDictMatch(repair) ? true : super.getIsRepairable(toRepair, repair);
      } else {
         return false;
      }
   }

   public String getItemStackDisplayName(ItemStack stack) {
      if (this.displayName != null) {
         String name = I18n.translateToLocalFormatted(String.format("item.%s:%s.name", "spartanweaponry", this.displayName), new Object[]{I18n.translateToLocal(this.material.getFullUnlocName())});
         return name;
      } else {
         return super.getItemStackDisplayName(stack);
      }
   }

   @Nullable
   public String getCreatorModId(ItemStack itemStack) {
      return this.modId != null ? this.modId : super.getCreatorModId(itemStack);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      boolean isShiftPressed = GuiScreen.isShiftKeyDown();
      if (this.doCraftCheck && worldIn != null) {
         if (this.material.getModId() == "spartanweaponry") {
            List<ItemStack> ores = OreDictionary.getOres(this.material.getOreDictForRepairMaterial(), false);
            if (ores == null || ores.isEmpty()) {
               this.canBeCrafted = false;
            }
         }

         this.doCraftCheck = false;
      } else if (!ConfigHandler.forceDisableUncraftableTooltips && !this.canBeCrafted) {
         tooltip.add(TextFormatting.RED + StringHelper.translateFormattedString("cantCraftMissingMaterial", "tooltip", "spartanweaponry", StringHelper.translateString(this.material.getOreDictForRepairMaterial(), "material", this.material.getModId())));
      }

      ItemStack bolt = ItemStack.EMPTY;
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      if (stack.getTagCompound().hasKey("ammoStack")) {
         ;
      }

      bolt = new ItemStack(stack.getTagCompound().getCompoundTag("ammoStack"));
      if (bolt != null && !bolt.isEmpty()) {
         tooltip.add(StringHelper.translateFormattedString("crossbow.loaded_bolt", "tooltip", "spartanweaponry", String.format("[%s x%d]", TextFormatting.AQUA.toString() + bolt.getDisplayName() + TextFormatting.WHITE.toString(), bolt.getCount())));
         tooltip.add("");
      }

      if (this.callback != null) {
         this.callback.onTooltip(this.material, stack, worldIn, tooltip, flagIn);
         tooltip.add("");
      }

      tooltip.add(TextFormatting.GOLD + StringHelper.translateFormattedString("description", "tooltip", "spartanweaponry", isShiftPressed ? TextFormatting.DARK_GRAY + StringHelper.translateString("showingDescription", "tooltip", "spartanweaponry") : TextFormatting.DARK_GRAY + StringHelper.translateFormattedString("showDescription", "tooltip", "spartanweaponry", TextFormatting.AQUA + "SHIFT" + TextFormatting.DARK_GRAY)));
      if (GuiScreen.isShiftKeyDown()) {
         tooltip.add(StringHelper.translateString("crossbow", "tooltip"));
         tooltip.add(StringHelper.translateString("crossbow.desc", "tooltip"));
         tooltip.add(StringHelper.translateString("crossbow.desc2", "tooltip"));
      }

      tooltip.add("");
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("modifiers.ammo.type", "tooltip", "spartanweaponry", TextFormatting.GRAY + StringHelper.translateString("modifiers.ammo.bolt", "tooltip")));
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("modifiers.crossbow.load_time", "tooltip", "spartanweaponry", TextFormatting.GRAY + "" + (float)this.getFullLoadTicks(stack) / 20.0F));
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("modifiers.crossbow.aim_time", "tooltip", "spartanweaponry", TextFormatting.GRAY + "" + (float)this.getAimTicks(stack) / 20.0F));
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("modifiers.crossbow.speed_multiplier", "tooltip", "spartanweaponry", TextFormatting.GRAY + StringHelper.translateFormattedString("modifiers.crossbow.speed_multiplier.value", "tooltip", "spartanweaponry", this.getBoltSpeed())));
      tooltip.add("");
   }

   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      if (this.callback != null) {
         this.callback.onCreateItem(this.material, stack);
      }

      super.onCreated(stack, worldIn, playerIn);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         ItemStack stack = new ItemStack(this);
         if (this.callback != null) {
            this.callback.onCreateItem(this.material, stack);
         }

         items.add(stack);
      }

   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (this.callback != null && entityIn instanceof EntityLivingBase) {
         this.callback.onItemUpdate(this.material, stack, worldIn, (EntityLivingBase)entityIn, itemSlot, isSelected);
      }

      super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
   }

   public String getHighlightTip(ItemStack item, String displayName) {
      if (!item.hasTagCompound()) {
         item.setTagCompound(new NBTTagCompound());
      }

      ItemStack stack = new ItemStack(item.getTagCompound().getCompoundTag("ammoStack"));
      return !stack.isEmpty() ? String.format("%s (%s x%d)", displayName, stack.getDisplayName(), stack.getCount()) : displayName;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return ConfigHandler.enchantmentWhitelistCrossbow.contains(enchantment.getRegistryName()) || super.canApplyAtEnchantingTable(stack, enchantment);
   }

   public boolean isLoaded(ItemStack stack) {
      return !stack.hasTagCompound() ? false : stack.getTagCompound().getBoolean("isLoaded");
   }

   public float getLoadProgress(ItemStack stack, EntityLivingBase entity) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      int fullLoadTicks = this.getFullLoadTicks(stack);
      return entity.isHandActive() && !stack.getTagCompound().getBoolean("isLoaded") ? ((float)fullLoadTicks == 0.0F ? 1.0F : 1.0F - MathHelper.clamp((float)this.getLoadingTicks(stack, entity) / (float)fullLoadTicks, 0.0F, 1.0F)) : 0.0F;
   }

   protected int getFullLoadTicks(ItemStack stack) {
      int i = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.CROSSBOW_CHARGE, stack);
      return Math.max(ConfigHandler.crossbowTicksToLoad - 4 * i + this.material.getHarvestLevel() * ConfigHandler.crossbowMaterialLoadModifier, 1);
   }

   protected int getLoadingTicks(ItemStack stack, EntityLivingBase entity) {
      return entity.getItemInUseCount();
   }

   public int getAimTicks(ItemStack stack) {
      int i = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.CROSSBOW_SHARPSHOOTER, stack);
      return Math.max(10 - 2 * i + this.material.getHarvestLevel() * ConfigHandler.crossbowMaterialAimModifier, 1);
   }
}
