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

@ZenClass("crafttweaker.event.PlayerDeathDropsEvent")
@ZenRegister
public interface PlayerDeathDropsEvent extends IPlayerEvent {
   @ZenGetter("items")
   List<IEntityItem> getItems();

   @ZenSetter("items")
   void setItems(List<IEntityItem> var1);

   @ZenMethod
   void addItem(IItemStack var1);

   @ZenMethod
   void addItem(IEntityItem var1);

   @ZenGetter("damageSource")
   IDamageSource getDamageSource();
}
