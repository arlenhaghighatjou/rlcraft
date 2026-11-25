package com.teamacronymcoders.base.registrysystem.pieces;

import com.teamacronymcoders.base.registrysystem.Registry;
import net.minecraft.util.ResourceLocation;

public interface IRegistryPiece<ENTRY> {
   boolean acceptsRegistry(Registry var1);

   boolean acceptsEntry(ResourceLocation var1, Object var2);

   void preInit(ResourceLocation var1, ENTRY var2);

   void init(ResourceLocation var1, ENTRY var2);

   void postInit(ResourceLocation var1, ENTRY var2);

   void addEntry(ResourceLocation var1, ENTRY var2);

   void onModelEvent(ResourceLocation var1, ENTRY var2);

   void onRegistryEvent(ResourceLocation var1, ENTRY var2);

   void afterRegistryEvent();
}
