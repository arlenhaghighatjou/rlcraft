package moe.plushie.armourers_workshop.api.client.render.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISkinnableEntityRenderer<ENTITY extends EntityLivingBase> {
}
