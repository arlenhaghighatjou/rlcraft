package lellson.foodexpansion.items;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import lellson.foodexpansion.FoodExpansionConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;

public class ItemEventHandler {
   public ItemEventHandler() {
   }

   public void register(EventBus bus) {
      bus.register(Finish.class, this::itemUsed);
   }

   public void itemUsed(Finish event) {
      if (event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         if (this.isBowl(event.getItem().getItem())) {
            player.inventory.addItemStackToInventory(event.getResultStack().copy());
            event.setResultStack(event.getItem().copy());
         }
      }

   }

   private boolean isBowl(Item item) {
      for(String s : FoodExpansionConfig.bowlStackSizeItems) {
         Item bowl = Item.getByNameOrId(s);
         if (bowl != null && item != null && bowl.equals(item)) {
            return true;
         }
      }

      return false;
   }
}
