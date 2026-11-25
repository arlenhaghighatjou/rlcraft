package bedbreakbegone.core;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;

@Mod(
   modid = "bedbreakbegone",
   version = "1.0.2",
   name = "BedBreakBegone",
   acceptableRemoteVersions = "*"
)
public class BedBreakBegone {
   public static final String MODID = "bedbreakbegone";
   public static final String VERSION = "1.0.2";
   public static final String NAME = "BedBreakBegone";
   public static final String CHANNEL = "BEDBREAKBEGONE";
   @Instance("bedbreakbegone")
   public static BedBreakBegone instance;

   public BedBreakBegone() {
   }
}
