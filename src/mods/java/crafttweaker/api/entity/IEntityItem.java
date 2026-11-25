package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenClass("crafttweaker.entity.IEntityItem")
@ZenRegister
public interface IEntityItem extends IEntity {
   @ZenGetter("item")
   IItemStack getItem();
}
