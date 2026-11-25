package com.creativemd.creativecore.common.gui.opener;

import com.creativemd.creativecore.common.gui.container.SubContainer;
import com.creativemd.creativecore.common.gui.container.SubGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CustomGuiHandler {
   public CustomGuiHandler() {
   }

   public abstract SubContainer getContainer(EntityPlayer var1, NBTTagCompound var2);

   @SideOnly(Side.CLIENT)
   public abstract SubGui getGui(EntityPlayer var1, NBTTagCompound var2);
}
