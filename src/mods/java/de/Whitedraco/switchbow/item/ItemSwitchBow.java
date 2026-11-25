package de.Whitedraco.switchbow.item;

import de.Whitedraco.switchbow.EnchantInit;
import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.Config.EnchantmentConfig;
import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import de.Whitedraco.switchbow.helper.ArrowItemStackEqual;
import de.Whitedraco.switchbow.helper.QuiverArrowHelper;
import de.Whitedraco.switchbow.helper.SwitchBowHelper;
import java.util.Vector;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSwitchBow extends ItemBow {
   private static final String TAG_BOW_INFO = "tag_bowinfo";
   private static final String TAG_BOW_DROWSPEED = "tag_drowspeed";

   public ItemSwitchBow(String UnlocalizedName) {
      this.setRegistryName(UnlocalizedName);
      this.setUnlocalizedName(UnlocalizedName);
      this.setMaxDamage(SwitchBowConfig.durabilitySwitchBow.getInteger());
      this.setCreativeTab(Initial.tabSwitchbow);
      this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (entityIn == null) {
               return 0.0F;
            } else {
               ItemStack itemstack = entityIn.getActiveItemStack();
               return itemstack != ItemStack.EMPTY && itemstack.getItem() instanceof ItemSwitchBow ? (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) * ItemSwitchBow.getPullSpeed(itemstack) / 20.0F : 0.0F;
            }
         }
      });
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return toRepair.getItem() instanceof ItemSwitchBow && repair.getItem() == Items.ENDER_EYE;
   }

   public int getItemEnchantability() {
      return SwitchBowConfig.enchantabilitySwitchBow.getInteger();
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BOW;
   }

   private ItemStack findAmmo(EntityPlayer player, ItemStack bow) {
      return ArrowItemStackEqual.containsArrow(QuiverArrowHelper.getArrowsInInvAndQuiver(player, player.inventory), SwitchBowHelper.getSelectionArrow(bow)) ? SwitchBowHelper.getSelectionArrow(bow) : ItemStack.EMPTY;
   }

   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      if (entityLiving instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)entityLiving;
         boolean flag = entityplayer.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
         ItemStack shootArrow = this.findAmmo(entityplayer, stack);
         int i = (int)((float)(this.getMaxItemUseDuration(stack) - timeLeft) * getPullSpeed(stack));
         i = ForgeEventFactory.onArrowLoose(stack, worldIn, (EntityPlayer)entityLiving, i, shootArrow != ItemStack.EMPTY || flag);
         if (i < 0) {
            return;
         }

         if (shootArrow != ItemStack.EMPTY || flag) {
            if (shootArrow == ItemStack.EMPTY) {
               shootArrow = new ItemStack(Items.ARROW);
            }

            float f = getArrowVelocity(i);
            if ((double)f >= 0.1D) {
               boolean flag1 = entityplayer.capabilities.isCreativeMode || shootArrow.getItem() instanceof ItemArrow && ((ItemArrow)shootArrow.getItem()).isInfinite(shootArrow, stack, entityplayer);
               if (!worldIn.isRemote) {
                  ItemArrow itemarrow = (ItemArrow)(shootArrow.getItem() instanceof ItemArrow ? shootArrow.getItem() : Items.ARROW);
                  EntityArrow entityarrow = itemarrow.createArrow(worldIn, shootArrow, entityplayer);
                  entityarrow.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);
                  if (f == 1.0F) {
                     entityarrow.setIsCritical(true);
                  }

                  int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                  if (j > 0) {
                     entityarrow.setDamage(entityarrow.getDamage() + (double)j * 0.5D + 0.5D);
                  }

                  if (SwitchBowConfig.extraDamageSwitchBow.getDoub() > 0.0D) {
                     entityarrow.setDamage(entityarrow.getDamage() + SwitchBowConfig.extraDamageSwitchBow.getDoub());
                  }

                  if (!SwitchBowConfig.didDamageSwitchBow.getBool()) {
                     entityarrow.setDamage(0.0D);
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
                  QuiverArrowHelper.removeArrow(entityplayer, shootArrow);
               }

               entityplayer.addStat(StatList.getObjectUseStats(this));
            }
         }
      }

   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      boolean flag = !this.findAmmo(playerIn, itemstack).isEmpty();
      ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, handIn, flag);
      if (ret != null) {
         return ret;
      } else if (!playerIn.capabilities.isCreativeMode && !flag) {
         return flag ? new ActionResult(EnumActionResult.PASS, itemstack) : new ActionResult(EnumActionResult.FAIL, itemstack);
      } else {
         playerIn.setActiveHand(handIn);
         return new ActionResult(EnumActionResult.SUCCESS, itemstack);
      }
   }

   public void onCreated(ItemStack itemStack, World par2World, EntityPlayer par3EntityPlayer) {
      itemStack.setTagCompound(new NBTTagCompound());
      this.setPullSpeed(itemStack, SwitchBowConfig.drawSpeedSwitchBow.getFloa());
   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      float isPullSpeed = pullSpeedwithEnchantment(EnchantmentHelper.getEnchantmentLevel(EnchantInit.PullSpeed, stack));
      if (isPullSpeed != getPullSpeed(stack)) {
         this.setPullSpeed(stack, isPullSpeed);
      }

      if (!worldIn.isRemote && entityIn instanceof EntityPlayerMP) {
         EntityPlayerMP Player = (EntityPlayerMP)entityIn;
         if (Player.getHeldItemMainhand() != ItemStack.EMPTY && Player.getHeldItemMainhand().getItem() instanceof ItemSwitchBow || Player.getHeldItemOffhand() != ItemStack.EMPTY && Player.getHeldItemOffhand().getItem() instanceof ItemSwitchBow) {
            ItemStack bow = ItemStack.EMPTY;
            if (Player.getHeldItemMainhand() != ItemStack.EMPTY && Player.getHeldItemMainhand().getItem() instanceof ItemSwitchBow) {
               bow = Player.getHeldItemMainhand();
            } else if (Player.getHeldItemOffhand() != ItemStack.EMPTY && Player.getHeldItemOffhand().getItem() instanceof ItemSwitchBow) {
               bow = Player.getHeldItemOffhand();
            }

            new Vector();
            Vector<ItemStack> arrowsininv = QuiverArrowHelper.getArrowsInInvAndQuiver(Player, Player.inventory);
            if (SwitchBowHelper.getSelectionArrow(bow) == ItemStack.EMPTY && arrowsininv.size() != 0) {
               SwitchBowHelper.setSelectionArrow(bow, (ItemStack)arrowsininv.get(0));
            }

            if (SwitchBowHelper.getSelectionArrow(bow) != ItemStack.EMPTY && !ArrowItemStackEqual.containsArrow(arrowsininv, SwitchBowHelper.getSelectionArrow(bow))) {
               if (arrowsininv.size() != 0) {
                  SwitchBowHelper.setSelectionArrow(bow, (ItemStack)arrowsininv.get(0));
               } else if (arrowsininv.size() == 0) {
                  SwitchBowHelper.setSelectionArrow(bow, ItemStack.EMPTY);
               }
            }

            if (arrowsininv.size() == 0 && SwitchBowHelper.getSelectionArrow(bow) != ItemStack.EMPTY) {
               SwitchBowHelper.setSelectionArrow(bow, ItemStack.EMPTY);
            }
         }
      }

   }

   public static float pullSpeedwithEnchantment(int enchantmentlvl) {
      float pullspeed = SwitchBowConfig.drawSpeedSwitchBow.getFloa();
      return pullspeed * (float)(EnchantmentConfig.enchantPullingSpeedProcent.getInteger() * enchantmentlvl + 100) / 100.0F;
   }

   public static float getPullSpeed(ItemStack bow) {
      if (bow.getTagCompound() != null) {
         NBTTagCompound data = bow.getTagCompound().getCompoundTag("tag_bowinfo");
         return data != null ? data.getFloat("tag_drowspeed") : SwitchBowConfig.drawSpeedSwitchBow.getFloa();
      } else {
         return SwitchBowConfig.drawSpeedSwitchBow.getFloa();
      }
   }

   public void setPullSpeed(ItemStack bow, float drowspeed) {
      NBTTagCompound data = new NBTTagCompound();
      data.setFloat("tag_drowspeed", drowspeed);
      bow.setTagInfo("tag_bowinfo", data);
   }
}
