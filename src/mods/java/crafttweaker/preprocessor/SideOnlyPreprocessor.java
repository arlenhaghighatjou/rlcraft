package crafttweaker.preprocessor;

import crafttweaker.api.network.NetworkSide;
import crafttweaker.runtime.ScriptFile;

public class SideOnlyPreprocessor extends PreprocessorActionBase {
   public static final String PREPROCESSOR_NAME = "sideonly";
   private NetworkSide side;

   public SideOnlyPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
      super(fileName, preprocessorLine, lineIndex);
      String s = preprocessorLine.substring("sideonly".length() + 1);
      String sideName = s.trim();
      byte var7 = -1;
      switch(sideName.hashCode()) {
      case -1357712437:
         if (sideName.equals("client")) {
            var7 = 0;
         }
         break;
      case -905826493:
         if (sideName.equals("server")) {
            var7 = 1;
         }
      }

      switch(var7) {
      case 0:
         this.side = NetworkSide.SIDE_CLIENT;
         break;
      case 1:
         this.side = NetworkSide.SIDE_SERVER;
         break;
      default:
         this.side = NetworkSide.INVALID_SIDE;
      }

   }

   public void executeActionOnFind(ScriptFile scriptFile) {
      scriptFile.setNetworkSide(this.side);
   }

   public String getPreprocessorName() {
      return "sideonly";
   }
}
