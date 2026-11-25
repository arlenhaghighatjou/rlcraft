package dev.satyrn.wolfarmor.api.common;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public interface IProxy {
   void preInit(@Nonnull FMLPreInitializationEvent var1);

   void init(@Nonnull FMLInitializationEvent var1);

   void postInit(@Nonnull FMLPostInitializationEvent var1);

   void loadComplete(@Nonnull FMLLoadCompleteEvent var1);

   IThreadListener getThreadFromContext(MessageContext var1);

   EntityPlayer getPlayerFromContext(MessageContext var1);

   Side getCurrentSide();
}
