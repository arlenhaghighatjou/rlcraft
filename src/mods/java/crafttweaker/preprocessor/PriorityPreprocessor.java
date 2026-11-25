package crafttweaker.preprocessor;

import crafttweaker.runtime.ScriptFile;

public class PriorityPreprocessor extends PreprocessorActionBase {
   private static final String PREPROCESSOR_NAME = "priority";
   private Integer priority;

   public PriorityPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
      super(fileName, preprocessorLine, lineIndex);
      String s = preprocessorLine.substring(9).trim();

      try {
         this.priority = Integer.parseInt(s);
      } catch (NumberFormatException var6) {
         this.priority = null;
      }

   }

   public void executeActionOnFind(ScriptFile scriptFile) {
      if (this.priority != null) {
         scriptFile.setPriority(this.priority.intValue());
      }

   }

   public String getPreprocessorName() {
      return "priority";
   }
}
