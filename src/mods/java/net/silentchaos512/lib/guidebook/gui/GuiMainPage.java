package net.silentchaos512.lib.guidebook.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.gui.TexturedButton;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideEntry;
import net.silentchaos512.lib.guidebook.button.EntryButton;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;

@SideOnly(Side.CLIENT)
public class GuiMainPage extends GuiGuide {
   @Nullable
   private TexturedButton achievementButton;
   @Nullable
   private TexturedButton configButton;
   private GuiButton tutorialButton;
   private boolean showTutorial;
   private String bookletName;
   private String bookletEdition;
   private List<String> quote;
   private String quoteGuy;

   public GuiMainPage(GuideBook book, GuiScreen previousScreen) {
      super(book, previousScreen, (GuiGuideBase)null);
   }

   private List<IGuideEntry> getDisplayedEntries() {
      List<IGuideEntry> displayed = new ArrayList();

      for(IGuideEntry entry : this.book.getEntries()) {
         if (entry.visibleOnFrontPage()) {
            displayed.add(entry);
         }
      }

      return displayed;
   }

   public void initGui() {
      super.initGui();
      this.bookletName = "guide." + this.book.getModId() + ".manualName.1";
      String usedQuote = this.book.selectQuote(this.mc.world.rand);
      if (!usedQuote.isEmpty()) {
         String[] quoteSplit = usedQuote.split("@");
         if (quoteSplit.length > 0) {
            this.quote = this.fontRenderer.listFormattedStringToWidth(quoteSplit[0], 120);
         }

         if (quoteSplit.length > 1) {
            this.quoteGuy = quoteSplit[1];
         } else {
            this.quoteGuy = null;
         }
      }

      String playerName = this.mc.player.getName();
      if (playerName.equalsIgnoreCase("derp")) {
         this.bookletEdition = "derp edition";
      } else {
         this.bookletEdition = this.book.getEditionString(this.mc.player);
      }

      int xPos = this.guiLeft - 4;
      List<String> configText = new ArrayList();
      configText.add(TextFormatting.GOLD + this.book.i18n.translate("guide", "configButton.name"));
      if (this.book.getConfigScreen(this) != null) {
         xPos += 20;
         this.configButton = new TexturedButton(this.book.getResourceGadgets(), -388, xPos, this.guiTop + this.ySize - 30, 188, 14, 16, 16, configText);
         this.buttonList.add(this.configButton);
      }

      List<String> achievementText = new ArrayList();
      achievementText.add(TextFormatting.GOLD + this.book.i18n.translate("guide", "achievementButton.name"));
      if (this.book.getAchievementScreen(this) != null) {
         xPos = xPos + 20;
         this.achievementButton = new TexturedButton(this.book.getResourceGadgets(), -389, xPos, this.guiTop + this.ySize - 30, 204, 14, 16, 16, achievementText);
         this.buttonList.add(this.achievementButton);
      }

      for(int i = 0; i < 12; ++i) {
         List<IGuideEntry> displayed = this.getDisplayedEntries();
         if (displayed.size() <= i) {
            return;
         }

         IGuideEntry entry = (IGuideEntry)displayed.get(i);
         this.buttonList.add(new EntryButton(this, i, this.guiLeft + 156, this.guiTop + 11 + i * 13, 115, 10, "- " + entry.getLocalizedNameWithFormatting(), ItemStack.EMPTY));
      }

   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if (button instanceof EntryButton) {
         List<IGuideEntry> displayed = this.getDisplayedEntries();
         if (displayed.size() > button.id) {
            IGuideEntry entry = (IGuideEntry)displayed.get(button.id);
            if (entry != null) {
               this.mc.displayGuiScreen(new GuiEntry(this.book, this.previousScreen, this, entry, 0, "", false));
            }
         }
      } else if (button == this.achievementButton) {
         GuiScreen achievements = this.book.getAchievementScreen(this);
         if (achievements != null) {
            this.mc.displayGuiScreen(achievements);
         }
      } else if (button == this.configButton) {
         GuiScreen config = this.book.getConfigScreen(this);
         if (config != null) {
            this.mc.displayGuiScreen(config);
         }
      } else if (this.showTutorial && button == this.tutorialButton) {
         if (this.hasBookmarkButtons()) {
            if (!isShiftKeyDown()) {
               ;
            }

            this.showTutorial = false;
            this.tutorialButton.visible = false;
            this.configButton.visible = true;
            this.achievementButton.visible = true;
         }
      } else {
         super.actionPerformed(button);
      }

   }

   public void drawScreenPre(int mouseX, int mouseY, float partialTicks) {
      super.drawScreenPre(mouseX, mouseY, partialTicks);
      String strg = TextFormatting.DARK_GREEN + this.book.i18n.translate(this.bookletName);
      this.fontRenderer.drawString(strg, this.guiLeft + 72 - this.fontRenderer.getStringWidth(strg) / 2 - 3, this.guiTop + 19, 0);
      strg = TextFormatting.DARK_GREEN + this.book.i18n.translate("guide", "manualName.2");
      this.fontRenderer.drawString(strg, this.guiLeft + 72 - this.fontRenderer.getStringWidth(strg) / 2 - 3, this.guiTop + 19 + this.fontRenderer.FONT_HEIGHT, 0);
      strg = TextFormatting.GOLD + TextFormatting.ITALIC.toString() + this.bookletEdition;
      this.fontRenderer.drawString(strg, this.guiLeft + 72 - this.fontRenderer.getStringWidth(strg) / 2 - 3, this.guiTop + 40, 0);
      if (this.showTutorial) {
         String text = TextFormatting.BLUE + "It looks like this is the first time you are using this manual. \nIf you click the button below, some useful bookmarks will be stored at the bottom of the GUI. You should definitely check them out to get started with " + this.book.getModId() + "! \nIf you don't want this, shift-click the button.";
         this.renderSplitScaledAsciiString(text, this.guiLeft + 11, this.guiTop + 55, 0, false, this.getMediumFontSize(), 120);
      } else if (this.quote != null && !this.quote.isEmpty()) {
         int quoteSize = this.quote.size();

         for(int i = 0; i < quoteSize; ++i) {
            this.renderScaledAsciiString(TextFormatting.ITALIC + (String)this.quote.get(i), this.guiLeft + 25, this.guiTop + 90 + i * 8, 0, false, this.getMediumFontSize());
         }

         if (this.quoteGuy != null) {
            this.renderScaledAsciiString("- " + this.quoteGuy, this.guiLeft + 60, this.guiTop + 93 + quoteSize * 8, 0, false, this.getLargeFontSize());
         }
      }

   }

   public void addOrModifyItemRenderer(ItemStack renderedStack, int x, int y, float scale, boolean shouldTryTransfer) {
   }
}
