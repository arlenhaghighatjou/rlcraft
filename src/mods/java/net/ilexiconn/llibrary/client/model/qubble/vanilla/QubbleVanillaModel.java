package net.ilexiconn.llibrary.client.model.qubble.vanilla;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;

public class QubbleVanillaModel implements INBTSerializable<NBTTagCompound> {
   private String name;
   private String author;
   private int version;
   private boolean ambientOcclusion = true;
   private Map<String, QubbleVanillaTexture> textures = new LinkedHashMap();
   private Set<QubbleVanillaCuboid> cuboids = new LinkedHashSet();
   private transient String fileName;

   private QubbleVanillaModel() {
   }

   public static QubbleVanillaModel create(String name, String author) {
      QubbleVanillaModel model = new QubbleVanillaModel();
      model.setName(name == null ? "Unknown" : name);
      model.setAuthor(author == null ? "Unknown" : author);
      model.setVersion(LLibrary.QUBBLE_VANILLA_VERSION);
      return model;
   }

   public static QubbleVanillaModel deserialize(NBTTagCompound compound) {
      QubbleVanillaModel model = new QubbleVanillaModel();
      model.deserializeNBT(compound);
      return model;
   }

   public NBTTagCompound serializeNBT() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString("name", this.name);
      compound.setString("author", this.author);
      compound.setByte("version", (byte)(this.version & 255));
      compound.setBoolean("ambientOcclusion", this.ambientOcclusion);
      NBTTagList textures = new NBTTagList();

      for(Entry<String, QubbleVanillaTexture> entry : this.textures.entrySet()) {
         NBTTagCompound texture = new NBTTagCompound();
         texture.setString("key", (String)entry.getKey());
         texture.setTag("value", ((QubbleVanillaTexture)entry.getValue()).serializeNBT());
         textures.appendTag(texture);
      }

      compound.setTag("textures", textures);
      NBTTagList cuboids = new NBTTagList();

      for(QubbleVanillaCuboid cuboid : this.cuboids) {
         cuboids.appendTag(cuboid.serializeNBT());
      }

      compound.setTag("cuboids", cuboids);
      return compound;
   }

   public void deserializeNBT(NBTTagCompound compound) {
      this.name = compound.getString("name");
      this.author = compound.getString("author");
      this.version = compound.getByte("version") & 255;
      if (compound.hasKey("ambientOcclusion")) {
         this.ambientOcclusion = compound.getBoolean("ambientOcclusion");
      }

      NBTTagList textures = compound.getTagList("textures", 10);

      for(int i = 0; i < textures.tagCount(); ++i) {
         NBTTagCompound texture = textures.getCompoundTagAt(i);
         if (texture.hasKey("key") && texture.hasKey("value")) {
            this.addTexture(texture.getString("key"), QubbleVanillaTexture.deserialize(texture.getCompoundTag("value")));
         }
      }

      NBTTagList cuboids = compound.getTagList("cuboids", 10);

      for(int i = 0; i < cuboids.tagCount(); ++i) {
         this.addCuboid(QubbleVanillaCuboid.deserialize(cuboids.getCompoundTagAt(i)));
      }

   }

   public QubbleVanillaModel copy() {
      QubbleVanillaModel model = create(this.name, this.author);
      model.setFileName(this.fileName);
      model.setAmbientOcclusion(this.ambientOcclusion);
      model.setVersion(this.version);

      for(QubbleVanillaCuboid cuboid : this.cuboids) {
         model.addCuboid(cuboid.copy());
      }

      for(Entry<String, QubbleVanillaTexture> texture : this.textures.entrySet()) {
         model.addTexture((String)texture.getKey(), ((QubbleVanillaTexture)texture.getValue()).copy());
      }

      return model;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setFileName(String fileName) {
      this.fileName = fileName;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public void setVersion(int version) {
      this.version = version;
   }

   public void setAmbientOcclusion(boolean ambientOcclusion) {
      this.ambientOcclusion = ambientOcclusion;
   }

   public void addCuboid(QubbleVanillaCuboid cuboid) {
      if (this.getCuboid(cuboid.getName()) == null) {
         this.cuboids.add(cuboid);
      }

   }

   public QubbleVanillaCuboid removeCuboid(String name) {
      QubbleVanillaCuboid cuboid = this.getCuboid(name);
      if (cuboid != null) {
         this.cuboids.remove(cuboid);
      }

      return cuboid;
   }

   public QubbleVanillaCuboid getCuboid(String name) {
      for(QubbleVanillaCuboid cuboid : this.cuboids) {
         if (cuboid.getName().equals(name)) {
            return cuboid;
         }
      }

      return null;
   }

   public void addTexture(String key, QubbleVanillaTexture texture) {
      this.textures.put(key, texture);
   }

   public void removeTexture(String key) {
      this.textures.remove(key);
   }

   public String getName() {
      return this.name;
   }

   public String getFileName() {
      return this.fileName;
   }

   public String getAuthor() {
      return this.author;
   }

   public boolean hasAmbientOcclusion() {
      return this.ambientOcclusion;
   }

   public Map<String, QubbleVanillaTexture> getTextures() {
      return this.textures;
   }

   public Set<QubbleVanillaCuboid> getCuboids() {
      return this.cuboids;
   }
}
