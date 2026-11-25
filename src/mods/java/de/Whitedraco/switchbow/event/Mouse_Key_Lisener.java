package de.Whitedraco.switchbow.event;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.helper.ArrowItemStackEqual;
import de.Whitedraco.switchbow.helper.QuiverArrowHelper;
import de.Whitedraco.switchbow.helper.SwitchBowHelper;
import de.Whitedraco.switchbow.item.ItemSwitchBow;
import de.Whitedraco.switchbow.item.ItemSwitchCrossbow;
import de.Whitedraco.switchbow.proxy.ClientProxy;
import de.Whitedraco.switchbow.proxy.SwitchBowHandler;
import java.util.Vector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Mouse_Key_Lisener {
   public Mouse_Key_Lisener() {
   }

   @SubscribeEvent
   public void keylisener(KeyInputEvent event) {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      ItemStack stack = ItemStack.EMPTY;
      int hand = 0;
      if (player.getHeldItemMainhand() == ItemStack.EMPTY || !(player.getHeldItemMainhand().getItem() instanceof ItemSwitchBow) && !(player.getHeldItemMainhand().getItem() instanceof ItemSwitchCrossbow)) {
         if (player.getHeldItemOffhand() != ItemStack.EMPTY && (player.getHeldItemOffhand().getItem() instanceof ItemSwitchBow || player.getHeldItemOffhand().getItem() instanceof ItemSwitchCrossbow)) {
            stack = player.getHeldItemOffhand();
            hand = 1;
         }
      } else {
         stack = player.getHeldItemMainhand();
      }

      if (stack != ItemStack.EMPTY) {
         if (ClientProxy.SwitchBow_KeyBinding_Up.isKeyDown()) {
            this.changeArrowtyp(player, stack, 1, hand);
         } else if (ClientProxy.SwitchBow_KeyBinding_Down.isPressed()) {
            this.changeArrowtyp(player, stack, -1, hand);
         }
      }

   }

   @SubscribeEvent
   public void shiftandscroll(MouseEvent event) {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      if (event.getDwheel() != 0 && player != null && player.isSneaking()) {
         ItemStack stack = ItemStack.EMPTY;
         int hand = 0;
         if (player.getHeldItemMainhand() == ItemStack.EMPTY || !(player.getHeldItemMainhand().getItem() instanceof ItemSwitchBow) && !(player.getHeldItemMainhand().getItem() instanceof ItemSwitchCrossbow)) {
            if (player.getHeldItemOffhand() != ItemStack.EMPTY && (player.getHeldItemOffhand().getItem() instanceof ItemSwitchBow || player.getHeldItemOffhand().getItem() instanceof ItemSwitchCrossbow)) {
               stack = player.getHeldItemOffhand();
               hand = 1;
            }
         } else {
            stack = player.getHeldItemMainhand();
         }

         if (stack != ItemStack.EMPTY) {
            this.changeArrowtyp(player, stack, event.getDwheel(), hand);
            event.setCanceled(true);
         }
      }

   }

   public void changeArrowtyp(EntityPlayerSP player, ItemStack stack, int Dwheel, int hand) {
      Vector<ItemStack> findedArrows = QuiverArrowHelper.getArrowsInInvAndQuiver(player, player.inventory);
      int arrowsize = findedArrows.size();
      ItemStack selectionItem = ItemStack.EMPTY;
      if (stack.getItem() instanceof ItemSwitchBow || stack.getItem() instanceof ItemSwitchCrossbow) {
         selectionItem = SwitchBowHelper.getSelectionArrow(stack);
      }

      if (arrowsize != 0 && arrowsize != 1) {
         int search = 0;

         for(int a = 0; a < arrowsize && !ArrowItemStackEqual.containsArrow((ItemStack)findedArrows.get(a), selectionItem); ++a) {
            ++search;
         }

         if (Dwheel < 0) {
            --search;
            if (search <= -1) {
               this.sendnewArrow((ItemStack)findedArrows.get(arrowsize - 1), hand);
            } else {
               this.sendnewArrow((ItemStack)findedArrows.get(search), hand);
            }
         } else if (Dwheel > 0) {
            ++search;
            if (search >= arrowsize) {
               this.sendnewArrow((ItemStack)findedArrows.get(0), hand);
            } else {
               this.sendnewArrow((ItemStack)findedArrows.get(search), hand);
            }
         }
      }

   }

   private void sendnewArrow(ItemStack newArrow, int hand) {
      Initial.network.sendToServer(new SwitchBowHandler(newArrow, hand));
   }
}
