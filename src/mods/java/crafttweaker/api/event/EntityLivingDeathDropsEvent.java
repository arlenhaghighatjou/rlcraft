package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.damage.IDamageSource;
import crafttweaker.api.entity.IEntityItem;
import crafttweaker.api.item.IItemStack;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenRegister
@ZenClass("crafttweaker.event.EntityLivingDeathDropsEvent")
public interface EntityLivingDeathDropsEvent extends ILivingEvent, IEventCancelable {
   @ZenGetter("damageSource")
   IDamageSource getDamageSource();

   @ZenMethod
   void addItem(IItemStack var1);

   @ZenMethod
   void addItem(IEntityItem var1);

   @ZenGetter("lootingLevel")
   int getLootingLevel();

   @ZenGetter("isRecentlyHit")
   boolean getIsRecentlyHit();

   @ZenGetter("drops")
   List<IEntityItem> getDrops();

   @ZenSetter("drops")
   void setDrops(List<IEntityItem> var1);
}
