package stanhebben.zenscript.definitions;

import java.util.List;
import stanhebben.zenscript.util.ZenPosition;

public class Import {
   private final ZenPosition position;
   private final List<String> name;
   private final String rename;

   public Import(ZenPosition position, List<String> name, String rename) {
      this.position = position;
      this.name = name;
      this.rename = rename;
   }

   public ZenPosition getPosition() {
      return this.position;
   }

   public List<String> getName() {
      return this.name;
   }

   public String getRename() {
      return this.rename == null ? (String)this.name.get(this.name.size() - 1) : this.rename;
   }
}
