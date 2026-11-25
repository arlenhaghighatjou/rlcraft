package com.creativemd.creativecore.common.config;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.config.gui.SubGuiConfig;
import com.creativemd.creativecore.common.config.holder.CreativeConfigRegistry;
import com.creativemd.creativecore.common.config.holder.ICreativeConfigHolder;
import com.creativemd.creativecore.common.gui.mc.GuiScreenSub;
import java.util.Collections;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory.RuntimeOptionCategoryElement;
import net.minecraftforge.fml.relauncher.Side;

public abstract class ConfigModGuiFactory implements IModGuiFactory {
   public ConfigModGuiFactory() {
   }

   public abstract String modid();

   public void initialize(Minecraft minecraftInstance) {
   }

   public ICreativeConfigHolder getHolder() {
      ICreativeConfigHolder holder = (ICreativeConfigHolder)CreativeConfigRegistry.ROOT.get(this.modid());
      return holder != null && !holder.isEmpty(Side.CLIENT) ? holder : null;
   }

   public boolean hasConfigGui() {
      return this.getHolder() != null;
   }

   public GuiScreen createConfigGui(GuiScreen parentScreen) {
      ICreativeConfigHolder holder = this.getHolder();
      if (holder != null) {
         CreativeCore.configHandler.load(this.modid(), Side.CLIENT);
         if (Minecraft.getMinecraft().player == null) {
            CreativeCore.configHandler.loadClientFields();
         }

         return new GuiScreenSub(parentScreen, new SubGuiConfig(holder, Side.CLIENT));
      } else {
         return null;
      }
   }

   public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
      return Collections.EMPTY_SET;
   }
}
