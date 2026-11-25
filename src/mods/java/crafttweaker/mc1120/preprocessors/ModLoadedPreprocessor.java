package crafttweaker.mc1120.preprocessors;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.preprocessor.PreprocessorActionBase;
import crafttweaker.runtime.ScriptFile;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.common.Loader;

public class ModLoadedPreprocessor extends PreprocessorActionBase {
   private static final String PREPROCESSOR_NAME = "modloaded";
   private List<ModLoadedPreprocessor.LoadedMod> modNames = new ArrayList();

   public ModLoadedPreprocessor(String fileName, String preprocessorLine, int lineIndex) {
      super(fileName, preprocessorLine, lineIndex);
      String s = preprocessorLine.substring(10);
      String[] names = s.split(" ");

      for(String name : names) {
         String n = name.trim();
         if (n.length() > 0) {
            boolean shouldBeLoaded = true;
            if (n.startsWith("!")) {
               n = n.substring(1);
               shouldBeLoaded = false;
            }

            this.modNames.add(new ModLoadedPreprocessor.LoadedMod(n, shouldBeLoaded));
         }
      }

   }

   public void executeActionOnFind(ScriptFile scriptFile) {
      if (!this.checkAreLoaded(this.modNames)) {
         CraftTweakerAPI.logInfo("Ignoring script " + scriptFile + " due to the following #modloaded preprocessor settings: " + this.modNames);
         scriptFile.setParsingBlocked(true);
         scriptFile.setCompileBlocked(true);
         scriptFile.setExecutionBlocked(true);
      }

   }

   public String getPreprocessorName() {
      return "modloaded";
   }

   private boolean checkAreLoaded(List<ModLoadedPreprocessor.LoadedMod> modNames) {
      for(ModLoadedPreprocessor.LoadedMod mod : modNames) {
         if (Loader.isModLoaded(mod.modName) != mod.shouldBeLoaded) {
            return false;
         }
      }

      return true;
   }

   private static class LoadedMod {
      String modName;
      boolean shouldBeLoaded;

      LoadedMod(String modName, boolean shouldBeLoaded) {
         this.modName = modName;
         this.shouldBeLoaded = shouldBeLoaded;
      }

      public String toString() {
         return "{" + (this.shouldBeLoaded ? "" : "!") + this.modName + "}";
      }
   }
}
