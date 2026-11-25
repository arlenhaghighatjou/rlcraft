package bq_standard.importers.ftbq.converters.rewards;

import betterquesting.api.questing.rewards.IReward;
import bq_standard.importers.ftbq.FTBQUtils;
import bq_standard.rewards.RewardItem;
import net.minecraft.nbt.NBTTagCompound;

public class FtbqRewardItem {
   public FtbqRewardItem() {
   }

   public IReward[] convertTask(NBTTagCompound tag) {
      RewardItem reward = new RewardItem();
      reward.items.add(FTBQUtils.convertItem(tag.getTag("item")));
      return new IReward[]{reward};
   }
}
