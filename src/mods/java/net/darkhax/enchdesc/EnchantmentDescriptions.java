package net.darkhax.enchdesc;

import net.darkhax.enchdesc.client.TooltipHandler;
import net.darkhax.enchdesc.handler.ConfigurationHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "enchdesc",
   name = "Enchantment Descriptions",
   version = "1.1.20",
   clientSideOnly = true,
   certificateFingerprint = "d476d1b22b218a10d845928d1665d45fce301b27",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class EnchantmentDescriptions {
   public static final Logger LOG = LogManager.getLogger("Enchantment Descriptions");

   public EnchantmentDescriptions() {
   }

   @EventHandler
   @SideOnly(Side.CLIENT)
   public void preInit(FMLPreInitializationEvent event) {
      ConfigurationHandler.initConfig(event.getSuggestedConfigurationFile());
      MinecraftForge.EVENT_BUS.register(new TooltipHandler());
   }

   @EventHandler
   @SideOnly(Side.CLIENT)
   public void postInit(FMLPostInitializationEvent event) {
      if (ConfigurationHandler.isExploreMode()) {
         for(Enchantment enchant : Enchantment.REGISTRY) {
            if (I18n.format(TooltipHandler.getTranslationKey(enchant), new Object[0]).startsWith("enchantment.")) {
               LOG.info(String.format("Undefined enchantment from %s %s", TooltipHandler.getModName(enchant), TooltipHandler.getTranslationKey(enchant)));
            }
         }
      }

   }
}
