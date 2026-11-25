package ivorius.reccomplex.nbt;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import ivorius.ivtoolkit.lang.IvClasses;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;

public class NBTStringTypeRegistry<B extends NBTCompoundObject> {
   private BiMap<Class<? extends B>, String> classMap;
   private String typeKey;
   private String objectKey;

   public NBTStringTypeRegistry(String objectKey, String typeKey) {
      this.classMap = HashBiMap.create();
      if (objectKey.equals(typeKey)) {
         throw new IllegalArgumentException("Type key must be different from object key");
      } else {
         this.objectKey = objectKey;
         this.typeKey = typeKey;
      }
   }

   public NBTStringTypeRegistry() {
      this("object", "type");
   }

   public String getTypeKey() {
      return this.typeKey;
   }

   public String getObjectKey() {
      return this.objectKey;
   }

   public <T extends B> void register(String id, Class<? extends T> clazz) {
      this.classMap.put(clazz, id);
   }

   public String type(Class<? extends B> aClass) {
      return (String)this.classMap.get(aClass);
   }

   public Class<? extends B> objectClass(String type) {
      return (Class)this.classMap.inverse().get(type);
   }

   public Collection<String> allIDs() {
      return this.classMap.inverse().keySet();
   }

   public B copy(B b) {
      return (B)this.read(this.write(b));
   }

   public NBTTagCompound write(B t) {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString(this.getTypeKey(), this.type(t.getClass()));
      compound.setTag(this.getObjectKey(), NBTCompoundObjects.write(t));
      return compound;
   }

   @Nullable
   public B read(NBTTagCompound compound) {
      Class<? extends B> type = this.objectClass(compound.getString(this.getTypeKey()));
      return (B)(type != null ? NBTCompoundObjects.read(compound.getCompoundTag(this.getObjectKey()), () -> {
         return (B)(IvClasses.instantiate(type));
      }) : null);
   }
}
