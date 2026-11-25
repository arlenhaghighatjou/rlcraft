package stanhebben.zenscript.dump;

import java.util.Collections;
import java.util.List;

public interface IDumpConvertable {
   default List<? extends IDumpable> asDumpedObject() {
      return Collections.emptyList();
   }
}
