package codersafterdark.reskillable.skill.magic;

import codersafterdark.reskillable.api.unlockable.Trait;
import codersafterdark.reskillable.base.ExperienceHelper;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class TraitGoldenOsmosis extends Trait {
   public TraitGoldenOsmosis() {
      super(new ResourceLocation("reskillable", "golden_osmosis"), 3, 2, new ResourceLocation("reskillable", "magic"), 10, "reskillable:magic|20", "reskillable:mining|6", "reskillable:gathering|6", "reskillable:attack|6");
   }

   public void onPlayerTick(PlayerTickEvent event) {
      if (!event.player.world.isRemote) {
         this.tryRepair(event.player, event.player.getHeldItemMainhand());
         this.tryRepair(event.player, event.player.getHeldItemOffhand());
         event.player.inventory.armorInventory.forEach((stack) -> {
            this.tryRepair(event.player, stack);
         });
      }

   }

   private void tryRepair(EntityPlayer player, ItemStack stack) {
      if (!stack.isEmpty()) {
         int damage = stack.getItemDamage();
         if (damage > 2) {
            Item item = stack.getItem();
            if (item.isRepairable() && item.getIsRepairable(stack, new ItemStack(Items.GOLD_INGOT)) && ExperienceHelper.getPlayerXP(player) > 0) {
               ExperienceHelper.drainPlayerXP(player, 1);
               stack.setItemDamage(damage - 3);
            }
         }
      }

   }
}
