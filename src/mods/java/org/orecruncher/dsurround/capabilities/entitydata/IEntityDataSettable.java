package org.orecruncher.dsurround.capabilities.entitydata;

import net.minecraft.entity.EntityLiving;

public interface IEntityDataSettable extends IEntityData {
   EntityLiving getEntity();

   void setAttacking(boolean var1);

   void setFleeing(boolean var1);

   void sync();
}
