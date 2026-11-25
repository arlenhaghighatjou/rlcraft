package com.github.benmanes.caffeine.cache;

enum SingletonWeigher implements Weigher<Object, Object> {
   INSTANCE;

   private SingletonWeigher() {
   }

   public int weigh(Object key, Object value) {
      return 1;
   }
}
