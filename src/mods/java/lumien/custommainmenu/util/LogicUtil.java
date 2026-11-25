package lumien.custommainmenu.util;

import java.util.ArrayList;
import lumien.custommainmenu.lib.StringReplacer;
import net.minecraft.client.resources.I18n;

public class LogicUtil {
   public LogicUtil() {
   }

   public static ArrayList<String> getTooltip(String tooltipString) {
      ArrayList<String> tooltip = new ArrayList();
      String[] split = tooltipString.split("\n");

      for(String s : split) {
         tooltip.add(I18n.format(StringReplacer.replacePlaceholders(s), new Object[0]));
      }

      return tooltip;
   }
}
