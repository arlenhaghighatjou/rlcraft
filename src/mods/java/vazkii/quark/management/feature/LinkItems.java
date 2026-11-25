package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageLinkItem;

public class LinkItems extends Feature {
   public LinkItems() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void keyboardEvent(Post event) {
      if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindChat) && event.getGui() instanceof GuiContainer && GuiScreen.isShiftKeyDown()) {
         GuiContainer gui = (GuiContainer)event.getGui();
         Slot slot = gui.getSlotUnderMouse();
         if (slot != null && slot.inventory != null && !"tmp".equals(slot.inventory.getName())) {
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && !MinecraftForge.EVENT_BUS.post(new ClientChatEvent(stack.getTextComponent().getUnformattedText()))) {
               NetworkHandler.INSTANCE.sendToServer(new MessageLinkItem(stack));
            }
         }
      }

   }

   public static void linkItem(EntityPlayer player, ItemStack item) {
      if (ModuleLoader.isFeatureEnabled(LinkItems.class)) {
         if (!item.isEmpty() && player instanceof EntityPlayerMP) {
            ITextComponent comp = item.getTextComponent();
            ITextComponent fullComp = new TextComponentTranslation("chat.type.text", new Object[]{player.getDisplayName(), comp});
            PlayerList players = ((EntityPlayerMP)player).mcServer.getPlayerList();
            ServerChatEvent event = new ServerChatEvent((EntityPlayerMP)player, comp.getUnformattedText(), fullComp);
            if (!MinecraftForge.EVENT_BUS.post(event)) {
               players.sendMessage(fullComp, false);
               NetHandlerPlayServer handler = ((EntityPlayerMP)player).connection;
               int threshold = ((Integer)ObfuscationReflectionHelper.getPrivateValue(NetHandlerPlayServer.class, handler, "chatSpamThresholdCount")).intValue();
               threshold = threshold + 20;
               if (threshold > 200 && !players.canSendCommands(player.getGameProfile())) {
                  handler.onDisconnect(new TextComponentTranslation("disconnect.spam", new Object[0]));
               }

               ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, handler, Integer.valueOf(threshold), "chatSpamThresholdCount");
            }
         }

      }
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public String getFeatureIngameConfigName() {
      return "Link Items To Chat";
   }
}
