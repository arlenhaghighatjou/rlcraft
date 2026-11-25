package mezz.jei.api.gui;

import java.util.List;

@FunctionalInterface
public interface ITooltipCallback<T> {
   void onTooltip(int var1, boolean var2, T var3, List<String> var4);
}
