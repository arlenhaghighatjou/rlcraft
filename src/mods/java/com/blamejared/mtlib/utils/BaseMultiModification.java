package com.blamejared.mtlib.utils;

import crafttweaker.IAction;
import java.util.LinkedList;

public class BaseMultiModification extends BaseAction {
   protected final LinkedList<IAction> actions = new LinkedList();

   protected BaseMultiModification(String name) {
      super(name);
   }

   public void apply() {
      for(IAction action : this.actions) {
         action.apply();
      }

   }

   public String describe() {
      return String.format("Applying %d actions for %s Recipe change", this.actions.size(), this.name);
   }
}
