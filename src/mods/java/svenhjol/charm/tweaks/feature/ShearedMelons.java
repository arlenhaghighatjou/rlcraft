package svenhjol.charm.tweaks.feature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.ForgeHelper;

public class ShearedMelons extends Feature {
   public static float chance;
   public static int maxPieces;

   public ShearedMelons() {
   }

   public String getDescription() {
      return "Using shears to break a melon has a chance to drop all 9 melon pieces.";
   }

   public boolean isEnabled() {
      return this.enabled && !ForgeHelper.areModsLoaded("inspirations");
   }

   public void configure() {
      super.configure();
      chance = 0.75F;
      maxPieces = 9;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onBreak(HarvestDropsEvent event) {
      if (!event.getWorld().isRemote && event.getHarvester() != null && event.getState().getBlock() == Blocks.MELON_BLOCK) {
         EntityPlayer player = event.getHarvester();
         if ((player.getHeldItemMainhand().getItem() == Items.SHEARS || player.getHeldItemOffhand().getItem() == Items.SHEARS) && event.getWorld().rand.nextFloat() <= chance) {
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(Items.MELON, maxPieces));
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
