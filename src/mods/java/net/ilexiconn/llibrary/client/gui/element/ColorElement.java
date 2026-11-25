package net.ilexiconn.llibrary.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.server.property.IIntProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorElement<T extends IElementGUI> extends Element<T> {
   public static final int[] COLORS = new int[]{-83455, -29952, -564722, -3518449, -2475263, -1087152, -3132360, -48572, -1685418, -1568477, -1441697, -3932078, -1900405, -4325001, -3982925, -6618998, -16681001, -16751695, -7172138, -9737770, -7904072, -9155160, -5224766, -7923817, -16737861, -13796198, -16664636, -16547192, -16731500, -16677259, -16724368, -15693762, -8752012, -10593705, -9996663, -11510678, -11043724, -11966368, -11894267, -15696881, -9013642, -11843000, -9799298, -11775141, -10191772, -11313836, -8161980, -8490145};
   private final IIntProperty selectedColor;
   private final int[] colors;
   private int horizontalRows;

   public ColorElement(T gui, float posX, float posY, int width, int height, int color) {
      this(gui, posX, posY, width, height, new IIntProperty.WithState(color));
   }

   public ColorElement(T gui, float posX, float posY, int width, int height, IIntProperty selectedColor) {
      super(gui, posX, posY, width, height);
      this.selectedColor = selectedColor;
      this.colors = COLORS;
      this.horizontalRows = (int)Math.ceil((double)(width / 23));
   }

   public void render(float mouseX, float mouseY, float partialTicks) {
      this.drawRectangle((double)(this.getPosX() + 1.0F), (double)(this.getPosY() + 1.0F), (double)(this.getWidth() - 1), (double)(this.getHeight() - 1), LLibrary.CONFIG.getSecondaryColor());
      float offsetX = (float)(this.getWidth() / 2 - (this.horizontalRows * 23 + 4) / 2);
      float offsetY = (float)(this.getHeight() / 2 - (this.colors.length / this.horizontalRows * 23 + 4) / 2);

      for(int i = 0; i < this.colors.length; ++i) {
         int color = this.colors[i];
         int x = i % this.horizontalRows * 23 + 4;
         int y = i / this.horizontalRows * 23 + 4;
         this.drawRectangle((double)(this.getPosX() + (float)x + offsetX), (double)(this.getPosY() + (float)y + offsetY), 20.0D, 20.0D, color);
         if (color == LLibrary.CONFIG.getAccentColor()) {
            this.drawOutline((double)(this.getPosX() + (float)x + offsetX), (double)(this.getPosY() + (float)y + offsetY), 20.0D, 20.0D, LLibrary.CONFIG.getTextColor(), 2.0D);
         }
      }

   }

   public boolean mouseClicked(float mouseX, float mouseY, int button) {
      float offsetX = (float)(this.getWidth() / 2 - (this.horizontalRows * 23 + 4) / 2);
      float offsetY = (float)(this.getHeight() / 2 - (this.colors.length / this.horizontalRows * 23 + 4) / 2);

      for(int i = 0; i < this.colors.length; ++i) {
         float x = this.getPosX() + (float)(i % this.horizontalRows * 23) + 4.0F + offsetX;
         float y = this.getPosY() + (float)(i / this.horizontalRows * 23) + 4.0F + offsetY;
         if (button == 0 && mouseX >= x && mouseX <= x + 20.0F && mouseY >= y && mouseY <= y + 20.0F) {
            if (this.selectedColor.trySetInt(this.colors[i])) {
               this.gui.playClickSound();
            }

            return true;
         }
      }

      return false;
   }

   public int getColor() {
      return this.selectedColor.getInt();
   }
}
