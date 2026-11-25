package crafttweaker.api.entity;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenOperator;

@ZenClass("crafttweaker.entity.IEntityEquipmentSlot")
@ZenRegister
public interface IEntityEquipmentSlot {
   @ZenGetter("index")
   int getIndex();

   @ZenGetter("slotIndex")
   int getSlotIndex();

   @ZenGetter("name")
   String getName();

   @ZenOperator(OperatorType.COMPARE)
   int compareTo(IEntityEquipmentSlot var1);

   Object getInternal();
}
