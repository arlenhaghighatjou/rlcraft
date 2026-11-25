package com.endertech.minecraft.forge.coremod.descriptors;

import com.endertech.minecraft.forge.coremod.names.ClassName;

public enum Classes {
   BOOLEAN("java.lang.Boolean"),
   BLOCK("net/minecraft/block/Block"),
   BLOCK_FIRE("net.minecraft.block.BlockFire"),
   BLOCK_LEAVES("net/minecraft/block/BlockLeaves"),
   BLOCK_LIQUID("net/minecraft/block/BlockLiquid"),
   BLOCK_POS("net/minecraft/util/math/BlockPos"),
   BLOCK_POS$MUTABLE("net/minecraft/util/math/BlockPos$MutableBlockPos"),
   IBLOCK_STATE("net/minecraft/block/state/IBlockState"),
   IINVENTORY("net.minecraft.inventory.IInventory"),
   CHUNK("net/minecraft/world/chunk/Chunk"),
   ENTITY("net/minecraft/entity/Entity"),
   ENTITY_LIVING_BASE("net.minecraft.entity.EntityLivingBase"),
   ENTITY_MINECART("net.minecraft.entity.item.EntityMinecart"),
   ENTITY_PLAYER("net/minecraft/entity/player/EntityPlayer"),
   ENTITY_RENDERER("net.minecraft.client.renderer.EntityRenderer"),
   ENUM_FACING("net.minecraft.util.EnumFacing"),
   ENUM_HAND("net/minecraft/util/EnumHand"),
   EXPLOSION("net.minecraft.world.Explosion"),
   FLUID_STACK("net/minecraftforge/fluids/FluidStack"),
   FLUID_TANK("net/minecraftforge/fluids/FluidTank"),
   IBAKED_MODEL("net/minecraft/client/renderer/block/model/IBakedModel"),
   ITEM("net/minecraft/item/Item"),
   ITEM_STACK("net/minecraft/item/ItemStack"),
   NON_NULL_LIST("net/minecraft/util/NonNullList"),
   OBJECT("java/lang/Object"),
   PREDICATE("java.util.function.Predicate"),
   RANDOM("java/util/Random"),
   RENDER_ITEM("net/minecraft/client/renderer/RenderItem"),
   RESOURCE_LOCATION("net.minecraft.util.ResourceLocation"),
   TEXTURE_MANAGER("net.minecraft.client.renderer.texture.TextureManager"),
   TEXTURE_ATLAS_SPRITE("net.minecraft.client.renderer.texture.TextureAtlasSprite"),
   TILE_ENTITY("net/minecraft/tileentity/TileEntity"),
   TILE_ENTITY_FURNACE("net/minecraft/tileentity/TileEntityFurnace"),
   VEC3D("net/minecraft/util/math/Vec3d"),
   WORLD("net/minecraft/world/World"),
   WORLD_CLIENT("net/minecraft/client/multiplayer/WorldClient"),
   WORLD_PROVIDER("net/minecraft/world/WorldProvider"),
   WORLD_SERVER("net/minecraft/world/WorldServer");

   public final ClassName name;
   public final ClassDescriptor descriptor;

   private Classes(String normal) {
      this.name = ClassName.from(normal);
      this.descriptor = new ClassDescriptor(this.name);
   }
}
