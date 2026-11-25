package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WeaponToolRangeBase extends ItemBow {
   private int bonus;
   private int damage;
   private int damageCap;
   private boolean calling;
   private byte idTool;

   public WeaponToolRangeBase(String name, int durability, int bonus, int damageC, int damage, boolean fear, byte id) {
      this.setRegistryName("weapon_" + name);
      this.setUnlocalizedName("srparasites.weapon_" + name);
      this.maxStackSize = 1;
      this.bonus = bonus;
      this.damageCap = damageC;
      this.damage = damage;
      this.calling = fear;
      this.idTool = id;
      this.setMaxDamage(durability);
      this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (entityIn == null) {
               return 0.0F;
            } else {
               return !(entityIn.getActiveItemStack().getItem() instanceof WeaponToolRangeBase) ? 0.0F : (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 50.0F;
            }
         }
      });
      this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
         }
      });
      this.addPropertyOverride(new ResourceLocation("vinni"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            return SRPConfig.useVinniT ? 1.0F : 0.0F;
         }
      });
   }

   private ItemStack findAmmo(EntityPlayer player) {
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

   protected boolean isArrow(ItemStack stack) {
      return stack.getItem() instanceof ItemArrow;
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      if (entityLiving instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)entityLiving;
         boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
         ItemStack itemstack = this.findAmmo(entityplayer);
         int i = this.getMaxItemUseDuration(stack) - timeLeft;
         i = ForgeEventFactory.onArrowLoose(stack, worldIn, entityplayer, i, !itemstack.isEmpty() || flag);
         if (i < 0) {
            return;
         }

         double seconds = (double)((72000 - timeLeft) / 20);
         double bonusD = seconds * (double)this.bonus;
         if (!itemstack.isEmpty() || flag) {
            if (itemstack.isEmpty()) {
               itemstack = new ItemStack(Items.ARROW);
            }

            float f = getArrowVelocity(i);
            if ((double)f >= 0.1D) {
               boolean flag1 = entityplayer.capabilities.isCreativeMode || itemstack.getItem() instanceof ItemArrow && ((ItemArrow)itemstack.getItem()).isInfinite(itemstack, stack, entityplayer);
               if (!worldIn.isRemote) {
                  ItemArrow itemarrow = (ItemArrow)(itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
                  EntityArrow entityarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
                  entityarrow.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 4.4F, 0.0F);
                  if (f == 1.0F) {
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
                  if (flag1 || entityplayer.capabilities.isCreativeMode && (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
                     entityarrow.pickupStatus = PickupStatus.CREATIVE_ONLY;
                  }

                  if (bonusD > (double)(this.damage * this.damageCap)) {
                     bonusD = (double)(this.damage * this.damageCap);
                  }

                  entityarrow.setDamage(entityarrow.getDamage() * bonusD);
                  entityarrow.setDamage(entityarrow.getDamage() + (double)this.damage);
                  worldIn.spawnEntity(entityarrow);
               }

               worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
               if (!flag1 && !entityplayer.capabilities.isCreativeMode) {
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

   public int getItemEnchantability() {
      return 10;
   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
      if (!worldIn.isRemote && this.calling && SRPConfigSystems.useOneMind && worldIn.rand.nextInt(10) == 0 && entityIn.ticksExisted % 40 == 0) {
         ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(SRPPotions.PREY_E, 1200, 0, false, false));
      }

   }

   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      super.onCreated(stack, worldIn, playerIn);
   }

   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.YELLOW + I18n.format("tootip.srparasites.weaponr." + this.idTool, new Object[0]));
      tooltip.add(TextFormatting.RED + I18n.format("tootip.srparasites.weaponr." + this.idTool * 10, new Object[0]));
      if (this.calling && SRPConfigSystems.useOneMind) {
         tooltip.add(TextFormatting.BLACK + I18n.format("tootip.srparasites.weaponr." + this.idTool * 100, new Object[0]));
      }

   }
}
