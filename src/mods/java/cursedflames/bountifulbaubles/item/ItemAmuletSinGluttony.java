package cursedflames.bountifulbaubles.item;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Finish;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Start;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Tick;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemAmuletSinGluttony extends ItemAmuletSin {
   public ItemAmuletSinGluttony() {
      super("amuletSinGluttony", "amulet_sin_gluttony");
   }

   @SubscribeEvent
   public static void onItemUse(Tick event) {
      if (event.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         if (BaublesApi.isBaubleEquipped(player, ModItems.sinPendantGluttony) != -1) {
            ItemStack stack = event.getItem();
            EnumAction action = stack.getItem().getItemUseAction(stack);
            if ((action == EnumAction.EAT || action == EnumAction.DRINK) && event.getDuration() > 7) {
               event.setDuration(7);
            }
         }
      }

   }

   @SubscribeEvent
   public static void onItemUse(Start event) {
      if (event.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         if (BaublesApi.isBaubleEquipped(player, ModItems.sinPendantGluttony) != -1) {
            ItemStack stack = event.getItem();
            EnumAction action = stack.getItem().getItemUseAction(stack);
            if ((action == EnumAction.EAT || action == EnumAction.DRINK) && event.getDuration() > 7) {
               event.setDuration(7);
            }
         }
      }

   }

   @SubscribeEvent
   public static void onItemUse(Finish event) {
      if (event.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         if (BaublesApi.isBaubleEquipped(player, ModItems.sinPendantGluttony) != -1) {
            ItemStack stack = event.getItem();
            EnumAction action = stack.getItem().getItemUseAction(stack);
            if (action == EnumAction.EAT) {
               float level = 0.0F;
               if (stack.getItem() instanceof ItemFood) {
                  ItemFood item = (ItemFood)stack.getItem();
                  level = (float)(item.getHealAmount(stack) / 4);
                  level = level + item.getSaturationModifier(stack) / 6.0F;
               }

               addEffect(player, (int)Math.floor((double)level), 200, true);
            }
         }
      }

   }
}
