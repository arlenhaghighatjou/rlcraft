package crafttweaker.zenscript;

import crafttweaker.socket.SingleError;
import java.util.ArrayList;
import java.util.List;
import stanhebben.zenscript.util.ZenPosition;

public class CrtStoringErrorLogger extends CrTErrorLogger {
   private List<SingleError> errors = new ArrayList();

   public CrtStoringErrorLogger() {
   }

   public void clear() {
      this.errors.clear();
   }

   public List<SingleError> getErrors() {
      return this.errors;
   }

   public void error(ZenPosition position, String message) {
      super.error(position, message);
      this.errors.add(new SingleError(position.getFileName(), position.getLine(), position.getLineOffset(), message, SingleError.Level.ERROR));
   }

   public void warning(ZenPosition position, String message) {
      super.warning(position, message);
      this.errors.add(new SingleError(position.getFileName(), position.getLine(), position.getLineOffset(), message, SingleError.Level.WARN));
   }

   public void info(ZenPosition position, String message) {
      super.info(position, message);
      this.errors.add(new SingleError(position.getFileName(), position.getLine(), position.getLineOffset(), message, SingleError.Level.INFO));
   }
}
