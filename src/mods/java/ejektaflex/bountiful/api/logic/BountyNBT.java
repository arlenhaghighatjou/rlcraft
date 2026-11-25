package ejektaflex.bountiful.api.logic;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000f\u00a8\u0006\u0010"},
   d2 = {"Lejektaflex/bountiful/api/logic/BountyNBT;", "", "key", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getKey", "()Ljava/lang/String;", "setKey", "(Ljava/lang/String;)V", "BoardStamp", "BountyTime", "Rarity", "Worth", "BountyStamp", "ToGet", "Rewards", "Bountiful_api"}
)
public enum BountyNBT {
   BoardStamp,
   BountyTime,
   Rarity,
   Worth,
   BountyStamp,
   ToGet,
   Rewards;

   @NotNull
   private String key;

   @NotNull
   public final String getKey() {
      return this.key;
   }

   public final void setKey(@NotNull String var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      this.key = <set-?>;
   }

   protected BountyNBT(@NotNull String key) {
      Intrinsics.checkParameterIsNotNull(key, "key");
      super($enum_name_or_ordinal$0, $enum_name_or_ordinal$1);
      this.key = key;
   }
}
