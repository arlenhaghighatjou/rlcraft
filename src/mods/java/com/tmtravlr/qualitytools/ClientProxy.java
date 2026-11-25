package com.tmtravlr.qualitytools;

import com.tmtravlr.qualitytools.items.ItemEmeraldBauble;
import com.tmtravlr.qualitytools.reforging.BlockReforgingStation;
import com.tmtravlr.qualitytools.reforging.RendererReforgingStation;
import com.tmtravlr.qualitytools.reforging.TileEntityReforgingStation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerRenderers() {
      Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ItemEmeraldBauble.EMERALD_RING, 0, new ModelResourceLocation(ItemEmeraldBauble.EMERALD_RING.getRegistryName(), "inventory"));
      Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ItemEmeraldBauble.EMERALD_AMULET, 0, new ModelResourceLocation(ItemEmeraldBauble.EMERALD_AMULET.getRegistryName(), "inventory"));
      Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(BlockReforgingStation.ITEM_INSTANCE, 0, new ModelResourceLocation(BlockReforgingStation.ITEM_INSTANCE.getRegistryName(), "inventory"));
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReforgingStation.class, new RendererReforgingStation());
   }
}
