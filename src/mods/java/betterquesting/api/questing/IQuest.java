package betterquesting.api.questing;

import betterquesting.api.enums.EnumQuestState;
import betterquesting.api.properties.IPropertyContainer;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.storage.IDatabaseNBT;
import betterquesting.api2.storage.INBTProgress;
import betterquesting.api2.storage.INBTSaveLoad;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public interface IQuest extends INBTSaveLoad<NBTTagCompound>, INBTProgress<NBTTagCompound>, IPropertyContainer {
   EnumQuestState getState(UUID var1);

   @Nullable
   NBTTagCompound getCompletionInfo(UUID var1);

   void setCompletionInfo(UUID var1, @Nullable NBTTagCompound var2);

   void update(EntityPlayer var1);

   void detect(EntityPlayer var1);

   boolean isUnlocked(UUID var1);

   boolean canSubmit(EntityPlayer var1);

   boolean isComplete(UUID var1);

   void setComplete(UUID var1, long var2);

   boolean canClaim(EntityPlayer var1);

   boolean hasClaimed(UUID var1);

   void claimReward(EntityPlayer var1);

   void setClaimed(UUID var1, long var2);

   void resetUser(@Nullable UUID var1, boolean var2);

   IDatabaseNBT<ITask, NBTTagList, NBTTagList> getTasks();

   IDatabaseNBT<IReward, NBTTagList, NBTTagList> getRewards();

   @Nonnull
   int[] getRequirements();

   void setRequirements(@Nonnull int[] var1);
}
