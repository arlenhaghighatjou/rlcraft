package com.Shultrea.Rin.Main_Sector;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import com.Shultrea.Rin.Enchantments_Sector.OrderedRegistry;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import com.Shultrea.Rin.Enchantments_Sector.Smc_020;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Prop_Sector.ArrowProperties;
import com.Shultrea.Rin.Prop_Sector.ArrowPropertiesStorage;
import com.Shultrea.Rin.Prop_Sector.IArrowProperties;
import com.Shultrea.Rin.Prop_Sector.IPlayerProperties;
import com.Shultrea.Rin.Prop_Sector.PlayerProperties;
import com.Shultrea.Rin.Prop_Sector.PlayerPropertiesStorage;
import com.Shultrea.Rin.Utility_Sector.CommProxy;
import com.Shultrea.Rin.Utility_Sector.EnchantmentLister;
import com.Shultrea.Rin.Utility_Sector.PotionLister;
import com.Shultrea.Rin.Utility_Sector.SMEsounds;
import java.util.concurrent.Callable;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "somanyenchantments",
   name = "Rin's So Many Enchantments?",
   version = "0.5.5",
   acceptedMinecraftVersions = "[1.12.0, 1.12.2]"
)
public class somanyenchantments {
   public static final String MODID = "somanyenchantments";
   public static final String NAME = "Rin's So Many Enchantments?";
   public static final String VERSION = "0.5.5";
   public static boolean hotbarOnly = true;
   public static somanyenchantments instance;
   public static CommProxy proxy;
   public static DamageSource Pure = (new DamageSource("Pure")).setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
   public static DamageSource Ethereal = (new DamageSource("Ethereal")).setDamageBypassesArmor();
   public static DamageSource Cleave = new DamageSource("Cleave");
   public static DamageSource Deconstruct = (new DamageSource("Deconstruct")).setDamageIsAbsolute().setDamageBypassesArmor();
   public static DamageSource Culled = (new DamageSource("Culled")).setDamageIsAbsolute().setDamageBypassesArmor();
   public static DamageSource CounterAttack = new DamageSource("CounterAttack");
   public static DamageSource Silence = new DamageSource("Silence");
   public static DamageSource PhysicalDamage = new DamageSource("PhysicalDamage");

   public somanyenchantments() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new com.Shultrea.Rin.Utility_Sector.ClientProxy() : new com.Shultrea.Rin.Utility_Sector.CommProxy();
         com.Shultrea.Rin.Main_Sector.ModConfig.register();
         com.Shultrea.Rin.Prop_Sector.ArrowPropertiesProvider.registerCapabilities();
         com.Shultrea.Rin.Prop_Sector.PlayerPropertiesProvider.registerCapabilities();
         com.Shultrea.Rin.Enchantments_Sector.OrderedRegistry.EventSubscriber.register(MinecraftForge.EVENT_BUS);
         com.Shultrea.Rin.Main_Sector.ModConfig.EventHandler.register(MinecraftForge.EVENT_BUS);
         com.Shultrea.Rin.Utility_Sector.SMEsounds.EventSubscriber.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.load((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent fEvent) {
      CapabilityManager.INSTANCE.register(IArrowProperties.class, new ArrowPropertiesStorage(), ArrowProperties::new);
      CapabilityManager.INSTANCE.register(IPlayerProperties.class, new PlayerPropertiesStorage(), PlayerProperties::new);
      proxy.preInit(fEvent);
      SMEsounds.registerSounds();
      Smc_010.init();
      Smc_020.init();
      Smc_030.init();
      Smc_040.init();
   }

   public void load(FMLInitializationEvent fEvent) {
      proxy.onInit(fEvent);
   }

   public void postInit(FMLPostInitializationEvent fEvent) {
      proxy.postInit(fEvent);
      OrderedRegistry.orderedRegister();
      PotionLister.Cycle();
      EnumList.initializeEnchantmentTab();
      EnchantmentLister.initEnchantmentList();
   }
}
