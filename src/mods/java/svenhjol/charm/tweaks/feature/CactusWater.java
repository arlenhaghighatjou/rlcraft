package svenhjol.charm.tweaks.feature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.ItemHelper;
import svenhjol.meson.helper.PlayerHelper;
import svenhjol.meson.helper.SoundHelper;

public class CactusWater extends Feature {
   public static double chance;

   public CactusWater() {
   }

   public String getDescription() {
      return "Right click a cactus with an empty glass bottle for a chance to fill the bottle with water.";
   }

   public void configure() {
      super.configure();
      chance = 0.15D;
   }

   @SubscribeEvent
   public void onUse(RightClickBlock event) {
      if (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.CACTUS && event.getEntityPlayer().getHeldItem(event.getHand()).getItem() == Items.GLASS_BOTTLE) {
         if (event.getWorld().isRemote) {
            SoundHelper.playerSound(event.getEntityPlayer(), SoundEvents.ITEM_BOTTLE_EMPTY, 0.5F, 1.1F);
         } else {
            if ((double)event.getWorld().rand.nextFloat() <= Math.min(1.0D, chance * 2.0D)) {
               event.getWorld().destroyBlock(event.getPos(), event.getWorld().rand.nextBoolean());
            }

            if ((double)event.getWorld().rand.nextFloat() <= chance) {
               EntityPlayer player = event.getEntityPlayer();
               EnumHand hand = event.getHand();
               ItemStack out = ItemHelper.getFilledWaterBottle();
               PlayerHelper.setHeldItem(player, hand, out);
            }
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
