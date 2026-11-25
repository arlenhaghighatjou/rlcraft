package reborncore.common.logic;

import java.io.IOException;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import reborncore.common.network.NetworkManager;

public class LogicGui extends GuiContainer {
   @Nonnull
   EntityPlayer player;
   @Nonnull
   LogicController logicController;

   public LogicGui(EntityPlayer player, LogicController logicController) {
      super(new LogicContainer(player, logicController));
      this.player = player;
      this.logicController = logicController;
      this.buttonList.clear();
   }

   public void initGui() {
      super.initGui();
      if (this.logicController.getButtons() != null) {
         for(GuiButton b : this.logicController.getButtons()) {
            this.buttonList.add(b);
         }
      }

   }

   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      this.logicController.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, this);
   }

   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.logicController.drawGuiContainerForegroundLayer(mouseX, mouseY, this, this.guiLeft, this.guiTop);
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      NetworkManager.sendToServer(new PacketButtonID(this.logicController.getPos(), button.id));
   }
}
