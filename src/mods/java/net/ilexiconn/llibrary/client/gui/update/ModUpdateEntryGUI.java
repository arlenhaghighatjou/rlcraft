package net.ilexiconn.llibrary.client.gui.update;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModUpdateEntryGUI extends GuiScrollingList {
   private ModUpdateGUI parent;
   private List<ITextComponent> textList = null;

   public ModUpdateEntryGUI(ModUpdateGUI parent, int width, List<String> textList) {
      super(parent.mc, width, parent.height, 32, parent.height - 55, parent.getModList().getWidth() + 20, 60, parent.width, parent.height);
      this.parent = parent;
      this.textList = this.resizeContent(textList);
      this.setHeaderInfo(true, this.getHeaderHeight());
   }

   protected int getSize() {
      return 0;
   }

   protected void elementClicked(int index, boolean doubleClick) {
   }

   protected boolean isSelected(int index) {
      return false;
   }

   protected void drawBackground() {
   }

   protected void drawSlot(int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tessellator) {
   }

   private List<ITextComponent> resizeContent(List<String> textList) {
      List<ITextComponent> list = new ArrayList();

      for(String text : textList) {
         if (text == null) {
            list.add((Object)null);
         } else {
            ITextComponent chat = ForgeHooks.newChatWithLinks(text, false);
            list.addAll(GuiUtilRenderComponents.splitText(chat, this.listWidth - 8, this.parent.mc.fontRenderer, false, true));
         }
      }

      return list;
   }

   private int getHeaderHeight() {
      int height = this.textList.size() * 10;
      if (height < this.bottom - this.top - 8) {
         height = this.bottom - this.top - 8;
      }

      return height;
   }

   protected void drawHeader(int entryRight, int relativeY, Tessellator tess) {
      int top = relativeY;

      for(ITextComponent text : this.textList) {
         if (text != null) {
            GlStateManager.enableBlend();
            this.parent.mc.fontRenderer.drawStringWithShadow(text.getFormattedText(), (float)(this.left + 4), (float)top, 16777215);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
         }

         top += 10;
      }

   }

   protected void clickHeader(int x, int y) {
      if (y > 0) {
         int lineIdx = y / 10;
         if (lineIdx < this.textList.size()) {
            ITextComponent text = (ITextComponent)this.textList.get(lineIdx);
            if (text != null) {
               int k = -4;

               for(ITextComponent part : text) {
                  if (part instanceof TextComponentString) {
                     k += this.parent.mc.fontRenderer.getStringWidth(((TextComponentString)part).getText());
                     if (k >= x) {
                        this.parent.handleComponentClick(part);
                        break;
                     }
                  }
               }
            }

         }
      }
   }
}
