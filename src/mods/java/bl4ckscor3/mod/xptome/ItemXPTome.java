package bl4ckscor3.mod.xptome;

import java.util.List;
import java.util.Random;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import openmods.utils.EnchantmentUtils;

public class ItemXPTome extends Item {
   public static final String NAME = "xp_book";
   public static final int MAX_STORAGE = 1395;
   private final Random random = new Random();

   public ItemXPTome() {
      this.setCreativeTab(CreativeTabs.MISC);
      this.setMaxDamage(1395);
      this.setMaxStackSize(1);
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (player.isSneaking() && this.getXPStored(stack) != 1395) {
         int playerXP = EnchantmentUtils.getPlayerXP(player);
         if (playerXP == 0) {
            return new ActionResult(EnumActionResult.PASS, stack);
         } else {
            int actuallyStored = this.addXP(stack, playerXP);
            EnchantmentUtils.addPlayerXP(player, -actuallyStored);
            if (!world.isRemote) {
               world.playSound((EntityPlayer)null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.1F, (this.random.nextFloat() - this.random.nextFloat()) * 0.35F + 0.9F);
            }

            return new ActionResult(EnumActionResult.SUCCESS, stack);
         }
      } else if (!player.isSneaking() && this.getXPStored(stack) != 0) {
         EnchantmentUtils.addPlayerXP(player, this.getXPStored(stack));
         this.setStoredXP(stack, 0);
         if (!world.isRemote) {
            float pitchMultiplier = player.experienceLevel > 30 ? 1.0F : (float)player.experienceLevel / 30.0F;
            world.playSound((EntityPlayer)null, player.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, pitchMultiplier * 0.75F, 1.0F);
         }

         return new ActionResult(EnumActionResult.SUCCESS, stack);
      } else {
         return new ActionResult(EnumActionResult.PASS, stack);
      }
   }

   public boolean hasEffect(ItemStack stack) {
      return this.getXPStored(stack) > 0;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }

   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return false;
   }

   public boolean isRepairable() {
      return false;
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
      tooltip.add(I18n.format("xpbook.tooltip.1", new Object[0]));
      tooltip.add(I18n.format("xpbook.tooltip.2", new Object[0]));
      tooltip.add(I18n.format("xpbook.tooltip.3", new Object[]{this.getXPStored(stack), Integer.valueOf(1395)}));
   }

   public int addXP(ItemStack stack, int amount) {
      int stored = this.getXPStored(stack);
      if (stored + amount > 1395) {
         this.setStoredXP(stack, 1395);
         return 1395 - stored;
      } else {
         this.setStoredXP(stack, stored + amount);
         return amount;
      }
   }

   public void setStoredXP(ItemStack stack, int amount) {
      stack.setItemDamage(1395 - amount);
   }

   public int getXPStored(ItemStack stack) {
      return 1395 - stack.getItemDamage();
   }
}
