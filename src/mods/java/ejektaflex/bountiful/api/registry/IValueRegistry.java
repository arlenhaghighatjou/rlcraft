package ejektaflex.bountiful.api.registry;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\u00020\u0002J!\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00028\u00000\u0006\"\u00028\u0000H&¢\u0006\u0002\u0010\u0007J\u000e\u0010\b\u001a\b\u0012\u0004\u0012\u00028\u00000\tH&J\b\u0010\n\u001a\u00020\u0004H&J\u0015\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00028\u0000H&¢\u0006\u0002\u0010\u000eJ5\u0010\u000f\u001a\b\u0012\u0004\u0012\u00028\u00000\t2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00028\u00000\t2\u0017\u0010\u0011\u001a\u0013\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\f0\u0012¢\u0006\u0002\b\u0013H&J\u0016\u0010\u0014\u001a\u00020\u00042\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00028\u00000\tH&¨\u0006\u0016"},
   d2 = {"Lejektaflex/bountiful/api/registry/IValueRegistry;", "T", "", "add", "", "items", "", "([Ljava/lang/Object;)V", "backup", "", "empty", "remove", "", "item", "(Ljava/lang/Object;)Z", "replace", "newItems", "condition", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "restore", "backupList", "Bountiful_api"}
)
public interface IValueRegistry<T> {
   void add(@NotNull T... var1);

   boolean remove(@NotNull T var1);

   @NotNull
   List<T> backup();

   void restore(@NotNull List<? extends T> var1);

   void empty();

   @NotNull
   List<T> replace(@NotNull List<? extends T> var1, @NotNull Function1<? super T, Boolean> var2);
}
