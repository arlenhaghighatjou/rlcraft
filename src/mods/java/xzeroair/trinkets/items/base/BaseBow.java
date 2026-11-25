package xzeroair.trinkets.items.base;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.util.interfaces.IAccessoryInterface;
import xzeroair.trinkets.util.interfaces.IsModelLoaded;

public class BaseBow extends ItemBow implements IsModelLoaded, IAccessoryInterface {
   public BaseBow(String name) {
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      this.setMaxStackSize(1);
      this.setCreativeTab(Trinkets.trinketstab);
      this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (entityIn == null) {
               return 0.0F;
            } else {
               return entityIn.getActiveItemStack().getItem() != Items.BOW ? 0.0F : (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F;
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

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
   }

   public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
      super.onArmorTick(world, player, itemStack);
   }

   public ItemStack findAmmo(EntityPlayer player) {
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
                  entityarrow.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);
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

                  if (stack.getItemDamage() == 0) {
                     if (this.getMaxItemUseDuration(stack) - timeLeft < 6) {
                        return;
                     }

                     float f1 = getArrowVelocity(this.getMaxItemUseDuration(stack) - timeLeft);
                     worldIn.playSound((EntityPlayer)null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f1 * 0.5F);
                     stack.damageItem(1, entityLiving);
                  }

                  if (stack.getItemDamage() != 0) {
                     worldIn.spawnEntity(entityarrow);
                     worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                  }
               }

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

   public static float getArrowVelocity(int charge) {
      float f = (float)charge / 20.0F;
      f = (f * f + f * 2.0F) / 3.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 72000;
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BOW;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      boolean flag = !this.findAmmo(playerIn).isEmpty();
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

   public int getItemEnchantability() {
      return 1;
   }

   public int getMetadata(int metadata) {
      return metadata;
   }

   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName();
   }

   public void registerModels() {
      Trinkets.proxy.registerItemRenderer(this, 0, "inventory");
   }

   public int getSlot(ItemStack stack) {
      return -1;
   }

   public String getItemHandler(ItemStack stack) {
      return "None";
   }
}
