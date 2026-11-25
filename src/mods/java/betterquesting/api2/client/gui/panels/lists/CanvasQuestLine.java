package betterquesting.api2.client.gui.panels.lists;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.enums.EnumQuestState;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.client.gui.controls.PanelButtonQuest;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.content.PanelGeneric;
import betterquesting.api2.client.gui.panels.content.PanelLine;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.lines.IGuiLine;
import betterquesting.api2.client.gui.resources.textures.SimpleTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetLine;
import betterquesting.api2.storage.DBEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class CanvasQuestLine extends CanvasScrolling {
   private final List<PanelButtonQuest> btnList = new ArrayList();
   private final int buttonId;
   private IQuestLine lastQL;

   public CanvasQuestLine(IGuiRect rect, int buttonId) {
      super(rect);
      this.setupAdvanceScroll(true, true, 24);
      this.enableBlocking(false);
      this.buttonId = buttonId;
   }

   public Collection<PanelButtonQuest> getQuestButtons() {
      return Collections.unmodifiableCollection(this.btnList);
   }

   public PanelButtonQuest getButtonAt(int mx, int my) {
      float zs = ((Float)this.zoomScale.readValue()).floatValue();
      int tx = this.getTransform().getX();
      int ty = this.getTransform().getY();
      int smx = (int)((float)(mx - tx) / zs) + this.lsx;
      int smy = (int)((float)(my - ty) / zs) + this.lsy;

      for(PanelButtonQuest btn : this.btnList) {
         if (btn.rect.contains(smx, smy)) {
            return btn;
         }
      }

      return null;
   }

   public IQuestLine getQuestLine() {
      return this.lastQL;
   }

   public void refreshQuestLine() {
      this.setQuestLine(this.lastQL);
   }

   public void setQuestLine(IQuestLine line) {
      this.resetCanvas();
      this.btnList.clear();
      this.lastQL = line;
      if (line != null) {
         EntityPlayer player = Minecraft.getMinecraft().player;
         UUID pid = QuestingAPI.getQuestingUUID(player);
         String bgString = (String)line.getProperty(NativeProps.BG_IMAGE);
         if (!StringUtils.isNullOrEmpty(bgString)) {
            int bgSize = ((Integer)line.getProperty(NativeProps.BG_SIZE)).intValue();
            this.addPanel(new PanelGeneric(new GuiRectangle(0, 0, bgSize, bgSize, 1), new SimpleTexture(new ResourceLocation(bgString), new GuiRectangle(0, 0, 256, 256))));
         }

         HashMap<Integer, PanelButtonQuest> questBtns = new HashMap();

         for(DBEntry<IQuestLineEntry> qle : line.getEntries()) {
            IQuest quest = (IQuest)((IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB)).getValue(qle.getID());
            if (QuestCache.isQuestShown(quest, pid, player)) {
               GuiRectangle rect = new GuiRectangle(((IQuestLineEntry)qle.getValue()).getPosX(), ((IQuestLineEntry)qle.getValue()).getPosY(), ((IQuestLineEntry)qle.getValue()).getSizeX(), ((IQuestLineEntry)qle.getValue()).getSizeY());
               PanelButtonQuest paBtn = new PanelButtonQuest(rect, this.buttonId, "", new DBEntry(qle.getID(), quest));
               this.addPanel(paBtn);
               this.btnList.add(paBtn);
               questBtns.put(Integer.valueOf(qle.getID()), paBtn);
            }
         }

         for(Entry<Integer, PanelButtonQuest> entry : questBtns.entrySet()) {
            DBEntry<IQuest> quest = (DBEntry)((PanelButtonQuest)entry.getValue()).getStoredValue();
            List<DBEntry<IQuest>> reqList = ((IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB)).bulkLookup(((IQuest)quest.getValue()).getRequirements());
            if (reqList.size() > 0) {
               boolean main = ((Boolean)((IQuest)quest.getValue()).getProperty(NativeProps.MAIN)).booleanValue();
               EnumQuestState qState = ((IQuest)quest.getValue()).getState(pid);
               IGuiLine lineRender = null;
               IGuiColor txLineCol = null;
               switch(qState) {
               case LOCKED:
                  lineRender = PresetLine.QUEST_LOCKED.getLine();
                  txLineCol = PresetColor.QUEST_LINE_LOCKED.getColor();
                  break;
               case UNLOCKED:
                  lineRender = PresetLine.QUEST_UNLOCKED.getLine();
                  txLineCol = PresetColor.QUEST_LINE_UNLOCKED.getColor();
                  break;
               case UNCLAIMED:
                  lineRender = PresetLine.QUEST_PENDING.getLine();
                  txLineCol = PresetColor.QUEST_LINE_PENDING.getColor();
                  break;
               case COMPLETED:
                  lineRender = PresetLine.QUEST_COMPLETE.getLine();
                  txLineCol = PresetColor.QUEST_LINE_COMPLETE.getColor();
               }

               for(DBEntry<IQuest> req : reqList) {
                  PanelButtonQuest parBtn = (PanelButtonQuest)questBtns.get(Integer.valueOf(req.getID()));
                  if (parBtn != null) {
                     PanelLine prLine = new PanelLine(parBtn.getTransform(), ((PanelButtonQuest)entry.getValue()).getTransform(), lineRender, main ? 8 : 4, txLineCol, 1);
                     this.addPanel(prLine);
                  }
               }
            }
         }

         this.fitToWindow();
      }
   }

   public void fitToWindow() {
      boolean flag = false;
      int minX = 0;
      int minY = 0;
      int maxX = 0;
      int maxY = 0;

      for(PanelButtonQuest btn : this.btnList) {
         GuiRectangle rect = btn.rect;
         if (!flag) {
            minX = rect.getX();
            minY = rect.getY();
            maxX = minX + rect.getWidth();
            maxY = minY + rect.getHeight();
            flag = true;
         } else {
            minX = Math.min(minX, rect.getX());
            minY = Math.min(minY, rect.getY());
            maxX = Math.max(maxX, rect.getX() + rect.getWidth());
            maxY = Math.max(maxY, rect.getY() + rect.getHeight());
         }
      }

      minX = minX - this.margin;
      minY = minY - this.margin;
      maxX = maxX + this.margin;
      maxY = maxY + this.margin;
      this.setZoom(Math.min((float)this.getTransform().getWidth() / (float)(maxX - minX), (float)this.getTransform().getHeight() / (float)(maxY - minY)));
      this.refreshScrollBounds();
      IGuiRect bounds = this.getScrollBounds();
      this.setScrollX(bounds.getX() + bounds.getWidth() / 2);
      this.setScrollY(bounds.getY() + bounds.getHeight() / 2);
      this.updatePanelScroll();
   }
}
