package mezz.jei.gui.overlay.bookmarks;

import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.Set;
import mezz.jei.input.IShowsRecipeFocuses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;

public interface ILeftAreaContent extends IShowsRecipeFocuses {
   void drawScreen(Minecraft var1, int var2, int var3, float var4);

   void drawOnForeground(GuiContainer var1, int var2, int var3);

   void drawTooltips(Minecraft var1, int var2, int var3);

   void updateBounds(Rectangle var1, Set<Rectangle> var2);

   boolean handleMouseScrolled(int var1, int var2, int var3);

   boolean handleMouseClicked(int var1, int var2, int var3);
}
