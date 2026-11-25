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

public class ItemSwitchCrossbow extends ItemBow {
   private static final String TAG_BOW_INFO = "tag_bowinfo";
   private static final String TAG_BOW_DROWSPEED = "tag_drowspeed";
   public static final int noGravity = 70;

   public ItemSwitchCrossbow(String UnlocalizedName) {
      this.setRegistryName(UnlocalizedName);
      this.setUnlocalizedName(UnlocalizedName);
      this.setMaxDamage(SwitchBowConfig.durabilitySwitchCrossBow.getInteger());
      this.setCreativeTab(Initial.tabSwitchbow);
      this.addPropertyOverride(new ResourceLocation("cooldown"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (entityIn == null) {
               return 0.0F;
            } else {
               return entityIn instanceof EntityPlayer ? ((EntityPlayer)entityIn).getCooldownTracker().getCooldown(stack.getItem(), 0.0F) : 0.0F;
            }
         }
      });
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return toRepair.getItem() instanceof ItemSwitchCrossbow && repair.getItem() == Items.ENDER_EYE;
   }

   public int getItemEnchantability() {
      return SwitchBowConfig.enchantabilitySwitchCrossBow.getInteger();
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
         int i = 1;
         i = ForgeEventFactory.onArrowLoose(stack, worldIn, (EntityPlayer)entityLiving, i, shootArrow != ItemStack.EMPTY || flag);
         if (shootArrow != ItemStack.EMPTY || flag) {
            if (shootArrow == ItemStack.EMPTY) {
               shootArrow = new ItemStack(Items.ARROW);
            }

            boolean flag1 = entityplayer.capabilities.isCreativeMode || shootArrow.getItem() instanceof ItemArrow && ((ItemArrow)shootArrow.getItem()).isInfinite(shootArrow, stack, entityplayer);
            float f = 1.0F;
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

               if (SwitchBowConfig.extraDamageSwitchCrossBow.getDoub() > 0.0D) {
                  entityarrow.setDamage(entityarrow.getDamage() + SwitchBowConfig.extraDamageSwitchCrossBow.getDoub());
               }

               if (!SwitchBowConfig.didDamageSwitchCrossBow.getBool()) {
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

               entityarrow.getEntityData().setInteger("NoGravity", 70);
               worldIn.spawnEntity(entityarrow);
            }

            worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
            ItemStack bow = ItemStack.EMPTY;
            if (entityplayer.getHeldItemMainhand() != ItemStack.EMPTY && entityplayer.getHeldItemMainhand().getItem() instanceof ItemSwitchCrossbow) {
               bow = entityplayer.getHeldItemMainhand();
            } else if (entityplayer.getHeldItemOffhand() != ItemStack.EMPTY && entityplayer.getHeldItemOffhand().getItem() instanceof ItemSwitchCrossbow) {
               bow = entityplayer.getHeldItemOffhand();
            }

            if (!flag1) {
               QuiverArrowHelper.removeArrow(entityplayer, shootArrow);
            }

            entityplayer.addStat(StatList.getObjectUseStats(this));
         }

         int cooldown = SwitchBowConfig.cooldownSwitchCrossBow.getInteger();
         if (cooldown > 0) {
            if (EnchantmentHelper.getEnchantmentLevel(EnchantInit.CrossbowCooldownReduce, stack) > 0) {
               cooldown = -Math.round((float)SwitchBowConfig.cooldownSwitchCrossBow.getInteger() * (float)(EnchantmentHelper.getEnchantmentLevel(EnchantInit.CrossbowCooldownReduce, stack) * EnchantmentConfig.enchantreducedCooldownProcent.getInteger() - 100) / 100.0F);
            }

            entityplayer.getCooldownTracker().setCooldown(this, cooldown);
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
   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (!worldIn.isRemote && entityIn instanceof EntityPlayerMP) {
         EntityPlayerMP Player = (EntityPlayerMP)entityIn;
         if (Player.getHeldItemMainhand() != ItemStack.EMPTY && Player.getHeldItemMainhand().getItem() instanceof ItemSwitchCrossbow || Player.getHeldItemOffhand() != ItemStack.EMPTY && Player.getHeldItemOffhand().getItem() instanceof ItemSwitchCrossbow) {
            ItemStack bow = ItemStack.EMPTY;
            if (Player.getHeldItemMainhand() != ItemStack.EMPTY && Player.getHeldItemMainhand().getItem() instanceof ItemSwitchCrossbow) {
               bow = Player.getHeldItemMainhand();
            } else if (Player.getHeldItemOffhand() != ItemStack.EMPTY && Player.getHeldItemOffhand().getItem() instanceof ItemSwitchCrossbow) {
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

      if (entityIn instanceof EntityPlayerMP) {
         EntityPlayerMP Player = (EntityPlayerMP)entityIn;
         if (Player.getHeldItemMainhand() != ItemStack.EMPTY && Player.getHeldItemMainhand().getItem() instanceof ItemSwitchCrossbow || Player.getHeldItemOffhand() != ItemStack.EMPTY && Player.getHeldItemOffhand().getItem() instanceof ItemSwitchCrossbow) {
            ItemStack bow = ItemStack.EMPTY;
            if (Player.getHeldItemMainhand() != ItemStack.EMPTY && Player.getHeldItemMainhand().getItem() instanceof ItemSwitchCrossbow) {
               bow = Player.getHeldItemMainhand();
            } else if (Player.getHeldItemOffhand() != ItemStack.EMPTY && Player.getHeldItemOffhand().getItem() instanceof ItemSwitchCrossbow) {
               bow = Player.getHeldItemOffhand();
            }

            if (this.findAmmo(Player, bow) == ItemStack.EMPTY && !Player.capabilities.isCreativeMode) {
               Player.getCooldownTracker().setCooldown(this, 20);
            }
         }
      }

   }
}
