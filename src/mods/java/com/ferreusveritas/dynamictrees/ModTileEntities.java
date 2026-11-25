package com.ferreusveritas.dynamictrees;

import com.ferreusveritas.dynamictrees.tileentity.TileEntityBonsai;
import com.ferreusveritas.dynamictrees.tileentity.TileEntitySpecies;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities {
   public ModTileEntities() {
   }

   public static void preInit() {
      GameRegistry.registerTileEntity(TileEntitySpecies.class, new ResourceLocation("minecraft", "species_tile_entity"));
      GameRegistry.registerTileEntity(TileEntityBonsai.class, new ResourceLocation("minecraft", "bonsai_tile_entity"));
   }
}
