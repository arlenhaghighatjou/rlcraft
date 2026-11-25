package com.teamacronymcoders.base.registrysystem.pieces;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.IModAware;
import com.teamacronymcoders.base.registrysystem.Registry;
import net.minecraft.util.ResourceLocation;

public class RegistryPieceBase<ENTRY> implements IRegistryPiece<ENTRY>, IModAware {
   private IBaseMod mod;
   private Class<ENTRY> entryClass;

   public RegistryPieceBase() {
      this((Class)null);
   }

   public RegistryPieceBase(Class<ENTRY> entryClass) {
      this.entryClass = entryClass;
   }

   public boolean acceptsRegistry(Registry registry) {
      return true;
   }

   public boolean acceptsEntry(ResourceLocation name, Object entry) {
      return this.entryClass == null || this.entryClass.isInstance(entry);
   }

   public void preInit(ResourceLocation name, ENTRY entry) {
   }

   public void init(ResourceLocation name, ENTRY entry) {
   }

   public void postInit(ResourceLocation name, ENTRY entry) {
   }

   public void addEntry(ResourceLocation name, ENTRY entry) {
   }

   public void onModelEvent(ResourceLocation name, ENTRY entry) {
   }

   public void onRegistryEvent(ResourceLocation name, ENTRY entry) {
   }

   public void afterRegistryEvent() {
   }

   public IBaseMod getMod() {
      return this.mod;
   }

   public void setMod(IBaseMod mod) {
      this.mod = mod;
   }
}
