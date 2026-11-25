package bq_standard.importers.hqm.converters.rewards;

import betterquesting.api.questing.rewards.IReward;
import bq_standard.rewards.RewardCommand;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.ArrayList;
import java.util.List;

public class HQMRewardCommand {
   public HQMRewardCommand() {
   }

   public IReward[] convertReward(JsonElement json) {
      if (!(json instanceof JsonArray)) {
         return null;
      } else {
         List<IReward> rList = new ArrayList();

         for(JsonElement je : json.getAsJsonArray()) {
            if (je instanceof JsonPrimitive) {
               RewardCommand reward = new RewardCommand();
               reward.command = je.getAsString();
               reward.viaPlayer = true;
               rList.add(reward);
            }
         }

         return (IReward[])rList.toArray(new IReward[0]);
      }
   }
}
