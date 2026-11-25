package moe.plushie.armourers_workshop.api.common.capability;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import net.minecraft.entity.player.EntityPlayerMP;

public interface IWardrobeCap {
   IExtraColours getExtraColours();

   ISkinDye getDye();

   ISkinnableEntity getSkinnableEntity();

   void syncToPlayer(EntityPlayerMP var1);

   void syncToAllTracking();

   void sendUpdateToServer();
}
