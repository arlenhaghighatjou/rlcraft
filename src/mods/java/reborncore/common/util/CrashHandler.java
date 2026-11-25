package reborncore.common.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.common.ICrashCallable;
import reborncore.RebornCore;

public class CrashHandler implements ICrashCallable {
   public CrashHandler() {
   }

   public String getLabel() {
      return "RebornCore";
   }

   public String call() throws Exception {
      StringBuilder builder = new StringBuilder();

      for(String str : this.getInfo()) {
         builder.append("\n\t\t" + str);
      }

      return builder.toString();
   }

   public List<String> getInfo() {
      List<String> str = new ArrayList();
      RebornCore.proxy.getCrashData(str);
      return str;
   }
}
