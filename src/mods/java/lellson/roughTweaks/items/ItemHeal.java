package lellson.roughTweaks.items;

import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHeal extends Item {
   private final int healRate;
   private final float healAmount;
   private final PotionEffect effect;
   private final ItemStack returnStack;

   public ItemHeal(String name, int useCount, int healRate, float healAmount, PotionEffect effect, ItemStack returnStack) {
      this.healRate = healRate;
      this.healAmount = healAmount;
      this.effect = effect;
      this.returnStack = returnStack;
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      this.setCreativeTab(CreativeTabs.MISC);
      this.setMaxStackSize(1);
      this.setMaxDamage(useCount);
      if (useCount > 0) {
         RoughItems.ITEMS.add(this);
      }

   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (player.capabilities.isCreativeMode) {
         return new ActionResult(EnumActionResult.PASS, stack);
      } else {
         player.setActiveHand(hand);
         return new ActionResult(EnumActionResult.SUCCESS, stack);
      }
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 72000;
   }

   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BOW;
   }

   public boolean hasEffect(ItemStack stack) {
      return this.effect != null;
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
      if (count % this.healRate == 1) {
         stack.damageItem(1, player);
         player.heal(this.healAmount);
         if (this.effect != null) {
            player.addPotionEffect(new PotionEffect(this.effect));
         }

         if (stack.getItemDamage() >= this.getMaxDamage(stack)) {
            player.setHeldItem(player.getActiveHand(), this.returnStack == null ? ItemStack.EMPTY : this.returnStack);
            player.playSound(SoundEvents.BLOCK_CLOTH_PLACE, 1.0F, 0.5F);
            player.stopActiveHand();
         } else {
            player.playSound(SoundEvents.BLOCK_CLOTH_PLACE, 1.0F, 1.5F);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
      if (GuiScreen.isShiftKeyDown()) {
         float hearts = this.healAmount / 2.0F;
         if ((double)hearts % 1.0D == 0.0D) {
            tooltip.add(TextFormatting.BLUE + "Heal Amount: " + (int)hearts + " Hearts");
         } else {
            tooltip.add(TextFormatting.BLUE + "Heal Amount: " + hearts + " Hearts");
         }
      }

   }
}
