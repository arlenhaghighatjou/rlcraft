package com.elenai.elenaidodge.capability.joined;

public class Joined implements IJoined {
   private boolean joined = false;

   public Joined() {
   }

   public void join() {
      this.joined = true;
   }

   public boolean hasJoined() {
      return this.joined;
   }

   public void set(boolean bool) {
      this.joined = bool;
   }
}
