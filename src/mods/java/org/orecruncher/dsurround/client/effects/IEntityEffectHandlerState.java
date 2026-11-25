package org.orecruncher.dsurround.client.effects;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IEntityEffectHandlerState extends IEntityEffectState {
   boolean isAlive();
}
