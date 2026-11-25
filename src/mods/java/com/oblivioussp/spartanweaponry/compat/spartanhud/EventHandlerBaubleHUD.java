package com.oblivioussp.spartanweaponry.compat.spartanhud;

import com.oblivioussp.spartanhudbaubles.client.event.RenderBaubleOnHUDEvent.Post;
import com.oblivioussp.spartanhudbaubles.client.gui.AlignmentHelper.Alignment;
import com.oblivioussp.spartanhudbaubles.client.gui.AlignmentHelper.HorizontalAlignment;
import com.oblivioussp.spartanhudbaubles.client.gui.AlignmentHelper.VerticalAlignment;
import com.oblivioussp.spartanweaponry.client.KeyBinds;
import com.oblivioussp.spartanweaponry.event.EventHandlerClient;
import com.oblivioussp.spartanweaponry.init.OreDictionarySW;
import com.oblivioussp.spartanweaponry.item.ItemQuiverBase;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.QuiverHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EventHandlerBaubleHUD {
   public EventHandlerBaubleHUD() {
   }

   @SubscribeEvent
   public void onRenderBaubleHUD(Post ev) {
      if (!ConfigHandler.disableSpartanHUDBaublesIntegration) {
         if (OreDictionarySW.matches("spartanweaponry:quivers", ev.getItemStack())) {
            ItemStack quiver = ev.getItemStack();
            ItemQuiverBase quiverItem = (ItemQuiverBase)ev.getItemStack().getItem();
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer font = ev.getFontRenderer();
            int totalAmmo = 0;
            String ammoStr = "";
            if (!quiver.hasTagCompound()) {
               quiver.setTagCompound(new NBTTagCompound());
            }

            NBTTagList list = quiver.getTagCompound().getTagList("ClientInventory", 10);

            for(int i = 0; i < list.tagCount(); ++i) {
               ItemStack item = new ItemStack(list.getCompoundTagAt(i));
               if (!item.isEmpty() && item.getCount() != 0) {
                  int ammo = item.getCount();
                  totalAmmo += ammo;
               }
            }

            ammoStr = String.format("%d", totalAmmo);
            GlStateManager.disableDepth();
            font.drawStringWithShadow(ammoStr, (float)(ev.getPositionX() + 17 - font.getStringWidth(ammoStr)), (float)(ev.getPositionY() + 10), totalAmmo == 0 ? 16736352 : 16760832);
            if (QuiverHelper.isInBaublesSlot(mc.player, EventHandlerClient.getActiveQuiverStack())) {
               ;
            }

            Alignment align = ev.getAlignment();
            VerticalAlignment vert = align.getVertical();
            HorizontalAlignment horiz = align.getHorizontal();
            boolean vertical = ev.isVertical();
            int keybindXOffset = ev.getPositionX();
            int keybindYOffset = ev.getPositionY();
            if (!KeyBinds.KEY_ACCESS_QUIVER.getDisplayName().equals("NONE")) {
               String inventoryKey = "[" + KeyBinds.KEY_ACCESS_QUIVER.getDisplayName().toUpperCase() + "]";
               int keyWidth = font.getStringWidth(inventoryKey);
               int keyTextXOffset = horiz == HorizontalAlignment.RIGHT && !vertical && keyWidth > 19 ? -keyWidth + 19 : (horiz == HorizontalAlignment.LEFT && !vertical && keyWidth > 19 && keybindXOffset < 19 ? -2 : (horiz == HorizontalAlignment.RIGHT && vertical ? -keyWidth - 4 : (vertical ? 20 : 8 - (int)((float)keyWidth / 2.0F))));
               int keyTextYOffset = vert == VerticalAlignment.TOP && !vertical ? 20 : (vertical ? 5 : -11);
               font.drawStringWithShadow(inventoryKey, (float)(keybindXOffset + keyTextXOffset), (float)(keybindYOffset + keyTextYOffset), 16777215);
            }

            GlStateManager.enableDepth();
         }

      }
   }
}
