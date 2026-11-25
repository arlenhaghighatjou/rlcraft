package ivorius.reccomplex..shadow.mcopts;

import ivorius.reccomplex..shadow.mcopts.translation.ServerTranslations;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class MCOpts {
   public static final String NAME = "MCOpts";
   public static final String VERSION = "0.9.9.4";
   public static Logger logger = LogManager.getLogger("MCOpts");
   public static final ServerTranslations translations = new ServerTranslations() {
      public boolean translateServerSide() {
         return true;
      }
   };

   public MCOpts() {
   }
}
