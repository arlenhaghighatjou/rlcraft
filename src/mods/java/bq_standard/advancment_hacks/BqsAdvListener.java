package bq_standard.advancment_hacks;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.tasks.ITask;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.TaskTrigger;
import java.lang.reflect.Field;
import javax.annotation.Nonnull;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.ICriterionTrigger.Listener;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class BqsAdvListener<T extends ICriterionInstance> extends Listener<T> {
   private final ICriterionTrigger<T> trigType;
   private final Tuple<Integer, Integer> mappedIDs;
   private static final Field f_playerAdv = ReflectionHelper.findField(PlayerAdvancements.class, new String[]{"player", "player"});

   public BqsAdvListener(@Nonnull ICriterionTrigger<T> trigType, @Nonnull T critereon, int questID, int taskID) {
      super(critereon, (Advancement)null, "BQ_PROXY");
      this.trigType = trigType;
      this.mappedIDs = new Tuple(questID, taskID);
      AdvListenerManager.INSTANCE.registerListener(this);
   }

   public void registerSelf(PlayerAdvancements playerAdv) {
      this.trigType.addListener(playerAdv, this);
   }

   public void unregisterSelf(PlayerAdvancements playerAdv) {
      this.trigType.removeListener(playerAdv, this);
   }

   public void grantCriterion(PlayerAdvancements playerAdv) {
      try {
         IQuest q = (IQuest)((IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB)).getValue(((Integer)this.mappedIDs.getFirst()).intValue());
         if (q == null) {
            return;
         }

         ITask t = (ITask)q.getTasks().getValue(((Integer)this.mappedIDs.getSecond()).intValue());
         if (!(t instanceof TaskTrigger)) {
            return;
         }

         ((TaskTrigger)t).onCriteriaComplete((EntityPlayerMP)f_playerAdv.get(playerAdv), this, ((Integer)this.mappedIDs.getFirst()).intValue());
      } catch (Exception var4) {
         BQ_Standard.logger.error(var4);
      }

   }

   public boolean verify() {
      IQuest q = (IQuest)((IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB)).getValue(((Integer)this.mappedIDs.getFirst()).intValue());
      if (q == null) {
         return false;
      } else {
         ITask t = (ITask)q.getTasks().getValue(((Integer)this.mappedIDs.getSecond()).intValue());
         if (t instanceof TaskTrigger) {
            TaskTrigger tCon = (TaskTrigger)t;
            return tCon.getListener() == this;
         } else {
            return false;
         }
      }
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         Listener<?> listener = (Listener)p_equals_1_;
         return this.getCriterionInstance().equals(listener.getCriterionInstance());
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.getCriterionInstance().hashCode();
      i = 31 * i;
      i = 31 * i + "BQ_PROXY".hashCode();
      return i;
   }

   static {
      f_playerAdv.setAccessible(true);
   }
}
