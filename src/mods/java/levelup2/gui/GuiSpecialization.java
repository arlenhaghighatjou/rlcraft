package levelup2.gui;

import levelup2.network.SkillPacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;

public class GuiSpecialization extends GuiScreen {
   private boolean closedWithButton = false;
   private boolean isRespec = false;
   private byte spec = -1;

   public GuiSpecialization() {
   }

   public static GuiSpecialization withRespec() {
      GuiSpecialization spec = new GuiSpecialization();
      spec.isRespec = true;
      return spec;
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void initGui() {
      this.closedWithButton = false;
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 + 96, this.height / 6 + 168, 96, 20, I18n.format("gui.done", new Object[0])));
      this.buttonList.add(new GuiButton(4, this.width / 2 - 192, this.height / 6 + 168, 96, 20, I18n.format("gui.cancel", new Object[0])));
      String[] specs = new String[]{"mining", "craft", "combat"};

      for(int i = 1; i < 4; ++i) {
         this.buttonList.add(new GuiImage(i, this.width / 2 - 170 + (i - 1) * 120, 50, 96, 120, specs[i - 1]));
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void onGuiClosed() {
      if (this.closedWithButton && this.spec != -1) {
         FMLProxyPacket pkt = SkillPacketHandler.getPacket(Side.SERVER, 1, this.spec, this.isRespec);
         SkillPacketHandler.classChannel.sendToServer(pkt);
      }

   }

   protected void actionPerformed(GuiButton button) {
      if (button.id == 0) {
         this.closedWithButton = true;
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
      } else if (button.id == 4) {
         this.closedWithButton = false;
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
      } else {
         this.spec = (byte)(button.id - 1);
         ((GuiImage)button).selected = true;

         for(GuiButton b : this.buttonList) {
            if (b.id != 0 && b.id != 4) {
               GuiImage img = (GuiImage)b;
               if (img.id != button.id && img.selected) {
                  img.selected = false;
               }
            }
         }
      }

   }
}
