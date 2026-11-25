package reborncore.modcl.manual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraftforge.fml.client.config.GuiUtils;
import reborncore.modcl.manual.pages.ManualPage;
import reborncore.modcl.manual.pages.PageDescription;
import reborncore.modcl.manual.pages.PageHome;
import reborncore.modcl.manual.widgets.GuiInvisibutton;
import reborncore.modcl.manual.widgets.GuiSmallButton;

public class GuiTeamRebornManual extends GuiScreen {
   public static int xSize = 0;
   public static int ySize = 0;
   public int guiLeft;
   public int guiTop;
   ManualBuilder builder = new ManualBuilder();
   ManualPage homePage = new PageHome();
   ManualPage currentPage;
   ManualPage previousPage;
   ManualPage nextPage;
   GuiSmallButton backButton;
   GuiSmallButton nextButton;
   GuiInvisibutton homeButton;

   public GuiTeamRebornManual() {
   }

   public void initGui() {
      super.initGui();
      xSize = 350;
      ySize = 201;
      this.guiLeft = this.width / 2 - xSize / 2;
      this.guiTop = this.height / 2 - ySize / 2;
      PageDescription desc = new PageDescription();
      desc.setTitle("Tech Reborn > Getting Started");
      desc.setDescription("First thing's first, to get started you must find a Rubber Tree in the world. They're more commonly found in swamps, but you can find them in most biomes that have trees. Craft a treetap and use it on a yellowish sap spot on a rubber tree to harvest it's sap. You can smelt the sap into rubber, which is used later on in things.");
      this.currentPage = desc;
      this.previousPage = this.homePage;
   }

   public void drawBackground(int tint) {
      super.drawBackground(tint);
      this.drawDefaultBackground();
      this.builder.drawDefaultBackground(this, this.guiLeft, this.guiTop, xSize, ySize);
      this.drawGradientRect(this.guiLeft + 6, 6 + this.guiTop, xSize - 6 + this.guiLeft, this.guiTop + 18, -6184543, -6184543);
      this.backButton = new GuiSmallButton(0, this.guiLeft + 6, this.guiTop + 6, 0, 12, "< Back");
      this.nextButton = new GuiSmallButton(1, 0, this.guiTop + 6, 0, 12, "Next >");
      this.homeButton = new GuiInvisibutton(2, this.guiLeft + (xSize / 2 - this.mc.fontRenderer.getStringWidth(this.currentPage.title()) / 2) - 1, this.guiTop + 7, this.mc.fontRenderer.getStringWidth(this.currentPage.title()) + 2, 10);
      this.backButton.setWidth(this.mc.fontRenderer.getStringWidth(this.backButton.displayString) + 8);
      this.nextButton.width = this.mc.fontRenderer.getStringWidth(this.nextButton.displayString) + 8;
      this.nextButton.x = this.guiLeft + xSize - 5 - this.nextButton.width;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.previousPage.equals(this.currentPage)) {
         this.backButton.enabled = false;
      }

      if (this.currentPage.nextPage() == null) {
         this.nextButton.enabled = false;
      }

      if (this.homeButton.isMouseOver()) {
         List<String> list = new ArrayList();
         list.add("Return to Home Screen");
         GuiUtils.drawHoveringText(list, mouseX, mouseY, this.width, this.height, -1, this.mc.fontRenderer);
         GlStateManager.disableLighting();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }

      this.backButton.drawButton(this.mc, mouseX, mouseY);
      this.drawCentredStringShadow(this.currentPage.title(), 8, -1);
      this.currentPage.draw(this.mc, this);
   }

   public void drawCentredString(String string, int y, int colour) {
      this.drawString(string, xSize / 2 - this.mc.fontRenderer.getStringWidth(string) / 2, y, colour);
   }

   public void drawCentredStringShadow(String string, int y, int colour) {
      this.drawStringShadow(string, xSize / 2 - this.mc.fontRenderer.getStringWidth(string) / 2, y, colour);
   }

   public void drawString(String string, int x, int y, int colour) {
      this.mc.fontRenderer.drawString(string, x + this.guiLeft, y + this.guiTop, colour);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void drawStringShadow(String string, int x, int y, int colour) {
      this.mc.fontRenderer.drawStringWithShadow(string, (float)(x + this.guiLeft), (float)(y + this.guiTop), colour);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      if (button.id == 0) {
         this.currentPage = this.previousPage;
      }

      if (button.id == 2) {
         this.currentPage = this.homePage;
      }

   }
}
