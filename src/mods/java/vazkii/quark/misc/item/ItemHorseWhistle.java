package vazkii.quark.misc.item;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.misc.ai.EntityAIHorseFollow;
import vazkii.quark.misc.feature.HorseWhistle;

public class ItemHorseWhistle extends ItemMod implements IQuarkItem {
   public ItemHorseWhistle(String name) {
      super(name, new String[0]);
      this.setCreativeTab(CreativeTabs.MISC);
      this.setMaxStackSize(1);
   }

   private void callHorses(EntityLivingBase player) {
      for(AbstractHorse horse : player.world.getEntitiesWithinAABB(AbstractHorse.class, player.getEntityBoundingBox().grow(HorseWhistle.horseSummonRange))) {
         if (horse.getDistanceSq(player) <= HorseWhistle.horseSummonRange * HorseWhistle.horseSummonRange) {
            for(EntityAITaskEntry task : horse.tasks.taskEntries) {
               if (task.action instanceof EntityAIHorseFollow) {
                  ((EntityAIHorseFollow)task.action).setOwner(player);
               }
            }
         }
      }

   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 40;
   }

   @Nonnull
   public EnumAction getItemUseAction(ItemStack stack) {
      return EnumAction.BOW;
   }

   public void onUsingTick(ItemStack stack, EntityLivingBase player, int count) {
      if (!player.world.isRemote && count >= 20) {
         this.callHorses(player);
      }

   }

   @Nonnull
   public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World world, EntityLivingBase player) {
      if (!world.isRemote) {
         this.callHorses(player);
      }

      if (player instanceof EntityPlayer) {
         ((EntityPlayer)player).getCooldownTracker().setCooldown(this, 20);
      }

      return stack;
   }

   public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft) {
      if (!world.isRemote && timeLeft <= 20) {
         this.callHorses(player);
      }

      if (player instanceof EntityPlayer) {
         ((EntityPlayer)player).getCooldownTracker().setCooldown(this, 20);
      }

   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
      ItemStack held = player.getHeldItem(hand);
      if (!world.isRemote) {
         world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, QuarkSounds.ITEM_HORSE_WHISTLE_BLOW, SoundCategory.PLAYERS, 1.0F, 1.0F);
      }

      if (!player.isHandActive()) {
         player.setActiveHand(hand);
      }

      return ActionResult.newResult(EnumActionResult.SUCCESS, held);
   }
}
