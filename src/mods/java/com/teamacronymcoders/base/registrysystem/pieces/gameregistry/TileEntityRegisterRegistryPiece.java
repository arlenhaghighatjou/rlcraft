package com.teamacronymcoders.base.registrysystem.pieces.gameregistry;

import com.google.common.collect.Maps;
import com.teamacronymcoders.base.blocks.IHasTileEntity;
import com.teamacronymcoders.base.registrysystem.Registry;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPiece;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPieceBase;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

@RegistryPiece
public class TileEntityRegisterRegistryPiece extends RegistryPieceBase<IHasTileEntity> {
   private Map<String, Class<? extends TileEntity>> tileEntitiesToRegister = Maps.newHashMap();

   public TileEntityRegisterRegistryPiece() {
      super(IHasTileEntity.class);
   }

   public boolean acceptsRegistry(Registry registry) {
      return "BLOCK".equalsIgnoreCase(registry.getName());
   }

   public void onRegistryEvent(ResourceLocation name, IHasTileEntity entry) {
      this.tileEntitiesToRegister.put(entry.getTileName(name), entry.getTileEntityClass());
   }

   public void afterRegistryEvent() {
      this.tileEntitiesToRegister.forEach((key, value) -> {
         GameRegistry.registerTileEntity(value, key);
      });
      this.tileEntitiesToRegister.clear();
   }
}
