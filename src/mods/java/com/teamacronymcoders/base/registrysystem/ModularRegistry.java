package com.teamacronymcoders.base.registrysystem;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.registrysystem.pieces.IRegistryPiece;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPieceComparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;

public class ModularRegistry<ENTRY> extends Registry<ENTRY> {
   private List<IRegistryPiece> registryPieces;

   public ModularRegistry(String name, IBaseMod mod, List<IRegistryPiece> registryPieces) {
      super(name, mod);
      this.registryPieces = (List)registryPieces.stream().filter((registryPiece) -> {
         return (void)registryPiece.acceptsRegistry(this);
      }).collect(Collectors.toList());
      this.registryPieces.sort(new RegistryPieceComparator());
   }

   public void preInit() {
      super.preInit();
      this.entries.forEach((entryName, entryValue) -> {
         this.registryPieces.stream().filter((registryPiece) -> {
            return registryPiece.acceptsEntry(entryName, entryValue);
         }).forEach((registryPiece) -> {
            registryPiece.preInit(entryName, entryValue);
         });
      });
   }

   public void registryEvent() {
      super.registryEvent();
      this.entries.forEach((entryName, entryValue) -> {
         this.registryPieces.stream().filter((registryPiece) -> {
            return registryPiece.acceptsEntry(entryName, entryValue);
         }).forEach((registryPiece) -> {
            registryPiece.onRegistryEvent(entryName, entryValue);
         });
      });
      this.registryPieces.forEach(IRegistryPiece::afterRegistryEvent);
   }

   public void init() {
      super.init();
      this.entries.forEach((entryName, entryValue) -> {
         this.registryPieces.stream().filter((registryPiece) -> {
            return registryPiece.acceptsEntry(entryName, entryValue);
         }).forEach((registryPiece) -> {
            registryPiece.init(entryName, entryValue);
         });
      });
   }

   public void onModelRun() {
      super.onModelRun();
      this.entries.forEach((entryName, entryValue) -> {
         this.registryPieces.stream().filter((registryPiece) -> {
            return registryPiece.acceptsEntry(entryName, entryValue);
         }).forEach((registryPiece) -> {
            registryPiece.onModelEvent(entryName, entryValue);
         });
      });
   }

   public void postInit() {
      super.postInit();
      this.entries.forEach((entryName, entryValue) -> {
         this.registryPieces.stream().filter((registryPiece) -> {
            return registryPiece.acceptsEntry(entryName, entryValue);
         }).forEach((registryPiece) -> {
            registryPiece.postInit(entryName, entryValue);
         });
      });
      this.registryPieces.clear();
   }

   public void register(ResourceLocation name, ENTRY entry) {
      super.register(name, entry);
      this.registryPieces.stream().filter((registryPiece) -> {
         return (void)registryPiece.acceptsEntry(name, entry);
      }).forEach((registryPiece) -> {
         registryPiece.addEntry(name, entry);
      });
   }
}
