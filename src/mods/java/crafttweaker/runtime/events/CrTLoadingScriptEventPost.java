package crafttweaker.runtime.events;

/** @deprecated */
@Deprecated
public class CrTLoadingScriptEventPost extends CrTScriptLoadingEvent.Post {
   public CrTLoadingScriptEventPost(String fileName) {
      super(fileName);
   }

   public String getFileName() {
      return super.getFileName();
   }
}
