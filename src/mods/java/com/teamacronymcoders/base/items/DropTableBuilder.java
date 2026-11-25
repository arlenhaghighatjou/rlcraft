package com.teamacronymcoders.base.items;

import java.util.LinkedList;
import java.util.List;

public class DropTableBuilder {
   private final List<String> items = new LinkedList();
   private boolean fortuneEnabled = false;
   private String oldSlots;

   public DropTableBuilder() {
      this.oldSlots = "";
   }

   public DropTableBuilder(String oldSlots) {
      this.oldSlots = oldSlots;
   }

   public DropTableBuilder newSlot() {
      this.oldSlots = this.toString();
      this.items.clear();
      this.fortuneEnabled = false;
      return this;
   }

   public DropTableBuilder addItem(String item) {
      this.items.add(item);
      return this;
   }

   public DropTableBuilder addItem(String item, int weight) {
      this.items.add(item + "%" + weight);
      return this;
   }

   public DropTableBuilder addItem(String item, int weight, int count) {
      this.items.add(item + "%" + weight + "%" + count);
      return this;
   }

   public DropTableBuilder enableFortune() {
      this.fortuneEnabled = true;
      return this;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder(this.oldSlots);
      sb.append("[").append((String)this.items.get(0));

      for(int i = 1; i < this.items.size(); ++i) {
         sb.append("#").append((String)this.items.get(i));
      }

      if (this.fortuneEnabled) {
         sb.append("$true");
      }

      return sb.append("]").toString();
   }
}
