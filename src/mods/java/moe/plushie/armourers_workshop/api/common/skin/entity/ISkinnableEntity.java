package moe.plushie.armourers_workshop.api.common.skin.entity;

import java.util.ArrayList;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ISkinnableEntity {
   Class<? extends EntityLivingBase> getEntityClass();

   @SideOnly(Side.CLIENT)
   void addRenderLayer(RenderManager var1);

   boolean canUseWandOfStyle(EntityPlayer var1);

   boolean canUseSkinsOnEntity();

   void getValidSkinTypes(ArrayList<ISkinType> var1);

   int getSlotsForSkinType(ISkinType var1);
}
