package lykrast.defiledlands.common.item;

import java.util.List;
import javax.annotation.Nullable;
import lykrast.defiledlands.common.init.ModItems;
import lykrast.defiledlands.common.util.LocUtils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemConcussionSmasher extends Item implements IEnchantDestructive {
   private static final float USE_TIME = 7.0F;

   public ItemConcussionSmasher(int durability) {
      this.maxStackSize = 1;
      this.setMaxDamage(durability);
      this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
            if (entityIn == null) {
               return 0.0F;
            } else {
               return entityIn.getActiveItemStack().getItem() != ModItems.concussionSmasher ? 0.0F : (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 7.0F;
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

   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
      if (entityLiving instanceof EntityPlayer) {
         EntityPlayer entityplayer = (EntityPlayer)entityLiving;
         int i = this.getMaxItemUseDuration(stack) - timeLeft;
         float f = getExplosionStrength(i);
         if ((double)f >= 0.1D) {
            if (!worldIn.isRemote) {
               f = f * 2.0F * this.getDestructiveBonus(stack);
               worldIn.createExplosion(entityLiving, entityLiving.posX, entityLiving.posY + (double)entityLiving.getEyeHeight() - 0.1D, entityLiving.posZ, f, false);
               stack.damageItem(1, entityplayer);
            }

            entityplayer.addStat(StatList.getObjectUseStats(this));
         }
      }

   }

   public static float getExplosionStrength(int charge) {
      float f = (float)charge / 7.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 72000;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      playerIn.setActiveHand(handIn);
      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   public int getItemEnchantability() {
      return 1;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return repair.getItem() == ModItems.umbriumIngot;
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + I18n.format(super.getUnlocalizedName(stack) + ".tooltip", new Object[0])));
   }
}
