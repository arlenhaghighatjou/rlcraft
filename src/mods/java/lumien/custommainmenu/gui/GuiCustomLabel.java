package lumien.custommainmenu.gui;

import com.google.common.base.Strings;
import java.util.List;
import lumien.custommainmenu.configuration.elements.Label;
import lumien.custommainmenu.lib.ANCHOR;
import lumien.custommainmenu.lib.StringReplacer;
import lumien.custommainmenu.lib.texts.TextString;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GuiCustomLabel extends Gui {
   Label text;
   int posX;
   int posY;
   FontRenderer fontRendererObj;
   int width;
   int height;
   GuiCustom parent;
   boolean hovered;

   public GuiCustomLabel(GuiCustom customGUI, Label text, int posX, int posY) {
      this.text = text;
      this.posX = posX;
      this.posY = posY;
      this.parent = customGUI;
      this.fontRendererObj = Minecraft.getMinecraft().fontRenderer;
      this.hovered = false;
      this.width = this.fontRendererObj.getStringWidth(text.text.get());
      this.height = this.fontRendererObj.FONT_HEIGHT;
      if (text.name.equals("fml")) {
         String string = "";
         List<String> brandings = FMLCommonHandler.instance().getBrandings(true);

         for(int i = 0; i < brandings.size(); ++i) {
            String brd = (String)brandings.get(i);
            if (!Strings.isNullOrEmpty(brd)) {
               string = string + brd + (i < brandings.size() - 1 ? "\n" : "");
            }
         }

         this.text.text = this.text.hoverText = new TextString(string);
      }

   }

   public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
      if (this.text.fontSize != 1.0F) {
         GlStateManager.translate((float)this.posX, (float)this.posY, 0.0F);
         GlStateManager.scale(this.text.fontSize, this.text.fontSize, 1.0F);
         GlStateManager.translate((float)(-this.posX), (float)(-this.posY), 0.0F);
      }

      String toDraw = this.hovered ? this.text.hoverText.get() : this.text.text.get();
      boolean newHovered = this.isMouseAboveLabel(mouseX, mouseY);
      if (newHovered && !this.hovered && this.text.hoverSound != null) {
         Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(this.text.hoverSound), SoundCategory.MASTER, 1.0F, 1.0F, false, 0, AttenuationType.NONE, 0.0F, 0.0F, 0.0F));
      }

      this.hovered = newHovered;
      if (toDraw.contains("\n")) {
         int modY = 0;
         String[] lines = toDraw.split("\n");

         for(String line : lines) {
            String lineDraw = StringReplacer.replacePlaceholders(line);
            int textWidth = this.fontRendererObj.getStringWidth(lineDraw);
            int offsetX = this.text.anchor == ANCHOR.START ? 0 : (this.text.anchor == ANCHOR.MIDDLE ? -(textWidth / 2) : -textWidth);
            if (this.hovered) {
               this.drawString(this.fontRendererObj, lineDraw, this.posX + offsetX, this.posY + modY, this.text.hoverColor);
            } else {
               this.drawString(this.fontRendererObj, lineDraw, this.posX + offsetX, this.posY + modY, this.text.color);
            }

            modY += this.fontRendererObj.FONT_HEIGHT;
         }
      } else {
         toDraw = StringReplacer.replacePlaceholders(toDraw);
         int textWidth = this.fontRendererObj.getStringWidth(toDraw);
         int offsetX = this.text.anchor == ANCHOR.START ? 0 : (this.text.anchor == ANCHOR.MIDDLE ? -(textWidth / 2) : -textWidth);
         if (this.hovered) {
            this.drawString(this.fontRendererObj, toDraw, this.posX + offsetX, this.posY, this.text.hoverColor);
         } else {
            this.drawString(this.fontRendererObj, toDraw, this.posX + offsetX, this.posY, this.text.color);
         }
      }

      if (this.text.fontSize != 1.0F) {
         GlStateManager.translate((float)this.posX, (float)this.posY, 0.0F);
         GlStateManager.scale(1.0F / this.text.fontSize, 1.0F / this.text.fontSize, 1.0F);
         GlStateManager.translate((float)(-this.posX), (float)(-this.posY), 0.0F);
      }

   }

   private boolean isMouseAboveLabel(int mouseX, int mouseY) {
      String stringText = this.text.text.get();
      if (stringText.contains("\n")) {
         String[] lines = stringText.split("\n");

         for(int i = 0; i < lines.length; ++i) {
            int width = this.fontRendererObj.getStringWidth(lines[i]);
            int height = this.fontRendererObj.FONT_HEIGHT;
            int modX = 0;
            switch(this.text.anchor) {
            case END:
               modX = -width;
               break;
            case MIDDLE:
               modX = -width / 2;
            case START:
            }

            if (mouseX >= this.posX + modX && mouseY >= this.posY + this.fontRendererObj.FONT_HEIGHT * i && mouseX < this.posX + width + modX && mouseY < this.posY + this.fontRendererObj.FONT_HEIGHT * i + height) {
               return true;
            }
         }

         return false;
      } else {
         int width = this.fontRendererObj.getStringWidth(stringText);
         int height = this.fontRendererObj.FONT_HEIGHT;
         int modX = 0;
         switch(this.text.anchor) {
         case END:
            modX = -width;
            break;
         case MIDDLE:
            modX = -width / 2;
         case START:
         }

         return mouseX >= this.posX + modX && mouseY >= this.posY && mouseX < this.posX + width + modX && mouseY < this.posY + height;
      }
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      boolean flag = this.isMouseAboveLabel(mouseX, mouseY);
      if (flag && this.text.action != null) {
         if (this.text.pressSound != null) {
            Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(this.text.pressSound), SoundCategory.MASTER, 1.0F, 1.0F, false, 0, AttenuationType.NONE, 0.0F, 0.0F, 0.0F));
         } else {
            Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("ui.button.click"), SoundCategory.MASTER, 1.0F, 1.0F, false, 0, AttenuationType.NONE, 0.0F, 0.0F, 0.0F));
         }

         this.text.action.perform(this.text, this.parent);
      }

   }

}
