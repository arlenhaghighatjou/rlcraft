package rtg.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class WorldTypeMessageGUI extends GuiScreen {
   private static final ResourceLocation LOGO_LOCATION = new ResourceLocation("rtg", "textures/gui/rtg-logo-worldtype.png");
   private static final String LANG_KEY = "gui.createWorld.worldtype";
   private GuiScreen parentScreen;

   public WorldTypeMessageGUI(GuiScreen parentScreen) {
      this.parentScreen = parentScreen;
   }

   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 25, this.height - 24, 50, 20, I18n.format("gui.back", new Object[0])));
   }

   protected void actionPerformed(GuiButton button) {
      if (button.enabled && button.id == 0) {
         this.mc.displayGuiScreen(this.parentScreen);
      }

   }

   public void drawScreen(int x, int y, float renderPartialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("gui.createWorld.worldtype.line1", new Object[0]), this.width / 2, 80, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.format("gui.createWorld.worldtype.line2", new Object[0]), this.width / 2, 96, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.format("gui.createWorld.worldtype.line3", new Object[]{I18n.format("gui.createWorld.worldtypename", new Object[0])}), this.width / 2, 112, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.format("gui.createWorld.worldtype.line4", new Object[0]), this.width / 2, 144, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.format("gui.createWorld.worldtype.line5", new Object[0]), this.width / 2, 160, 16777215);
      GL11.glEnable(3042);
      this.mc.getTextureManager().bindTexture(LOGO_LOCATION);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.drawTexturedModalRect(this.width / 2 - 84, 0, 0, 0, 168, 80);
      GL11.glDisable(3042);
      super.drawScreen(x, y, renderPartialTicks);
   }
}
