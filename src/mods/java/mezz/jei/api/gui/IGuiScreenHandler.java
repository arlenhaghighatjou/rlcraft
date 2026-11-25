package mezz.jei.api.gui;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;

@FunctionalInterface
public interface IGuiScreenHandler<T extends GuiScreen> extends Function<T, IGuiProperties> {
   @Nullable
   IGuiProperties apply(T var1);
}
