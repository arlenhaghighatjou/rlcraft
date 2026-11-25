package crafttweaker.preprocessor;

import crafttweaker.runtime.ScriptFile;

public class CrTScriptLoadEvent {
   private ScriptFile scriptFile;

   public CrTScriptLoadEvent(ScriptFile scriptFile) {
      this.scriptFile = scriptFile;
   }

   public ScriptFile getScriptFile() {
      return this.scriptFile;
   }
}
