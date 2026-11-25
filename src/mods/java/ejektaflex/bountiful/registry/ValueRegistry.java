package ejektaflex.bountiful.registry;

import ejektaflex.bountiful.api.registry.IValueRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0016\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u0005¢\u0006\u0002\u0010\u0004J!\u0010\t\u001a\u00020\n2\u0012\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00028\u00000\u000b\"\u00028\u0000H\u0016¢\u0006\u0002\u0010\fJ\u000e\u0010\r\u001a\b\u0012\u0004\u0012\u00028\u00000\u000eH\u0016J\b\u0010\u000f\u001a\u00020\nH\u0016J\u0015\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0013J5\u0010\u0014\u001a\b\u0012\u0004\u0012\u00028\u00000\u000e2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00028\u00000\u000e2\u0017\u0010\u0016\u001a\u0013\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\u00110\u0017¢\u0006\u0002\b\u0018H\u0016J\u0016\u0010\u0019\u001a\u00020\n2\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00028\u00000\u000eH\u0016R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00028\u00000\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u001b"},
   d2 = {"Lejektaflex/bountiful/registry/ValueRegistry;", "T", "", "Lejektaflex/bountiful/api/registry/IValueRegistry;", "()V", "items", "", "getItems", "()Ljava/util/List;", "add", "", "", "([Ljava/lang/Object;)V", "backup", "", "empty", "remove", "", "item", "(Ljava/lang/Object;)Z", "replace", "newItems", "condition", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "restore", "backupList", "Bountiful"}
)
public class ValueRegistry<T> implements IValueRegistry<T> {
   @NotNull
   private final List<T> items;

   @NotNull
   public final List<T> getItems() {
      return this.items;
   }

   public void add(@NotNull T... items) {
      Intrinsics.checkParameterIsNotNull(items, "items");

      for(Object item : items) {
         this.items.add(item);
      }

   }

   public boolean remove(@NotNull T item) {
      Intrinsics.checkParameterIsNotNull(item, "item");
      return this.items.remove(item);
   }

   @NotNull
   public List<T> backup() {
      List var1 = (List)(new ArrayList());
      var1.addAll((Collection)this.items);
      return var1;
   }

   public void restore(@NotNull List<? extends T> backupList) {
      Intrinsics.checkParameterIsNotNull(backupList, "backupList");
      this.empty();
      this.items.addAll((Collection)backupList);
   }

   public void empty() {
      this.items.clear();
   }

   @NotNull
   public List<T> replace(@NotNull List<? extends T> newItems, @NotNull Function1<? super T, Boolean> condition) {
      Intrinsics.checkParameterIsNotNull(newItems, "newItems");
      Intrinsics.checkParameterIsNotNull(condition, "condition");
      List invalids = (List)(new ArrayList());
      List dataBackup = this.backup();
      this.empty();

      for(Object item : newItems) {
         if (((Boolean)condition.invoke(item)).booleanValue()) {
            this.add(item);
         } else {
            invalids.add(item);
         }
      }

      Collection var7 = (Collection)invalids;
      if (!var7.isEmpty()) {
         this.restore(dataBackup);
      }

      return invalids;
   }

   public ValueRegistry() {
      List var2 = (List)(new ArrayList());
      this.items = var2;
   }
}
