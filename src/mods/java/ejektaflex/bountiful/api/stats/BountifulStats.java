package ejektaflex.bountiful.api.stats;

import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0011B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u000f\u001a\u00020\u0010R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0011\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006R\u0011\u0010\t\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0006R\u0011\u0010\u000b\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0006R\u0011\u0010\r\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u0006\u00a8\u0006\u0012"},
   d2 = {"Lejektaflex/bountiful/api/stats/BountifulStats;", "", "()V", "bountiesCommon", "Lejektaflex/bountiful/api/stats/BountifulStats$BountifulStat;", "getBountiesCommon", "()Lejektaflex/bountiful/api/stats/BountifulStats$BountifulStat;", "bountiesCompleted", "getBountiesCompleted", "bountiesEpic", "getBountiesEpic", "bountiesRare", "getBountiesRare", "bountiesUncommon", "getBountiesUncommon", "register", "", "BountifulStat", "Bountiful_api"}
)
public final class BountifulStats {
   @NotNull
   private static final BountifulStats.BountifulStat bountiesCompleted = new BountifulStats.BountifulStat("bountiesCompleted");
   @NotNull
   private static final BountifulStats.BountifulStat bountiesCommon = new BountifulStats.BountifulStat("bountiesCommon");
   @NotNull
   private static final BountifulStats.BountifulStat bountiesUncommon = new BountifulStats.BountifulStat("bountiesUncommon");
   @NotNull
   private static final BountifulStats.BountifulStat bountiesRare = new BountifulStats.BountifulStat("bountiesRare");
   @NotNull
   private static final BountifulStats.BountifulStat bountiesEpic = new BountifulStats.BountifulStat("bountiesEpic");
   public static final BountifulStats INSTANCE;

   @NotNull
   public final BountifulStats.BountifulStat getBountiesCompleted() {
      return bountiesCompleted;
   }

   @NotNull
   public final BountifulStats.BountifulStat getBountiesCommon() {
      return bountiesCommon;
   }

   @NotNull
   public final BountifulStats.BountifulStat getBountiesUncommon() {
      return bountiesUncommon;
   }

   @NotNull
   public final BountifulStats.BountifulStat getBountiesRare() {
      return bountiesRare;
   }

   @NotNull
   public final BountifulStats.BountifulStat getBountiesEpic() {
      return bountiesEpic;
   }

   public final boolean register() {
      for(Object element$iv : (Iterable)CollectionsKt.listOf(new BountifulStats.BountifulStat[]{bountiesCompleted, bountiesCommon, bountiesUncommon, bountiesRare, bountiesEpic})) {
         BountifulStats.BountifulStat it = (BountifulStats.BountifulStat)element$iv;
         it.registerStat();
      }

      return true;
   }

   private BountifulStats() {
   }

   static {
      BountifulStats var0 = new BountifulStats();
      INSTANCE = var0;
   }

   @Metadata(
      mv = {1, 1, 13},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0016\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005"},
      d2 = {"Lejektaflex/bountiful/api/stats/BountifulStats$BountifulStat;", "Lnet/minecraft/stats/StatBasic;", "statRoot", "", "(Ljava/lang/String;)V", "Bountiful_api"}
   )
   public static class BountifulStat extends StatBasic {
      public BountifulStat(@NotNull String statRoot) {
         Intrinsics.checkParameterIsNotNull(statRoot, "statRoot");
         super("stat." + statRoot, (ITextComponent)(new TextComponentTranslation("bountiful.stat." + statRoot, new Object[0])));
      }
   }
}
