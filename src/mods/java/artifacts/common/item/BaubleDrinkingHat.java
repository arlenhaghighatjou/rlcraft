package artifacts.common.item;

import artifacts.common.init.ModItems;
import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Start;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public abstract class BaubleDrinkingHat {
   public BaubleDrinkingHat() {
   }

   @SubscribeEvent
   public static void onItemUseStart(Start event) {
      if (event.getEntityLiving() instanceof EntityPlayer && BaublesApi.isBaubleEquipped((EntityPlayer)event.getEntityLiving(), ModItems.DRINKING_HAT) != -1 && event.getItem().getItemUseAction() == EnumAction.DRINK) {
         event.setDuration(event.getDuration() / 4);
      }

   }
}
