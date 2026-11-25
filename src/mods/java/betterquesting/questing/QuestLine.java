package betterquesting.questing;

import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.properties.IPropertyType;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.SimpleDatabase;
import betterquesting.storage.PropertyContainer;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class QuestLine extends SimpleDatabase<IQuestLineEntry> implements IQuestLine {
   private PropertyContainer info = new PropertyContainer();

   public QuestLine() {
      this.setupProps();
   }

   private void setupProps() {
      this.setupValue(NativeProps.NAME, "New Quest Line");
      this.setupValue(NativeProps.DESC, "No Description");
      this.setupValue(NativeProps.ICON, new BigItemStack(Items.BOOK));
      this.setupValue(NativeProps.VISIBILITY, EnumQuestVisibility.NORMAL);
      this.setupValue(NativeProps.BG_IMAGE);
      this.setupValue(NativeProps.BG_SIZE);
   }

   private <T> void setupValue(IPropertyType<T> prop) {
      this.setupValue(prop, prop.getDefault());
   }

   private <T> void setupValue(IPropertyType<T> prop, T def) {
      this.info.setProperty(prop, this.info.getProperty(prop, def));
   }

   public IQuestLineEntry createNew(int id) {
      IQuestLineEntry qle = new QuestLineEntry(0, 0, 24, 24);
      this.add(id, qle);
      return qle;
   }

   public String getUnlocalisedName() {
      String def = "New Quest Line";
      if (!this.info.hasProperty(NativeProps.NAME)) {
         this.info.setProperty(NativeProps.NAME, def);
         return def;
      } else {
         return (String)this.info.getProperty(NativeProps.NAME, def);
      }
   }

   public String getUnlocalisedDescription() {
      String def = "No Description";
      if (!this.info.hasProperty(NativeProps.DESC)) {
         this.info.setProperty(NativeProps.DESC, def);
         return def;
      } else {
         return (String)this.info.getProperty(NativeProps.DESC, def);
      }
   }

   public DBEntry<IQuestLineEntry> getEntryAt(int x, int y) {
      for(DBEntry<IQuestLineEntry> entry : this.getEntries()) {
         int i1 = ((IQuestLineEntry)entry.getValue()).getPosX();
         int j1 = ((IQuestLineEntry)entry.getValue()).getPosY();
         int i2 = i1 + ((IQuestLineEntry)entry.getValue()).getSizeX();
         int j2 = j1 + ((IQuestLineEntry)entry.getValue()).getSizeY();
         if (x >= i1 && x < i2 && y >= j1 && y < j2) {
            return entry;
         }
      }

      return null;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound json, @Nullable List<Integer> subset) {
      json.setTag("properties", this.info.writeToNBT(new NBTTagCompound()));
      NBTTagList jArr = new NBTTagList();

      for(DBEntry<IQuestLineEntry> entry : this.getEntries()) {
         if (subset == null || subset.contains(Integer.valueOf(entry.getID()))) {
            NBTTagCompound qle = (NBTTagCompound)((IQuestLineEntry)entry.getValue()).writeToNBT(new NBTTagCompound());
            qle.setInteger("id", entry.getID());
            jArr.appendTag(qle);
         }
      }

      json.setTag("quests", jArr);
      return json;
   }

   public void readFromNBT(NBTTagCompound json, boolean merge) {
      this.info.readFromNBT(json.getCompoundTag("properties"));
      if (!merge) {
         this.reset();
      }

      NBTTagList qList = json.getTagList("quests", 10);

      for(int i = 0; i < qList.tagCount(); ++i) {
         NBTTagCompound qTag = qList.getCompoundTagAt(i);
         int id = qTag.hasKey("id", 99) ? qTag.getInteger("id") : -1;
         if (id >= 0) {
            this.add(id, new QuestLineEntry(qTag));
         }
      }

      this.setupProps();
   }

   public <T> T getProperty(IPropertyType<T> prop) {
      return (T)this.info.getProperty(prop);
   }

   public <T> T getProperty(IPropertyType<T> prop, T def) {
      return (T)this.info.getProperty(prop, def);
   }

   public boolean hasProperty(IPropertyType<?> prop) {
      return this.info.hasProperty(prop);
   }

   public <T> void setProperty(IPropertyType<T> prop, T value) {
      this.info.setProperty(prop, value);
   }

   public void removeProperty(IPropertyType<?> prop) {
      this.info.removeProperty(prop);
   }

   public void removeAllProps() {
      this.info.removeAllProps();
   }
}
