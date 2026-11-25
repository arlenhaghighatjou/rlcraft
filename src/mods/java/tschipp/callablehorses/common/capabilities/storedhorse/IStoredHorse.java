package tschipp.callablehorses.common.capabilities.storedhorse;

public interface IStoredHorse {
   String getStorageUUID();

   void setStorageUUID(String var1);

   String getOwnerUUID();

   void setOwnerUUID(String var1);

   void setHorseNum(int var1);

   int getHorseNum();

   void setOwned(boolean var1);

   boolean isOwned();
}
