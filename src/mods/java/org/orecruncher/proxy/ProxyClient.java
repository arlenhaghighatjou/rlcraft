package org.orecruncher.proxy;

import java.util.Arrays;
import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.lib.ForgeUtils;
import org.orecruncher.lib.Localization;

@SideOnly(Side.CLIENT)
public class ProxyClient extends Proxy {
   public ProxyClient() {
   }

   public boolean isRunningAsServer() {
      return false;
   }

   public Side effectiveSide() {
      return FMLCommonHandler.instance().getEffectiveSide();
   }

   public void preInit(@Nonnull FMLPreInitializationEvent event) {
      Localization.initialize(Side.CLIENT, "orelib");
   }

   public void postInit(@Nonnull FMLPostInitializationEvent event) {
      ModMetadata data = ForgeUtils.getModMetadata("orelib");
      if (data != null) {
         data.name = Localization.loadString("orelib.metadata.Name");
         data.credits = Localization.loadString("orelib.metadata.Credits");
         data.description = Localization.loadString("orelib.metadata.Description");
         data.authorList = Arrays.asList(StringUtils.split(Localization.loadString("orelib.metadata.Authors"), ','));
      }

   }
}
