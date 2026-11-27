package mezz.jei.api.gui;

import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiScreen;

public interface IGhostIngredientHandler<T extends GuiScreen> {
   <I> List<IGhostIngredientHandler.Target<I>> getTargets(T var1, I var2, boolean var3);

   void onComplete();

   default boolean shouldHighlightTargets() {
      return true;
   }

   public interface Target<I> extends Consumer<I> {
      Rectangle getArea();

      void accept(I var1);
   }
}
