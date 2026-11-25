package ejektaflex.bountiful.api.logic;

import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\b\u0006\bf\u0018\u00002\u00020\u0001J\u000e\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00030\tH\u0016R\u0018\u0010\u0002\u001a\u00020\u0003X¦\u000e¢\u0006\f\u001a\u0004\b\u0004\u0010\u0005\"\u0004\b\u0006\u0010\u0007R \u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\tX¦\u000e¢\u0006\f\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\r¨\u0006\u000f"},
   d2 = {"Lejektaflex/bountiful/api/logic/IPickCommon;", "Lejektaflex/bountiful/api/logic/IStageRequirement;", "content", "", "getContent", "()Ljava/lang/String;", "setContent", "(Ljava/lang/String;)V", "stages", "", "getStages", "()Ljava/util/List;", "setStages", "(Ljava/util/List;)V", "requiredStages", "Bountiful_api"}
)
public interface IPickCommon extends IStageRequirement {
   @NotNull
   String getContent();

   void setContent(@NotNull String var1);

   @Nullable
   List<String> getStages();

   void setStages(@Nullable List<String> var1);

   @NotNull
   List<String> requiredStages();

   @Metadata(
      mv = {1, 1, 13},
      bv = {1, 0, 3},
      k = 3
   )
   public static final class DefaultImpls {
      @NotNull
      public static List<String> requiredStages(IPickCommon $this) {
         List var10000 = $this.getStages();
         if (var10000 == null) {
            var10000 = (List)(new ArrayList());
         }

         return var10000;
      }
   }
}
