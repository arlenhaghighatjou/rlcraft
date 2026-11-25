package dev.satyrn.wolfarmor.common;

import com.attributestudios.wolfarmor.api.IWolfArmorCapability;
import com.attributestudios.wolfarmor.common.capabilities.CapabilityWolfArmor;
import dev.satyrn.wolfarmor.WolfArmorMod;
import dev.satyrn.wolfarmor.advancements.WolfArmorTrigger;
import dev.satyrn.wolfarmor.api.common.IProxy;
import dev.satyrn.wolfarmor.api.compatibility.Compatibility;
import dev.satyrn.wolfarmor.api.util.Criteria;
import dev.satyrn.wolfarmor.api.util.Resources;
import dev.satyrn.wolfarmor.common.event.EntityEventHandler;
import dev.satyrn.wolfarmor.common.event.PlayerEventHandler;
import dev.satyrn.wolfarmor.common.event.PotionEventHandler;
import dev.satyrn.wolfarmor.common.event.RegistrationEventHandler;
import dev.satyrn.wolfarmor.common.loot.LootHandler;
import dev.satyrn.wolfarmor.common.network.WolfArmorGuiHandler;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy implements IProxy {
   public CommonProxy() {
   }

   public void preInit(@Nonnull FMLPreInitializationEvent preInitializationEvent) {
      this.registerEventListeners();
      this.registerCriteriaTriggers();
      Compatibility.preInit(preInitializationEvent);
   }

   public void registerEntityRenderingHandlers() {
   }

   public void registerItemRenders(@Nonnull FMLInitializationEvent initializationEvent) {
   }

   public void init(@Nonnull FMLInitializationEvent initializationEvent) {
      this.registerItemRenders(initializationEvent);
      this.registerItemColorHandlers(initializationEvent);
      this.registerLegacyCapabilityStubs();
      Compatibility.init(initializationEvent);
   }

   /** @deprecated */
   @Deprecated
   private void registerLegacyCapabilityStubs() {
      CapabilityManager.INSTANCE.register(IWolfArmorCapability.class, CapabilityWolfArmor.STORAGE, CapabilityWolfArmor::new);
      CapabilityManager.INSTANCE.register(dev.satyrn.wolfarmor.api.IWolfArmorCapability.class, dev.satyrn.wolfarmor.common.capabilities.CapabilityWolfArmor.STORAGE, dev.satyrn.wolfarmor.common.capabilities.CapabilityWolfArmor::new);
   }

   public void registerItemColorHandlers(@Nonnull FMLInitializationEvent initializationEvent) {
   }

   public void registerCriteriaTriggers() {
      Criteria.EQUIP_WOLF_ARMOR = new WolfArmorTrigger(Resources.CRITERIA_TRIGGER_EQUIP_WOLF_ARMOR);
      Criteria.EQUIP_WOLF_CHEST = new WolfArmorTrigger(Resources.CRITERIA_TRIGGER_EQUIP_WOLF_CHEST);
      CriteriaTriggers.register(Criteria.EQUIP_WOLF_ARMOR);
      CriteriaTriggers.register(Criteria.EQUIP_WOLF_CHEST);
   }

   public void registerLootTables() {
      LootHandler.init();
   }

   public void registerEventListeners() {
      MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
      MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
      MinecraftForge.EVENT_BUS.register(new RegistrationEventHandler());
      MinecraftForge.EVENT_BUS.register(new PotionEventHandler());
      MinecraftForge.EVENT_BUS.register(WolfArmorMod.getConfig());
   }

   public void postInit(@Nonnull FMLPostInitializationEvent postInitializationEvent) {
      this.registerGuiHandlers();
      this.registerLootTables();
      Compatibility.postInit(postInitializationEvent);
   }

   public void loadComplete(@Nonnull FMLLoadCompleteEvent loadCompleteEvent) {
      this.registerEntityRenderingHandlers();
      Compatibility.loadComplete(loadCompleteEvent);
   }

   public void registerGuiHandlers() {
      NetworkRegistry.INSTANCE.registerGuiHandler(WolfArmorMod.getInstance(), new WolfArmorGuiHandler());
   }

   public IThreadListener getThreadFromContext(MessageContext context) {
      return context.getServerHandler().player.getServer();
   }

   public EntityPlayer getPlayerFromContext(MessageContext context) {
      return context.getServerHandler().player;
   }

   public Side getCurrentSide() {
      return Side.SERVER;
   }
}
