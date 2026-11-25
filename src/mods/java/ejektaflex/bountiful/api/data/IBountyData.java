package ejektaflex.bountiful.api.data;

import ejektaflex.bountiful.api.logic.IStageRequirement;
import ejektaflex.bountiful.api.logic.picked.IPickedEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack;
import ejektaflex.bountiful.api.registry.IValueRegistry;
import kotlin.Metadata;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\bf\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u00012\u00020\u0003J\u0010\u0010 \u001a\u00020\u000b2\u0006\u0010!\u001a\u00020\"H&J\u0010\u0010#\u001a\u00020$2\u0006\u0010!\u001a\u00020\"H&J\u0010\u0010%\u001a\u00020\u000b2\u0006\u0010!\u001a\u00020\"H&R\u0018\u0010\u0004\u001a\u00020\u0005X¦\u000e¢\u0006\f\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u0004\u0018\u00010\u000bX¦\u000e¢\u0006\f\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u0018\u0010\u0010\u001a\u00020\u000bX¦\u000e¢\u0006\f\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u0018\u0010\u0015\u001a\u00020\u0005X¦\u000e¢\u0006\f\u001a\u0004\b\u0016\u0010\u0007\"\u0004\b\u0017\u0010\tR\u0018\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0019X¦\u0004¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u001cR\u0018\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0019X¦\u0004¢\u0006\u0006\u001a\u0004\b\u001f\u0010\u001c¨\u0006&"},
   d2 = {"Lejektaflex/bountiful/api/data/IBountyData;", "Lnet/minecraftforge/common/util/INBTSerializable;", "Lnet/minecraft/nbt/NBTTagCompound;", "Lejektaflex/bountiful/api/logic/IStageRequirement;", "boardStamp", "", "getBoardStamp", "()I", "setBoardStamp", "(I)V", "bountyStamp", "", "getBountyStamp", "()Ljava/lang/Long;", "setBountyStamp", "(Ljava/lang/Long;)V", "bountyTime", "getBountyTime", "()J", "setBountyTime", "(J)V", "rarity", "getRarity", "setRarity", "rewards", "Lejektaflex/bountiful/api/registry/IValueRegistry;", "Lejektaflex/bountiful/api/logic/picked/PickedEntryStack;", "getRewards", "()Lejektaflex/bountiful/api/registry/IValueRegistry;", "toGet", "Lejektaflex/bountiful/api/logic/picked/IPickedEntry;", "getToGet", "boardTimeLeft", "world", "Lnet/minecraft/world/World;", "hasExpired", "", "timeLeft", "Bountiful_api"}
)
public interface IBountyData extends INBTSerializable<NBTTagCompound>, IStageRequirement {
   int getBoardStamp();

   void setBoardStamp(int var1);

   long getBountyTime();

   void setBountyTime(long var1);

   int getRarity();

   void setRarity(int var1);

   @NotNull
   IValueRegistry<IPickedEntry> getToGet();

   @NotNull
   IValueRegistry<PickedEntryStack> getRewards();

   @Nullable
   Long getBountyStamp();

   void setBountyStamp(@Nullable Long var1);

   long timeLeft(@NotNull World var1);

   boolean hasExpired(@NotNull World var1);

   long boardTimeLeft(@NotNull World var1);
}
