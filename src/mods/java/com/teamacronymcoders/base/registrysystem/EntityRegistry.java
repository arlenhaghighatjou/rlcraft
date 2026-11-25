package com.teamacronymcoders.base.registrysystem;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.registrysystem.entity.ExtendedEntityEntry;
import com.teamacronymcoders.base.registrysystem.pieces.IRegistryPiece;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EntityRegistry extends ModularRegistry<ExtendedEntityEntry> {
   public EntityRegistry(IBaseMod mod, List<IRegistryPiece> registryPieces) {
      super("ENTITY", mod, registryPieces);
   }

   public void register(Class<? extends Entity> entityClass) {
      ResourceLocation name = new ResourceLocation(this.mod.getID(), entityClass.getSimpleName());
      ExtendedEntityEntry extendedEntityEntry = new ExtendedEntityEntry(entityClass, name.toString());
      this.register(name, extendedEntityEntry);
   }
}
