package betterquesting.questing;

import betterquesting.api.questing.IQuestLineEntry;
import net.minecraft.nbt.NBTTagCompound;

public class QuestLineEntry implements IQuestLineEntry {
   private int sizeX;
   private int sizeY;
   private int posX;
   private int posY;

   public QuestLineEntry(NBTTagCompound json) {
      this.sizeX = 0;
      this.sizeY = 0;
      this.posX = 0;
      this.posY = 0;
      this.readFromNBT(json);
   }

   public QuestLineEntry(int x, int y) {
      this(x, y, 24, 24);
   }

   /** @deprecated */
   @Deprecated
   public QuestLineEntry(int x, int y, int size) {
      this.sizeX = 0;
      this.sizeY = 0;
      this.posX = 0;
      this.posY = 0;
      this.sizeX = size;
      this.sizeY = size;
      this.posX = x;
      this.posY = y;
   }

   public QuestLineEntry(int x, int y, int sizeX, int sizeY) {
      this.sizeX = 0;
      this.sizeY = 0;
      this.posX = 0;
      this.posY = 0;
      this.sizeX = sizeX;
      this.sizeY = sizeY;
      this.posX = x;
      this.posY = y;
   }

   /** @deprecated */
   @Deprecated
   public int getSize() {
      return Math.max(this.getSizeX(), this.getSizeY());
   }

   public int getSizeX() {
      return this.sizeX;
   }

   public int getSizeY() {
      return this.sizeY;
   }

   public int getPosX() {
      return this.posX;
   }

   public int getPosY() {
      return this.posY;
   }

   public void setPosition(int posX, int posY) {
      this.posX = posX;
      this.posY = posY;
   }

   /** @deprecated */
   @Deprecated
   public void setSize(int size) {
      this.sizeX = size;
      this.sizeY = size;
   }

   public void setSize(int sizeX, int sizeY) {
      this.sizeX = sizeX;
      this.sizeY = sizeY;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      json.setInteger("sizeX", this.sizeX);
      json.setInteger("sizeY", this.sizeY);
      json.setInteger("x", this.posX);
      json.setInteger("y", this.posY);
      return json;
   }

   public void readFromNBT(NBTTagCompound json) {
      if (json.hasKey("size", 99)) {
         this.sizeX = json.getInteger("size");
         this.sizeY = this.sizeX;
      } else {
         this.sizeX = json.getInteger("sizeX");
         this.sizeY = json.getInteger("sizeY");
      }

      this.posX = json.getInteger("x");
      this.posY = json.getInteger("y");
   }
}
