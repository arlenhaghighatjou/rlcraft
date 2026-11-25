package net.ilexiconn.llibrary.client.model.qubble.vanilla;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

public class QubbleVanillaTexture implements INBTSerializable<NBTTagCompound> {
   private String texture;
   private Map<String, String> properties = new HashMap();

   private QubbleVanillaTexture() {
   }

   public static QubbleVanillaTexture create(String value) {
      QubbleVanillaTexture texture = new QubbleVanillaTexture();
      texture.texture = value;
      return texture;
   }

   public static QubbleVanillaTexture deserialize(NBTTagCompound compound) {
      QubbleVanillaTexture texture = new QubbleVanillaTexture();
      texture.deserializeNBT(compound);
      return texture;
   }

   public String getTexture() {
      return this.texture;
   }

   public Map<String, String> getProperties() {
      return this.properties;
   }

   public String getProperty(String key) {
      return (String)this.properties.get(key);
   }

   public boolean getBoolean(String key) {
      String property = this.getProperty(key);
      return property != null && property.equals("true");
   }

   public void setProperty(String key, String value) {
      this.properties.put(key, value);
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("texture", this.texture);
      NBTTagList propertyList = new NBTTagList();

      for(Entry<String, String> entry : this.properties.entrySet()) {
         NBTTagCompound property = new NBTTagCompound();
         property.setString("key", (String)entry.getKey());
         property.setString("value", (String)entry.getValue());
         propertyList.appendTag(property);
      }

      compound.setTag("properties", propertyList);
      return compound;
   }

   public void deserializeNBT(NBTTagCompound compound) {
      this.texture = compound.getString("texture");
      NBTTagList propertyList = compound.getTagList("properties", 10);

      for(int i = 0; i < propertyList.tagCount(); ++i) {
         NBTTagCompound property = propertyList.getCompoundTagAt(i);
         if (property.hasKey("key") && property.hasKey("value")) {
            this.properties.put(property.getString("key"), property.getString("value"));
         }
      }

   }

   public QubbleVanillaTexture copy() {
      QubbleVanillaTexture texture = create(this.texture);

      for(Entry<String, String> entry : this.properties.entrySet()) {
         texture.setProperty((String)entry.getKey(), (String)entry.getValue());
      }

      return texture;
   }
}
