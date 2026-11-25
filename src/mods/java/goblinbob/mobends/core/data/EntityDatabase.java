package goblinbob.mobends.core.data;

import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.bender.PreviewHelper;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class EntityDatabase {
   public static EntityDatabase instance = new EntityDatabase();
   protected final Map<Integer, LivingEntityData<?>> entryMap = new HashMap();

   public EntityDatabase() {
   }

   private LivingEntityData<?> get(Integer identifier) {
      return (LivingEntityData)this.entryMap.get(identifier);
   }

   public <T extends LivingEntityData<E>, E extends EntityLivingBase> T get(E entity) {
      return (T)this.get(Integer.valueOf(entity.getEntityId()));
   }

   public <T extends LivingEntityData<E>, E extends EntityLivingBase> T getOrMake(IEntityDataFactory<E> dataCreationFunction, E entity) {
      int entityId = entity.getEntityId();
      T data = this.get(Integer.valueOf(entityId));
      if (data == null) {
         data = dataCreationFunction.createEntityData(entity);
         this.add(entityId, data);
      }

      return data;
   }

   private void add(int identifier, LivingEntityData<?> data) {
      this.entryMap.put(Integer.valueOf(identifier), data);
   }

   public void add(Entity entity, LivingEntityData<?> data) {
      this.add(entity.getEntityId(), data);
   }

   public void updateClient() {
      Iterator<Entry<Integer, LivingEntityData<?>>> it = this.entryMap.entrySet().iterator();

      while(it.hasNext()) {
         Entry<Integer, LivingEntityData<?>> entry = (Entry)it.next();
         LivingEntityData<?> entityData = (LivingEntityData)entry.getValue();
         EntityLivingBase entityInData = entityData.getEntity();
         Entity entity = Minecraft.getMinecraft().world.getEntityByID(((Integer)entry.getKey()).intValue());
         if (!PreviewHelper.isPreviewEntity(entityInData) && (entity == null || entityInData != entity)) {
            EntityBenderRegistry.instance.clearCache(entityInData);
            it.remove();
         } else {
            entityData.updateClient();
         }
      }

   }

   public void updateRender(float partialTicks) {
      for(EntityData<?> entityData : this.entryMap.values()) {
         entityData.update(partialTicks);
      }

   }

   public void refresh() {
      this.entryMap.clear();
   }

   public void onTicksRestart() {
      this.entryMap.values().forEach((data) -> {
         data.onTicksRestart();
      });
   }
}
