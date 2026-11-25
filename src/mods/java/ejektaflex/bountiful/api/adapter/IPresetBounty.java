package ejektaflex.bountiful.api.adapter;

import ejektaflex.bountiful.api.data.IWeighted;
import ejektaflex.bountiful.api.logic.picked.IPickedEntry;
import java.util.List;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\b\u0010\u0011\u001a\u00020\u0012H&J\u000e\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014H&J\u000e\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014H&R\u001c\u0010\u0002\u001a\f\u0012\b\u0012\u00060\u0004R\u00020\u00050\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u001c\u0010\b\u001a\f\u0012\b\u0012\u00060\tR\u00020\u00050\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u0007R\u0018\u0010\u000b\u001a\u00020\fX\u00a6\u000e\u00a2\u0006\f\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0017"},
   d2 = {"Lejektaflex/bountiful/api/adapter/IPresetBounty;", "Lejektaflex/bountiful/api/data/IWeighted;", "objectives", "", "Lejektaflex/bountiful/api/adapter/PresetBounty$BountyObjective;", "Lejektaflex/bountiful/api/adapter/PresetBounty;", "getObjectives", "()Ljava/util/List;", "rewards", "Lejektaflex/bountiful/api/adapter/PresetBounty$BountyReward;", "getRewards", "weight", "", "getWeight", "()I", "setWeight", "(I)V", "isValid", "", "objectiveContent", "", "Lejektaflex/bountiful/api/logic/picked/IPickedEntry;", "rewardContent", "Bountiful_api"}
)
public interface IPresetBounty extends IWeighted {
   @NotNull
   List<PresetBounty.BountyObjective> getObjectives();

   @NotNull
   List<PresetBounty.BountyReward> getRewards();

   int getWeight();

   void setWeight(int var1);

   boolean isValid();

   @NotNull
   List<IPickedEntry> objectiveContent();

   @NotNull
   List<IPickedEntry> rewardContent();
}
