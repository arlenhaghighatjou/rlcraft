package mods.octarinecore.client.resource;

import java.util.Comparator;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 3,
   d1 = {"\u0000\u001e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\b\u0006\n\u0002\b\u0006\n\u0002\b\u0006\n\u0002\b\u0006\n\u0002\b\u0007\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u000e\u0010\u0003\u001a\n \u0004*\u0004\u0018\u0001H\u0002H\u00022\u000e\u0010\u0005\u001a\n \u0004*\u0004\u0018\u0001H\u0002H\u0002H\n\u00a2\u0006\u0004\b\u0006\u0010\u0007\u00a8\u0006\b"},
   d2 = {"<anonymous>", "", "T", "a", "kotlin.jvm.PlatformType", "b", "compare", "(Ljava/lang/Object;Ljava/lang/Object;)I", "kotlin/comparisons/ComparisonsKt__ComparisonsKt$compareBy$2"}
)
public final class ParameterList$toString$$inlined$sortedBy$1<T> implements Comparator<T> {
   public ParameterList$toString$$inlined$sortedBy$1() {
   }

   public final int compare(T a, T b) {
      boolean var3 = false;
      Entry it = (Entry)a;
      int $i$a$-sortedBy-ParameterList$toString$1 = 0;
      Comparable var10000 = (Comparable)((String)it.getKey());
      it = (Entry)b;
      Comparable var6 = var10000;
      $i$a$-sortedBy-ParameterList$toString$1 = 0;
      String var7 = (String)it.getKey();
      return ComparisonsKt.compareValues(var6, (Comparable)var7);
   }
}
