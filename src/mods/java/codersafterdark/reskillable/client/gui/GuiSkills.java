package codersafterdark.reskillable.client.gui;

import codersafterdark.reskillable.api.ReskillableRegistries;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.PlayerSkillInfo;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.client.base.RenderHelper;
import codersafterdark.reskillable.client.gui.handler.InventoryTabHandler;
import codersafterdark.reskillable.client.gui.handler.KeyBindings;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiSkills extends GuiScreen {
   public static final ResourceLocation SKILLS_RES = new ResourceLocation("reskillable", "textures/gui/skills.png");
   private int guiWidth;
   private int guiHeight;
   private Skill hoveredSkill;
   private int offset;
   private int left;
   private int top;
   private int lastY;
   private List<Skill> enabledSkills = new ArrayList();
   private List<Skill> skills = new ArrayList();

   public GuiSkills() {
      Stream var10000 = ReskillableRegistries.SKILLS.getValuesCollection().stream().filter(Skill::isEnabled);
      List var10001 = this.enabledSkills;
      this.enabledSkills.getClass();
      var10000.forEach(var10001::add);
   }

   public static void drawSkill(int x, int y, Skill skill) {
      Minecraft mc = Minecraft.getMinecraft();
      int rank = PlayerDataHandler.get(mc.player).getSkillInfo(skill).getRank();
      if (skill.hasCustomSprites()) {
         ResourceLocation sprite = skill.getSpriteLocation(rank);
         if (sprite != null) {
            mc.renderEngine.bindTexture(sprite);
            drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
         }
      } else {
         mc.renderEngine.bindTexture(skill.getSpriteLocation());
         Pair<Integer, Integer> pair = skill.getSpriteFromRank(rank);
         RenderHelper.drawTexturedModalRect(x, y, 1.0F, ((Integer)pair.getKey()).intValue(), ((Integer)pair.getValue()).intValue(), 16, 16, 0.015625F, 0.015625F);
      }

   }

   public static void drawScrollButtonsTop(int x, int y) {
      Minecraft.getMinecraft().renderEngine.bindTexture(SKILLS_RES);
      RenderHelper.drawTexturedModalRect(x, y, 1.0F, 0, 230, 80, 4);
   }

   public static void drawScrollButtonsBottom(int x, int y) {
      Minecraft.getMinecraft().renderEngine.bindTexture(SKILLS_RES);
      RenderHelper.drawTexturedModalRect(x, y, 1.0F, 0, 235, 80, 4);
   }

   protected void keyTyped(char typedChar, int keyCode) {
      if (keyCode == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
         if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
         }
      } else if (keyCode == KeyBindings.openGUI.getKeyCode() || keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()) {
         this.mc.displayGuiScreen((GuiScreen)null);
         if (this.mc.currentScreen != null) {
            this.mc.setIngameFocus();
         }
      }

   }

   public void initGui() {
      this.guiWidth = 176;
      this.guiHeight = 166;
      this.buttonList.clear();
      InventoryTabHandler.addTabs(this, this.buttonList);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.mc.renderEngine.bindTexture(SKILLS_RES);
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      this.left = this.width / 2 - this.guiWidth / 2;
      this.top = this.height / 2 - this.guiHeight / 2;
      this.drawTexturedModalRect(this.left, this.top, 0, 0, this.guiWidth, this.guiHeight);
      PlayerData data = PlayerDataHandler.get(this.mc.player);
      this.hoveredSkill = null;
      this.skills = new ArrayList();
      Stream var10000 = this.enabledSkills.stream().filter((enabledSkill) -> {
         return !enabledSkill.isHidden();
      });
      List var10001 = this.skills;
      this.skills.getClass();
      var10000.forEach(var10001::add);
      int index = 0;

      for(int j = this.offset; j < this.skills.size() && index < 8; ++j) {
         Skill skill = (Skill)this.skills.get(j);
         PlayerSkillInfo skillInfo = data.getSkillInfo(skill);
         int i = index++;
         int w = 79;
         int h = 32;
         int x = this.left + i % 2 * (w + 3) + 8;
         int y = this.top + i / 2 * (h + 3) + 18;
         this.lastY = y;
         int u = 0;
         int v = this.guiHeight;
         if (mouseX >= x && mouseY >= y && mouseX < x + w && mouseY < y + h) {
            u += w;
            this.hoveredSkill = skill;
         }

         if (skillInfo.isCapped()) {
            v += h;
         }

         this.mc.renderEngine.bindTexture(SKILLS_RES);
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(x, y, u, v, w, h);
         drawSkill(x + 5, y + 9, skill);
         this.mc.fontRenderer.drawString(skill.getName(), x + 26, y + 6, 16777215);
         this.mc.fontRenderer.drawString(skillInfo.getLevel() + "/" + skill.getCap(), x + 26, y + 17, 8947848);
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      drawScrollButtonsTop(this.left + 49, this.top + 14);
      drawScrollButtonsBottom(this.left + 49, this.lastY + 32);
      String skillsStr = (new TextComponentTranslation("reskillable.misc.skills", new Object[0])).getUnformattedComponentText();
      this.fontRenderer.drawString(skillsStr, this.width / 2 - this.fontRenderer.getStringWidth(skillsStr) / 2, this.top + 5, 4210752);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (mouseButton == 0 && this.hoveredSkill != null) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         GuiSkillInfo gui = new GuiSkillInfo(this.hoveredSkill);
         this.mc.displayGuiScreen(gui);
      }

      if (mouseButton == 0 && mouseX >= this.left + 49 && mouseX <= this.left + 128 && mouseY >= this.top + 14) {
         if (mouseY <= this.top + 18) {
            this.scrollUp();
         } else if (mouseY <= this.lastY + 36) {
            this.scrollDown();
         }
      }

   }

   private void scrollUp() {
      this.offset = Math.max(this.offset - 2, 0);
   }

   private void scrollDown() {
      int off = 2;
      if (this.skills.size() % 2 == 1) {
         off = 1;
      }

      this.offset = Math.min(this.offset + 2, Math.max(this.skills.size() - 6 - off, 0));
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      if (Mouse.getEventDWheel() > 0) {
         this.scrollUp();
      } else if (Mouse.getEventDWheel() < 0) {
         this.scrollDown();
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
