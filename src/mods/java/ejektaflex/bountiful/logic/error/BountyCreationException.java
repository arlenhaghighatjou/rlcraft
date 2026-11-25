package ejektaflex.bountiful.logic.error;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004¨\u0006\u0005"},
   d2 = {"Lejektaflex/bountiful/logic/error/BountyCreationException;", "Ljava/lang/Exception;", "msg", "", "(Ljava/lang/String;)V", "Bountiful"}
)
public final class BountyCreationException extends Exception {
   public BountyCreationException(@NotNull String msg) {
      Intrinsics.checkParameterIsNotNull(msg, "msg");
      super(msg);
   }
}
