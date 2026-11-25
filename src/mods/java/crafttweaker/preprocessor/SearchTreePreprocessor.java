package crafttweaker.preprocessor;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.runtime.ScriptFile;

public class SearchTreePreprocessor extends PreprocessorActionBase {
   public static final String PREPROCESSOR_NAME = "disable_search_tree";

   public SearchTreePreprocessor(String fileName, String preprocessorLine, int lineIndex) {
      super(fileName, preprocessorLine, lineIndex);
   }

   public void executeActionOnFind(ScriptFile scriptFile) {
      CraftTweakerAPI.logInfo("disable_search_tree Preprocessor found in " + scriptFile + ", disabling the search_tree");
      CraftTweakerAPI.ENABLE_SEARCH_TREE_RECALCULATION = false;
   }

   public String getPreprocessorName() {
      return "disable_search_tree";
   }
}
