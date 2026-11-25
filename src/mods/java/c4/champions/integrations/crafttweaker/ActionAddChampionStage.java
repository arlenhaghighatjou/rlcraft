package c4.champions.integrations.crafttweaker;

import c4.champions.integrations.gamestages.ChampionStages;
import crafttweaker.IAction;

public class ActionAddChampionStage implements IAction {
   private final String stage;
   private final String entity;
   private final int dimension;
   private final boolean dimensional;

   public ActionAddChampionStage(String stage, String entity) {
      this(stage, entity, 0, false);
   }

   public ActionAddChampionStage(String stage, String entity, int dimension) {
      this(stage, entity, dimension, true);
   }

   public ActionAddChampionStage(String stage, String entity, int dimension, boolean dimensional) {
      this.stage = stage;
      this.entity = entity;
      this.dimension = dimension;
      this.dimensional = dimensional;
   }

   public void apply() {
      if (this.dimensional) {
         ChampionStages.addStage(this.entity, this.stage, this.dimension);
      } else {
         ChampionStages.addStage(this.entity, this.stage);
      }

   }

   public String describe() {
      return "Adding " + this.entity + " to stage " + this.stage + (this.dimensional ? " for dimension " + this.dimension : "");
   }
}
