package betterquesting.api2.client.gui.popups;

import betterquesting.api2.client.gui.SceneController;
import betterquesting.api2.client.gui.controls.PanelButton;
import betterquesting.api2.client.gui.misc.GuiAlign;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.GuiTransform;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.CanvasEmpty;
import betterquesting.api2.client.gui.panels.CanvasResizeable;
import betterquesting.api2.client.gui.panels.bars.PanelVScrollBar;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.lists.CanvasScrolling;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import betterquesting.api2.utils.QuestTranslation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PopContextMenu extends CanvasEmpty {
   private final PopContextMenu.ContextCategory catRoot = new PopContextMenu.ContextCategory((PopContextMenu.ContextCategory)null, "root");
   private final GuiRectangle rect;
   private final boolean autoClose;

   public PopContextMenu(GuiRectangle rect, boolean autoClose) {
      super(rect);
      this.rect = rect;
      this.autoClose = autoClose;
   }

   public PopContextMenu.ContextCategory getRootCategory() {
      return this.catRoot;
   }

   public void addButton(@Nonnull String text, @Nullable IGuiTexture icon, @Nullable Runnable action) {
      this.catRoot.addButton(text, icon, action);
   }

   public PopContextMenu.ContextCategory addCateogry(@Nonnull String text) {
      return this.catRoot.addCateogry(text);
   }

   public void openCategory(@Nonnull PopContextMenu.ContextCategory category) {
      this.resetCanvas();
      int listH = Math.min(category.entries.size() * 16, this.rect.getHeight());
      if (this.getTransform().getParent() != null) {
         IGuiRect par = this.getTransform().getParent();
         this.rect.x += Math.min(0, par.getX() + par.getWidth() - (this.rect.getX() + this.rect.getWidth()));
         this.rect.y += Math.min(0, par.getY() + par.getHeight() - (this.rect.getY() + listH));
      }

      CanvasResizeable cvBG = new CanvasResizeable(new GuiRectangle(0, 0, 0, 0, 0), PresetTexture.PANEL_INNER.getTexture());
      this.addPanel(cvBG);
      cvBG.lerpToRect(new GuiRectangle(0, 0, this.rect.w - 8, listH, 0), 100L, true);
      CanvasScrolling cvScroll = new CanvasScrolling(new GuiTransform(GuiAlign.FULL_BOX));
      cvBG.addPanel(cvScroll);
      PanelVScrollBar scrollBar = new PanelVScrollBar(new GuiRectangle(this.rect.w - 8, 0, 8, listH, 0));
      this.addPanel(scrollBar);
      cvScroll.setScrollDriverY(scrollBar);

      for(int i = 0; i < category.entries.size(); ++i) {
         PopContextMenu.ContextEntry entry = (PopContextMenu.ContextEntry)category.entries.get(i);
         if (entry.icon != null) {
            cvScroll.addPanel(new PanelGeneric(new GuiRectangle(0, i * 16, 16, 16, 0), entry.icon));
            PanelButton eBtn = new PanelButton(new GuiRectangle(16, i * 16, this.rect.w - 24, 16, 0), -1, QuestTranslation.translate(entry.text));
            if (entry.action != null) {
               eBtn.setClickAction((b) -> {
                  entry.action.run();
               });
            } else {
               eBtn.setActive(false);
            }

            cvScroll.addPanel(eBtn);
         } else {
            PanelButton eBtn = new PanelButton(new GuiRectangle(0, i * 16, this.rect.w - 8, 16, 0), -1, QuestTranslation.translate(entry.text));
            if (entry.action != null) {
               eBtn.setClickAction((b) -> {
                  entry.action.run();
               });
            } else {
               eBtn.setActive(false);
            }

            cvScroll.addPanel(eBtn);
         }
      }

      scrollBar.setEnabled(cvScroll.getScrollBounds().getHeight() > 0);
   }

   public void initPanel() {
      super.initPanel();
      this.openCategory(this.catRoot);
   }

   public boolean onMouseClick(int mx, int my, int click) {
      boolean used = super.onMouseClick(mx, my, click);
      if (this.autoClose && !used && !this.rect.contains(mx, my) && SceneController.getActiveScene() != null) {
         SceneController.getActiveScene().closePopup();
         return true;
      } else {
         return used;
      }
   }

   public class ContextCategory {
      private final String name;
      private final PopContextMenu.ContextCategory parent;
      private final List<PopContextMenu.ContextEntry> entries;

      private ContextCategory(@Nullable PopContextMenu.ContextCategory parent, @Nonnull String name) {
         this.entries = new ArrayList();
         this.parent = parent;
         this.name = name;
         if (this.parent != null) {
            this.addButton("<", (IGuiTexture)null, () -> {
               PopContextMenu.this.openCategory(this.parent);
            });
         }

      }

      public void addButton(@Nonnull String text, @Nullable IGuiTexture icon, @Nullable Runnable action) {
         this.entries.add(PopContextMenu.this.new ContextEntry(text, icon, action));
      }

      public PopContextMenu.ContextCategory addCateogry(@Nonnull String text) {
         PopContextMenu.ContextCategory cat = PopContextMenu.this.new ContextCategory(this, text);
         this.addButton(QuestTranslation.translate(text) + " >", (IGuiTexture)null, () -> {
            PopContextMenu.this.openCategory(cat);
         });
         return cat;
      }
   }

   public class ContextEntry {
      private final String text;
      private final IGuiTexture icon;
      private final Runnable action;

      public ContextEntry(@Nonnull String text, @Nullable IGuiTexture icon, @Nullable Runnable action) {
         this.text = text;
         this.icon = icon;
         this.action = action;
      }
   }
}
