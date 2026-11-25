package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.ClientProxy;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.message.MessageGetMyrmexHive;
import com.github.alexthe666.iceandfire.structures.WorldGenMyrmexHive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class GuiMyrmexAddRoom extends GuiScreen {
   private static final ResourceLocation JUNGLE_TEXTURE = new ResourceLocation("iceandfire:textures/gui/myrmex_staff_jungle.png");
   private static final ResourceLocation DESERT_TEXTURE = new ResourceLocation("iceandfire:textures/gui/myrmex_staff_desert.png");
   private ItemStack staff;
   private boolean jungle;
   private BlockPos interactPos;
   private EnumFacing facing;

   public GuiMyrmexAddRoom(ItemStack staff, BlockPos interactPos, EnumFacing facing) {
      this.staff = staff;
      this.jungle = staff.getItem() == ModItems.myrmex_jungle_staff;
      this.interactPos = interactPos;
      this.facing = facing;
      this.initGui();
   }

   public void initGui() {
      super.initGui();
      this.buttonList.clear();
      int i = (this.width - 248) / 2;
      int j = (this.height - 166) / 2;
      if (ClientProxy.getReferedClientHive() != null) {
         this.buttonList.add(new GuiButton(0, i + 50, j + 35, 150, 20, I18n.format("myrmex.message.establishroom_food", new Object[0])));
         this.buttonList.add(new GuiButton(1, i + 50, j + 60, 150, 20, I18n.format("myrmex.message.establishroom_nursery", new Object[0])));
         this.buttonList.add(new GuiButton(2, i + 50, j + 85, 150, 20, I18n.format("myrmex.message.establishroom_enterance_surface", new Object[0])));
         this.buttonList.add(new GuiButton(3, i + 50, j + 110, 150, 20, I18n.format("myrmex.message.establishroom_enterance_bottom", new Object[0])));
         this.buttonList.add(new GuiButton(4, i + 50, j + 135, 150, 20, I18n.format("myrmex.message.establishroom_misc", new Object[0])));
      }

   }

   protected void actionPerformed(GuiButton button) {
      if (ClientProxy.getReferedClientHive() != null) {
         EntityPlayer player = Minecraft.getMinecraft().player;
         switch(button.id) {
         case 0:
            ClientProxy.getReferedClientHive().addRoomWithMessage(player, this.interactPos, WorldGenMyrmexHive.RoomType.FOOD);
            break;
         case 1:
            ClientProxy.getReferedClientHive().addRoomWithMessage(player, this.interactPos, WorldGenMyrmexHive.RoomType.NURSERY);
            break;
         case 2:
            ClientProxy.getReferedClientHive().addEnteranceWithMessage(player, false, this.interactPos, this.facing);
            break;
         case 3:
            ClientProxy.getReferedClientHive().addEnteranceWithMessage(player, true, this.interactPos, this.facing);
            break;
         case 4:
            ClientProxy.getReferedClientHive().addRoomWithMessage(player, this.interactPos, WorldGenMyrmexHive.RoomType.EMPTY);
         }

         this.onGuiClosed();
         Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
      }
   }

   public void drawDefaultBackground() {
      super.drawDefaultBackground();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(this.jungle ? JUNGLE_TEXTURE : DESERT_TEXTURE);
      int i = (this.width - 248) / 2;
      int j = (this.height - 166) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, 248, 166);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.initGui();
      int i = (this.width - 248) / 2 + 10;
      int j = (this.height - 166) / 2 + 8;
      super.drawScreen(mouseX, mouseY, partialTicks);
      int color = this.jungle ? 3533333 : 16760576;
      if (ClientProxy.getReferedClientHive() != null) {
         if (!ClientProxy.getReferedClientHive().colonyName.isEmpty()) {
            String title = I18n.format("myrmex.message.colony_named", new Object[]{ClientProxy.getReferedClientHive().colonyName});
            this.fontRenderer.drawString(title, (float)(i + 40 - title.length() / 2), (float)(j - 3), color, true);
         } else {
            this.fontRenderer.drawString(I18n.format("myrmex.message.colony", new Object[0]), (float)(i + 80), (float)(j - 3), color, true);
         }

         this.fontRenderer.drawString(I18n.format("myrmex.message.create_new_room", new Object[]{this.interactPos.getX(), this.interactPos.getY(), this.interactPos.getZ()}), (float)(i + 30), (float)(j + 6), color, true);
      }

   }

   public void onGuiClosed() {
      IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageGetMyrmexHive(ClientProxy.getReferedClientHive()));
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
