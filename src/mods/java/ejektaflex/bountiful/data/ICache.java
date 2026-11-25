package ejektaflex.bountiful.data;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\bf\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u0002*\b\b\u0001\u0010\u0003*\u00020\u00022\u00020\u0002J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00028\u0000H\u00a6\u0002\u00a2\u0006\u0002\u0010\u0007J\u0015\u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00028\u0000H&\u00a2\u0006\u0002\u0010\nJ\u001d\u0010\u000b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00028\u00002\u0006\u0010\f\u001a\u00028\u0001H&\u00a2\u0006\u0002\u0010\r\u00a8\u0006\u000e"},
   d2 = {"Lejektaflex/bountiful/data/ICache;", "T", "", "U", "contains", "", "item", "(Ljava/lang/Object;)Z", "invalidate", "", "(Ljava/lang/Object;)V", "store", "data", "(Ljava/lang/Object;Ljava/lang/Object;)V", "Bountiful"}
)
public interface ICache<T, U> {
   void invalidate(@NotNull T var1);

   void store(@NotNull T var1, @NotNull U var2);

   boolean contains(@NotNull T var1);
}
