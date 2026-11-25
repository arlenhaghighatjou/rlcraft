package cursedflames.bountifulbaubles.wormhole;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.client.gui.GuiBetterButton;
import cursedflames.bountifulbaubles.client.gui.GuiToggleButton;
import cursedflames.bountifulbaubles.network.NBTPacket;
import cursedflames.bountifulbaubles.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.CPacketCloseWindow;

public class GuiWormhole extends GuiScreen {
   private static final int WIDTH = 192;
   private static final int HEIGHT = 220;
   private int guiLeft;
   private int guiTop;
   private int page = 0;
   private ContainerWormhole container;

   public ContainerWormhole getContainer() {
      return this.container;
   }

   public GuiWormhole(ContainerWormhole container) {
      this.container = container;
   }

   public void initGui() {
      super.initGui();
      this.guiLeft = (this.width - 192) / 2;
      this.guiTop = (this.height - 220) / 2;
      int id = 0;
      int i = 0;

      for(int j = 2; i < 8; j += 24) {
         this.buttonList.add(new GuiBetterButton(id++, this.guiLeft, this.guiTop + j, 80, 20, ""));
         this.buttonList.add(new GuiBetterButton(id++, this.guiLeft + 96, this.guiTop + j, 80, 20, ""));
         ++i;
      }

      i = 0;

      for(int j = 3; i < 8; j += 24) {
         this.buttonList.add(new GuiToggleButton(id++, this.guiLeft + 80, this.guiTop + j));
         this.buttonList.add(new GuiToggleButton(id++, this.guiLeft + 96 + 80, this.guiTop + j));
         ++i;
      }

      String prev = BountifulBaubles.proxy.translate("bountifulbaubles.gui.misc.prev_page");
      String next = BountifulBaubles.proxy.translate("bountifulbaubles.gui.misc.next_page");
      this.buttonList.add(new GuiBetterButton(id++, this.guiLeft, this.guiTop + 8 + 192, 80, 20, prev));
      this.buttonList.add(new GuiBetterButton(id++, this.guiLeft + 192 - 80, this.guiTop + 8 + 192, 80, 20, next));
      this.pageChange(this.page);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.container.guiDirty) {
         this.pageChange(this.page);
         this.container.guiDirty = false;
      }

   }

   protected void actionPerformed(GuiButton button) {
      if (button.id < 16) {
         this.wormholeToPlayer(button.id);
      } else if (button.id < 32) {
         NBTTagCompound tag = new NBTTagCompound();
         tag.setInteger("index", this.page * 16 + button.id - 16);
         PacketHandler.INSTANCE.sendToServer(new NBTPacket(tag, PacketHandler.HandlerIds.WORMHOLE_PIN.id));
      } else {
         if (button.id == 32) {
            --this.page;
         } else {
            ++this.page;
         }

         int maxPage = this.container.targets.size() / 16;
         if (this.page < 0) {
            this.page = maxPage;
         } else if (this.page > maxPage) {
            this.page = 0;
         }

         this.pageChange(this.page);
      }

   }

   private void pageChange(int page) {
      for(int i = 0; i < 16; ++i) {
         int listIndex = page * 16 + i;
         GuiButton button = (GuiButton)this.buttonList.get(i);
         GuiButton pinButton = (GuiButton)this.buttonList.get(i + 16);
         if (listIndex < this.container.targets.size()) {
            button.visible = true;
            pinButton.visible = true;
            IWormholeTarget target = (IWormholeTarget)this.container.targets.get(listIndex);
            if (target != null) {
               button.displayString = target.getName();
            }

            button.enabled = target.isEnabled();
            if (pinButton instanceof GuiToggleButton) {
               ((GuiToggleButton)pinButton).pinned = listIndex < this.container.pinCount;
            }
         } else {
            button.visible = false;
            pinButton.visible = false;
            button.displayString = "";
         }
      }

      GuiButton prev = (GuiButton)this.buttonList.get(32);
      GuiButton next = (GuiButton)this.buttonList.get(33);
      boolean pageButtons = this.container.targets.size() > 16;
      prev.visible = pageButtons;
      next.visible = pageButtons;
   }

   private void wormholeToPlayer(int index) {
      NBTTagCompound tag = new NBTTagCompound();
      if (index + this.page * 16 < this.container.targets.size()) {
         tag.setTag("target", ((IWormholeTarget)this.container.targets.get(index + this.page * 16)).toNBT());
         PacketHandler.INSTANCE.sendToServer(new NBTPacket(tag, PacketHandler.HandlerIds.WORMHOLE.id));
         this.mc.player.closeScreen();
      }
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void onGuiClosed() {
      super.onGuiClosed();
      this.mc.player.connection.sendPacket(new CPacketCloseWindow(this.mc.player.openContainer.windowId));
      this.mc.player.openContainer = this.mc.player.inventoryContainer;
      this.mc.player.inventoryContainer.windowId = 0;
   }
}
