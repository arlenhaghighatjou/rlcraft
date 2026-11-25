package crafttweaker.runtime.events;

/** @deprecated */
@Deprecated
public class CrTLoadingScriptEventPre extends CrTScriptLoadingEvent.Pre {
   public CrTLoadingScriptEventPre(String fileName) {
      super(fileName);
   }

   public String getFileName() {
      return super.getFileName();
   }
}
