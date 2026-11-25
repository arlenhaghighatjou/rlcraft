package com.github.alexthe666.iceandfire.client.gui;

import com.github.alexthe666.iceandfire.ClientProxy;
import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.gui.bestiary.ChangePageButton;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.message.MessageGetMyrmexHive;
import com.github.alexthe666.iceandfire.structures.WorldGenMyrmexHive;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class GuiMyrmexStaff extends GuiScreen {
   private static final ResourceLocation JUNGLE_TEXTURE = new ResourceLocation("iceandfire:textures/gui/myrmex_staff_jungle.png");
   private static final ResourceLocation DESERT_TEXTURE = new ResourceLocation("iceandfire:textures/gui/myrmex_staff_desert.png");
   private ItemStack staff;
   private boolean jungle;
   private static final WorldGenMyrmexHive.RoomType[] ROOMS = new WorldGenMyrmexHive.RoomType[]{WorldGenMyrmexHive.RoomType.FOOD, WorldGenMyrmexHive.RoomType.NURSERY, WorldGenMyrmexHive.RoomType.EMPTY};
   public ChangePageButton previousPage;
   public ChangePageButton nextPage;
   int ticksSinceDeleted = 0;
   int currentPage = 0;
   private final List<GuiMyrmexStaff.Room> allRoomPos = Lists.newArrayList();
   private final List<MyrmexDeleteButton> allRoomButtonPos = Lists.newArrayList();
   private static final int ROOMS_PER_PAGE = 5;
   private int hiveCount;

   public GuiMyrmexStaff(ItemStack staff) {
      this.staff = staff;
      this.jungle = staff.getItem() == ModItems.myrmex_jungle_staff;
      this.initGui();
   }

   public void initGui() {
      super.initGui();
      this.buttonList.clear();
      this.allRoomButtonPos.clear();
      int i = (this.width - 248) / 2;
      int j = (this.height - 166) / 2;
      int x_translate = 193;
      int y_translate = 37;
      if (ClientProxy.getReferedClientHive() != null) {
         this.populateRoomMap();
         this.buttonList.add(new GuiButton(0, i + 124, j + 15, 120, 20, ClientProxy.getReferedClientHive().reproduces ? I18n.format("myrmex.message.disablebreeding", new Object[0]) : I18n.format("myrmex.message.enablebreeding", new Object[0])));
         this.buttonList.add(this.previousPage = new ChangePageButton(1, i + 5, j + 150, false, 0, this.jungle ? 2 : 1));
         this.buttonList.add(this.nextPage = new ChangePageButton(2, i + 225, j + 150, true, 0, this.jungle ? 2 : 1));
         int totalRooms = this.allRoomPos.size();

         for(int rooms = 0; rooms < this.allRoomPos.size(); ++rooms) {
            int yIndex = rooms % 5;
            MyrmexDeleteButton button = new MyrmexDeleteButton(2 + rooms, i + x_translate, j + y_translate + yIndex * 22, ((GuiMyrmexStaff.Room)this.allRoomPos.get(rooms)).pos, I18n.format("myrmex.message.delete", new Object[0]));
            if (rooms < 5 * (this.currentPage + 1) && rooms >= 5 * this.currentPage) {
               button.visible = true;
            } else {
               button.visible = false;
            }

            this.buttonList.add(button);
            this.allRoomButtonPos.add(button);
         }

         if (totalRooms <= 5 * this.currentPage && this.currentPage > 0) {
            --this.currentPage;
         }

      }
   }

   private void populateRoomMap() {
      this.allRoomPos.clear();

      for(WorldGenMyrmexHive.RoomType type : ROOMS) {
         for(BlockPos pos : ClientProxy.getReferedClientHive().getRooms(type)) {
            String name = type == WorldGenMyrmexHive.RoomType.FOOD ? "food" : (type == WorldGenMyrmexHive.RoomType.NURSERY ? "nursery" : "misc");
            this.allRoomPos.add(new GuiMyrmexStaff.Room(pos, name));
         }
      }

      for(BlockPos pos : ClientProxy.getReferedClientHive().getEntrances().keySet()) {
         this.allRoomPos.add(new GuiMyrmexStaff.Room(pos, "enterance_surface"));
      }

      for(BlockPos pos : ClientProxy.getReferedClientHive().getEntranceBottoms().keySet()) {
         this.allRoomPos.add(new GuiMyrmexStaff.Room(pos, "enterance_bottom"));
      }

   }

   protected void actionPerformed(GuiButton button) {
      if (button.id == 0) {
         ClientProxy.getReferedClientHive().reproduces = !ClientProxy.getReferedClientHive().reproduces;
      } else if (button.id == 1 && this.currentPage > 0) {
         --this.currentPage;
      }

      if (button.id == 2 && this.currentPage < this.allRoomButtonPos.size() / 5) {
         ++this.currentPage;
      } else if (button instanceof MyrmexDeleteButton && this.ticksSinceDeleted <= 0) {
         ClientProxy.getReferedClientHive().removeRoom(((MyrmexDeleteButton)button).pos);
         this.ticksSinceDeleted = 5;
      }

      this.initGui();
   }

   public boolean doesGuiPauseGame() {
      return false;
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
      if (this.ticksSinceDeleted > 0) {
         --this.ticksSinceDeleted;
      }

      this.initGui();
      int i = (this.width - 248) / 2 + 10;
      int j = (this.height - 166) / 2 + 8;
      super.drawScreen(mouseX, mouseY, partialTicks);
      int color = this.jungle ? 3533333 : 16760576;
      this.hiveCount = 0;

      for(int rooms = 0; rooms < this.allRoomButtonPos.size(); ++rooms) {
         if (rooms < 5 * (this.currentPage + 1) && rooms >= 5 * this.currentPage) {
            this.drawRoomInfo(((GuiMyrmexStaff.Room)this.allRoomPos.get(rooms)).string, ((GuiMyrmexStaff.Room)this.allRoomPos.get(rooms)).pos, i, j, color);
         }
      }

      if (ClientProxy.getReferedClientHive() != null) {
         if (!ClientProxy.getReferedClientHive().colonyName.isEmpty()) {
            String title = I18n.format("myrmex.message.colony_named", new Object[]{ClientProxy.getReferedClientHive().colonyName});
            this.fontRenderer.drawString(title, (float)(i + 40 - title.length() / 2), (float)(j - 3), color, true);
         } else {
            this.fontRenderer.drawString(I18n.format("myrmex.message.colony", new Object[0]), (float)(i + 80), (float)(j - 3), color, true);
         }

         int opinion = ClientProxy.getReferedClientHive().getPlayerReputation(Minecraft.getMinecraft().player.getUniqueID());
         this.fontRenderer.drawString(I18n.format("myrmex.message.hive_opinion", new Object[]{opinion}), (float)i, (float)(j + 12), color, true);
         this.fontRenderer.drawString(I18n.format("myrmex.message.rooms", new Object[0]), (float)i, (float)(j + 25), color, true);
      }

   }

   public void onGuiClosed() {
      IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageGetMyrmexHive(ClientProxy.getReferedClientHive()));
   }

   private void drawRoomInfo(String type, BlockPos pos, int i, int j, int color) {
      String translate = "myrmex.message.room." + type;
      this.fontRenderer.drawString(I18n.format(translate, new Object[]{pos.getX(), pos.getY(), pos.getZ()}), (float)i, (float)(j + 36 + this.hiveCount * 22), color, true);
      ++this.hiveCount;
   }

   private class Room {
      public BlockPos pos;
      public String string;

      public Room(BlockPos pos, String string) {
         this.pos = pos;
         this.string = string;
      }
   }
}
