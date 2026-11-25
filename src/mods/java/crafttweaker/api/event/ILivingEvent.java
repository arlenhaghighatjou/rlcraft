package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityLivingBase;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("crafttweaker.event.ILivingEvent")
@ZenRegister
public interface ILivingEvent extends IEntityEvent {
   @ZenGetter("entityLivingBase")
   IEntityLivingBase getEntityLivingBase();

   default IEntity getEntity() {
      return this.getEntityLivingBase();
   }
}
