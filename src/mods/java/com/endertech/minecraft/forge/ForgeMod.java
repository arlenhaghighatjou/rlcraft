package com.endertech.minecraft.forge;

import com.endertech.minecraft.forge.configs.ModConfig;
import com.endertech.minecraft.forge.messages.Connection;
import com.endertech.minecraft.forge.regs.ForgeRegistrator;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public abstract class ForgeMod {
   private ModConfig config;
   private final Logger logger = LogManager.getLogger(this.getId());
   private final ForgeRegistrator registrator = new ForgeRegistrator(this);

   public abstract String getId();

   public abstract String getName();

   protected ForgeMod() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      this.getRegistrator().onPreInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      this.getRegistrator().onInit(event);
   }

   @EventHandler
   @SideOnly(Side.CLIENT)
   public void initClient(FMLInitializationEvent event) {
   }

   @EventHandler
   public void interModCom(IMCEvent event) {
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      this.getRegistrator().onPostInit(event);
      this.getConfig().save();
   }

   public void printInitMsg(FMLEvent event) {
      if (event != null) {
         this.getLogger().debug("{} of '{}'", event.description(), this.getName());
      }

   }

   public ForgeRegistrator getRegistrator() {
      return this.registrator;
   }

   public ModConfig getConfig() {
      return this.config;
   }

   protected void setConfig(ModConfig config) {
      this.config = config;
   }

   public Logger getLogger() {
      return this.logger;
   }

   public abstract ForgeMod getInstance();

   public Connection getConnection() {
      return this.getRegistrator().getConnection();
   }
}
