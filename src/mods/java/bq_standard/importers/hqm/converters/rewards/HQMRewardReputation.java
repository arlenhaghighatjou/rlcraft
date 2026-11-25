package bq_standard.importers.hqm.converters.rewards;

import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.utils.JsonHelper;
import bq_standard.importers.hqm.HQMQuestImporter;
import bq_standard.importers.hqm.converters.HQMRep;
import bq_standard.rewards.RewardScoreboard;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class HQMRewardReputation {
   public HQMRewardReputation() {
   }

   public IReward[] convertReward(JsonElement json) {
      if (!(json instanceof JsonArray)) {
         return null;
      } else {
         List<IReward> rList = new ArrayList();

         for(JsonElement je : json.getAsJsonArray()) {
            if (je instanceof JsonObject) {
               JsonObject jRep = je.getAsJsonObject();
               JsonElement jid = jRep.get("reputation");
               if (jid != null && jid.isJsonPrimitive()) {
                  String repId;
                  if (jid.getAsJsonPrimitive().isString()) {
                     repId = JsonHelper.GetString(jRep, "reputation", "");
                  } else {
                     repId = JsonHelper.GetNumber(jRep, "reputation", Integer.valueOf(0)).toString();
                  }

                  HQMRep repObj = (HQMRep)HQMQuestImporter.INSTANCE.reputations.get(repId);
                  if (repObj != null) {
                     RewardScoreboard reward = new RewardScoreboard();
                     reward.score = repObj.rName;
                     reward.value = JsonHelper.GetNumber(jRep, "value", Integer.valueOf(1)).intValue();
                     rList.add(reward);
                  }
               }
            }
         }

         return (IReward[])rList.toArray(new IReward[0]);
      }
   }
}
