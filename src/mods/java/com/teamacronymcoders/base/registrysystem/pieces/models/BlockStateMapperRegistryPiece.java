package com.teamacronymcoders.base.registrysystem.pieces.models;

import com.teamacronymcoders.base.blocks.IHasBlockStateMapper;
import com.teamacronymcoders.base.client.BlockStateMappers;
import com.teamacronymcoders.base.registrysystem.Registry;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPiece;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPieceBase;
import com.teamacronymcoders.base.registrysystem.pieces.RegistrySide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@RegistryPiece(
   value = RegistrySide.CLIENT,
   priority = EventPriority.LOW
)
@SideOnly(Side.CLIENT)
public class BlockStateMapperRegistryPiece extends RegistryPieceBase<IHasBlockStateMapper> {
   public BlockStateMapperRegistryPiece() {
      super(IHasBlockStateMapper.class);
   }

   public boolean acceptsRegistry(Registry registry) {
      return "BLOCK".equalsIgnoreCase(registry.getName());
   }

   public void onModelEvent(ResourceLocation name, IHasBlockStateMapper entry) {
      BlockStateMappers.registerStateMapper(entry);
   }
}
