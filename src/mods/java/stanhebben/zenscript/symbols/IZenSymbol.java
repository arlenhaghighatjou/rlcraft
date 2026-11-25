package stanhebben.zenscript.symbols;

import java.util.Collections;
import java.util.List;
import stanhebben.zenscript.dump.IDumpConvertable;
import stanhebben.zenscript.dump.IDumpable;
import stanhebben.zenscript.dump.types.DumpDummy;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.util.ZenPosition;

public interface IZenSymbol extends IDumpConvertable {
   IPartialExpression instance(ZenPosition var1);

   default List<? extends IDumpable> asDumpedObject() {
      return Collections.singletonList(new DumpDummy(this));
   }
}
