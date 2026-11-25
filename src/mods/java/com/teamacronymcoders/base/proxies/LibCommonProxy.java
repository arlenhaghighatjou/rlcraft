package com.teamacronymcoders.base.proxies;

import com.google.common.base.Strings;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.modulesystem.IModule;
import com.teamacronymcoders.base.modulesystem.ModuleHandler;
import com.teamacronymcoders.base.modulesystem.proxies.IModuleProxy;
import com.teamacronymcoders.base.registrysystem.pieces.RegistrySide;
import com.teamacronymcoders.base.util.ClassLoading;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class LibCommonProxy {
   private IBaseMod mod;

   public LibCommonProxy() {
   }

   public void addOBJDomain() {
   }

   public void setAllItemModels(IHasModel model) {
   }

   public void addSidedBlockDomain() {
   }

   public void registerFluidModel(Block fluidBlock, ResourceLocation loc) {
   }

   public abstract IModuleProxy getModuleProxy(IModule var1);

   @Nullable
   protected IModuleProxy getModuleProxy(String path) {
      IModuleProxy moduleProxy = null;
      if (!Strings.isNullOrEmpty(path.trim())) {
         moduleProxy = (IModuleProxy)ClassLoading.createInstanceOf(IModuleProxy.class, path);
      }

      return moduleProxy;
   }

   public RegistrySide getRegistrySide() {
      return RegistrySide.BOTH;
   }

   public boolean isRightSide(RegistrySide side) {
      return side == RegistrySide.BOTH || side == this.getRegistrySide();
   }

   public IBaseMod getMod() {
      return this.mod;
   }

   public void setMod(IBaseMod mod) {
      this.mod = mod;
   }

   public String getFileContents(ResourceLocation resourceLocation) {
      return "";
   }

   public void createResourceLoader(String modid) {
   }

   public void handleSounds() {
   }

   public void loadEntityRenderers(ASMDataTable table, ModuleHandler moduleHandler) {
   }

   public abstract World getWorld(MessageContext var1);
}
