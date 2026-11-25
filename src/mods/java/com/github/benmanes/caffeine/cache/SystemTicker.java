package com.github.benmanes.caffeine.cache;

enum SystemTicker implements Ticker {
   INSTANCE;

   private SystemTicker() {
   }

   public long read() {
      return System.nanoTime();
   }
}
