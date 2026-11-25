package crafttweaker.socket.messages;

import crafttweaker.socket.SingleError;
import java.util.List;

public class LintResponseMessage extends SocketMessage<LintResponseMessage> {
   public static final String MESSAGE_TYPE = "LintResponse";
   public List<SingleError> errors;
   public boolean loadSuccessful;

   public LintResponseMessage(List<SingleError> errors, boolean loadSuccessful) {
      super("LintResponse");
      this.errors = errors;
      this.loadSuccessful = loadSuccessful;
   }

   public String toString() {
      return "LintResponseMessage{errors=" + this.errors + '}';
   }
}
