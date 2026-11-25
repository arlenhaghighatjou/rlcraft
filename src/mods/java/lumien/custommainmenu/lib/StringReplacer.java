package lumien.custommainmenu.lib;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;

public class StringReplacer {
   static final String TIME_FORMAT = "HH:mm";
   static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

   public StringReplacer() {
   }

   public static String replacePlaceholders(String source) {
      if (source.indexOf('#') < 0) {
         return source;
      }
      if (source.contains("#date#")) {
         source = source.replace("#date#", DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault()).format(new Date()));
      }
      if (source.contains("#time#")) {
         source = source.replace("#time#", timeFormat.format(new Date()));
      }
      Loader loader = Loader.instance();
      return source.replace("#mcversion#", ForgeVersion.mcVersion)
         .replace("#fmlversion#", loader.getFMLVersionString())
         .replace("#mcpversion#", ForgeVersion.mcpVersion)
         .replace("#modsloaded#", Integer.toString(loader.getModList().size()))
         .replace("#modsactive#", Integer.toString(loader.getActiveModList().size()))
         .replace("#forgeversion#", ForgeVersion.getVersion())
         .replace("#username#", Minecraft.getMinecraft().getSession().getUsername());
   }
}
