package com.github.benmanes.caffeine.cache;

public enum RemovalCause {
   EXPLICIT {
      public boolean wasEvicted() {
         return false;
      }
   },
   REPLACED {
      public boolean wasEvicted() {
         return false;
      }
   },
   COLLECTED {
      public boolean wasEvicted() {
         return true;
      }
   },
   EXPIRED {
      public boolean wasEvicted() {
         return true;
      }
   },
   SIZE {
      public boolean wasEvicted() {
         return true;
      }
   };

   private RemovalCause() {
   }

   public abstract boolean wasEvicted();
}
