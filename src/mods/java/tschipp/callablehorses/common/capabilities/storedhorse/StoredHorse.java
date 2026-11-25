package tschipp.callablehorses.common.capabilities.storedhorse;

public class StoredHorse implements IStoredHorse {
   private String storageUUID = "";
   private String ownerUUID = "";
   private int horseNum = 0;
   private boolean owned = false;

   public StoredHorse() {
   }

   public String getStorageUUID() {
      return this.storageUUID;
   }

   public void setStorageUUID(String uuid) {
      this.storageUUID = uuid;
   }

   public String getOwnerUUID() {
      return this.ownerUUID;
   }

   public void setOwnerUUID(String uuid) {
      this.ownerUUID = uuid;
   }

   public void setHorseNum(int num) {
      this.horseNum = num;
   }

   public int getHorseNum() {
      return this.horseNum;
   }

   public void setOwned(boolean bool) {
      this.owned = bool;
   }

   public boolean isOwned() {
      return this.owned;
   }
}
