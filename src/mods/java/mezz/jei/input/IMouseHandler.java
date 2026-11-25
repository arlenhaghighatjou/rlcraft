package mezz.jei.input;

public interface IMouseHandler {
   boolean isMouseOver(int var1, int var2);

   boolean handleMouseClicked(int var1, int var2, int var3);

   boolean handleMouseScrolled(int var1, int var2, int var3);
}
