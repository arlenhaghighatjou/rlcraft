package com.github.benmanes.caffeine.cache;

enum DisabledTicker implements Ticker {
   INSTANCE;

   private DisabledTicker() {
   }

   public long read() {
      return 0L;
   }
}
