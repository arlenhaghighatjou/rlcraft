package ivorius.reccomplex.files.loading;

import java.util.Set;

public interface LeveledRegistry<S> {
   S register(String var1, String var2, S var3, boolean var4, LeveledRegistry.ILevel var5);

   S unregister(String var1, LeveledRegistry.ILevel var2);

   S get(String var1);

   LeveledRegistry.Status status(String var1);

   boolean has(String var1);

   Set<String> ids();

   void clear(LeveledRegistry.ILevel var1);

   public interface ILevel {
      int getLevel();
   }

   public static enum Level implements LeveledRegistry.ILevel {
      INTERNAL,
      MODDED,
      CUSTOM,
      SERVER;

      private Level() {
      }

      public int getLevel() {
         return this.ordinal();
      }

      public boolean isCustom() {
         return this == CUSTOM;
      }

      public boolean isServer() {
         return this == SERVER;
      }
   }

   public interface Status {
      String getId();

      boolean isActive();

      void setActive(boolean var1);

      String getDomain();

      LeveledRegistry.ILevel getLevel();
   }
}
