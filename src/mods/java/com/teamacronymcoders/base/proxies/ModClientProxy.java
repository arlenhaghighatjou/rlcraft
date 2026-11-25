package com.teamacronymcoders.base.proxies;

import com.teamacronymcoders.base.client.ClientHelper;
import java.util.Objects;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModClientProxy extends ModCommonProxy {
   public ModClientProxy() {
   }

   public World getClientWorld() {
      return ClientHelper.world();
   }

   public boolean isClient() {
      return true;
   }

   public boolean isServer() {
      return false;
   }

   public EntityPlayer getClientPlayer() {
      return ClientHelper.player();
   }

   public String translateMessage(String label, String message) {
      return Objects.equals(label, "") ? I18n.format(message, new Object[0]) : I18n.format(String.format("%s.%s.%s", label, "base", message), new Object[0]);
   }
}
