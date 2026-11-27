package com.oblivioussp.spartanweaponry.item;

import net.lax1dude.eaglercraft.v1_8.HString;
import com.oblivioussp.spartanweaponry.api.IWeaponCallback;
import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.client.gui.CreativeTabsSW;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class ItemLongbow extends ItemBow implements IHudQuiverDisplay {
   protected ToolMaterialEx material;
   protected String modId;
   protected String displayName;
   protected boolean doCraftCheck;
   protected boolean canBeCrafted;
   protected IWeaponCallback callback;

   public ItemLongbow(String unlocName, ToolMaterialEx toolMaterial) {
      this.modId = null;
      this.displayName = null;
      this.doCraftCheck = true;
      this.canBeCrafted = true;
      this.callback = null;
      this.maxStackSize = 1;
      this.material = toolMaterial;
      this.setMaxDamage((int)((float)toolMaterial.getMaxUses() * 1.5F));
      this.setCreativeTab(CreativeTabsSW.TAB_SW);
      this.setRegistryName(unlocName);
      this.setUnlocalizedName(unlocName);
      this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (entityIn == null) {
               return 0.0F;
            } else {
               ItemStack itemstack = entityIn.getActiveItemStack();
               return !itemstack.isEmpty() ? MathHelper.clamp((float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / (float)ItemLongbow.this.getDrawTicks(), 0.0F, 1.0F) : 0.0F;
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

   public ItemLongbow(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
      this.displayName = "longbow_custom";
   }

   public ItemLongbow(String unlocName, String externalModId, ToolMaterialEx material, IWeaponCallback weaponCallback) {
      this(unlocName, externalModId, material);
      this.callback = weaponCallback;
   }

   public String getUnlocalizedName() {
      return this.modId != null ? StringHelper.getItemUnlocalizedName(super.getUnlocalizedName(), this.modId) : StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return this.modId != null ? StringHelper.getItemUnlocalizedName(super.getUnlocalizedName(), this.modId) : StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }

   protected ItemStack findAmmo(EntityPlayer player) {
      if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
         return player.getHeldItem(EnumHand.OFF_HAND);
      } else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
         return player.getHeldItem(EnumHand.MAIN_HAND);
      } else {
         for(int i = 0; i < player.inventory.getSizeInventory(); ++i) {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            if (this.isArrow(itemstack)) {
               return itemstack;
            }
         }

         return ItemStack.EMPTY;
      }
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      if (entityLiving instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)entityLiving;
         boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
         ItemStack itemstack = this.findAmmo(entityplayer);
         int i = this.getMaxItemUseDuration(stack) - timeLeft;
         i = ForgeEventFactory.onArrowLoose(stack, worldIn, (EntityPlayer)entityLiving, i, itemstack != null || flag);
         if (i < 0) {
            return;
         }

         if (!itemstack.isEmpty() || flag) {
            if (itemstack.isEmpty()) {
               itemstack = new ItemStack(Items.ARROW);
            }

            float f = this.getArrowSpeed(i);
            if ((double)f >= 0.1D) {
               boolean flag1 = entityplayer.capabilities.isCreativeMode || itemstack.getItem() instanceof ItemArrow && ((ItemArrow)itemstack.getItem()).isInfinite(itemstack, stack, entityplayer);
               if (!worldIn.isRemote) {
                  ItemArrow itemarrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
                  EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
                  entityarrow.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 0.5F);
                  if (i == this.getDrawTicks()) {
                     entityarrow.setIsCritical(true);
                  }

                  int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                  if (j > 0) {
                     entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5D + 0.5D);
                  }

                  int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                  if (k > 0) {
                     entityarrow.setKnockbackStrength(k);
                  }

                  if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                     entityarrow.setFire(100);
                  }

                  stack.damageItem(1, entityplayer);
                  if (flag1) {
                     entityarrow.pickupStatus = PickupStatus.CREATIVE_ONLY;
                  }

                  worldIn.spawnEntity(entityarrow);
               }

               worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
               if (!flag1) {
                  itemstack.shrink(1);
                  if (itemstack.isEmpty()) {
                     entityplayer.inventory.deleteStack(itemstack);
                  }
               }

               entityplayer.addStat(StatList.getObjectUseStats(this));
            }
         }
      }

   }

   public float getArrowSpeed(int charge) {
      float f = (float)charge / 20.0F;
      f = (f * f + f * 2.0F) / 3.0F;
      float maxArrowSpeed = this.getMaxArrowSpeed();
      if (f > maxArrowSpeed) {
         f = maxArrowSpeed;
      }

      return f;
   }

   public int getArrowSlotToUseFirst(@Nonnull IItemHandler quiverHandler) {
      for(int i = 0; i < quiverHandler.getSlots(); ++i) {
         ItemStack stack = quiverHandler.getStackInSlot(i);
         if (!stack.isEmpty()) {
            return i;
         }
      }

      return -1;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack bow = playerIn.getHeldItem(handIn);
      boolean flag = false;
      flag = !this.findAmmo(playerIn).isEmpty() || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow) > 0;
      ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(bow, worldIn, playerIn, handIn, flag);
      if (ret != null) {
         return ret;
      } else if (!playerIn.capabilities.isCreativeMode && !flag) {
         return flag ? new ActionResult(EnumActionResult.PASS, bow) : new ActionResult(EnumActionResult.FAIL, bow);
      } else {
         playerIn.setActiveHand(handIn);
         return new ActionResult(EnumActionResult.SUCCESS, bow);
      }
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
         String name = I18n.translateToLocalFormatted(HString.format("item.%s:%s.name", "spartanweaponry", this.displayName), new Object[]{I18n.translateToLocal(this.material.getFullUnlocName())});
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

      if (this.callback != null) {
         this.callback.onTooltip(this.material, stack, worldIn, tooltip, flagIn);
         tooltip.add("");
      }

      tooltip.add(TextFormatting.GOLD + StringHelper.translateFormattedString("description", "tooltip", "spartanweaponry", isShiftPressed ? TextFormatting.DARK_GRAY + StringHelper.translateString("showingDescription", "tooltip", "spartanweaponry") : TextFormatting.DARK_GRAY + StringHelper.translateFormattedString("showDescription", "tooltip", "spartanweaponry", TextFormatting.AQUA + "SHIFT" + TextFormatting.DARK_GRAY)));
      if (GuiScreen.isShiftKeyDown()) {
         tooltip.add(StringHelper.translateString("longbow", "tooltip"));
         tooltip.add(StringHelper.translateString("longbow.desc", "tooltip"));
      }

      tooltip.add("");
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("modifiers.ammo.type", "tooltip", "spartanweaponry", TextFormatting.GRAY + StringHelper.translateString("modifiers.ammo.arrow", "tooltip")));
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("modifiers.longbow.draw_length", "tooltip", "spartanweaponry", TextFormatting.GRAY + "" + (float)this.getDrawTicks() / 20.0F));
      tooltip.add(TextFormatting.DARK_AQUA + StringHelper.translateFormattedString("modifiers.longbow.speed_multiplier", "tooltip", "spartanweaponry", TextFormatting.GRAY + StringHelper.translateFormattedString("modifiers.longbow.speed_multiplier.value", "tooltip", "spartanweaponry", this.getMaxArrowSpeed())));
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

   public Class<? extends ItemQuiverBase> getRequiredQuiverClass() {
      return ItemQuiverArrow.class;
   }

   public int getDrawTicks() {
      return MathHelper.floor(ConfigHandler.multiplierLongbow * 20.0F) + this.material.getHarvestLevel() * ConfigHandler.materialDrawModifierLongbow;
   }

   public float getMaxArrowSpeed() {
      return ConfigHandler.multiplierLongbow + (float)this.material.getHarvestLevel() * ConfigHandler.materialSpeedModifierLongbow;
   }
}
