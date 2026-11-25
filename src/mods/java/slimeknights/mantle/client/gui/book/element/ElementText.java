package slimeknights.mantle.client.gui.book.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.action.StringActionProcessor;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.TextDataRenderer;

@SideOnly(Side.CLIENT)
public class ElementText extends SizedBookElement {
   public TextData[] text;
   private List<String> tooltip;
   private boolean doAction;

   public ElementText(int x, int y, int width, int height, String text) {
      this(x, y, width, height, new TextData(text));
   }

   public ElementText(int x, int y, int width, int height, Collection<TextData> text) {
      this(x, y, width, height, (TextData[])text.toArray(new TextData[text.size()]));
   }

   public ElementText(int x, int y, int width, int height, TextData... text) {
      super(x, y, width, height);
      this.tooltip = new ArrayList();
      this.doAction = false;
      this.text = text;
   }

   public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
      String action = TextDataRenderer.drawText(this.x, this.y, this.width, this.height, this.text, mouseX, mouseY, fontRenderer, this.tooltip);
      if (this.doAction) {
         this.doAction = false;
         StringActionProcessor.process(action, this.parent);
      }

   }

   public void drawOverlay(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
      if (this.tooltip.size() > 0) {
         TextDataRenderer.drawTooltip(this.tooltip, mouseX, mouseY, fontRenderer);
         this.tooltip.clear();
      }

   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         this.doAction = true;
      }

   }
}
