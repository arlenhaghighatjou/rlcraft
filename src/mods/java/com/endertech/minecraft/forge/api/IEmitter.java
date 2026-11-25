package com.endertech.minecraft.forge.api;

import com.endertech.common.KeyValuePair;
import com.endertech.minecraft.forge.blocks.BlockState;
import com.endertech.minecraft.forge.configs.ForgeNBT;
import com.endertech.minecraft.forge.regs.IPostInit;
import com.endertech.minecraft.forge.units.IRelatedUnit;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public interface IEmitter extends IRelatedUnit, IPostInit {
   String COMMENT_ACTIVE_STATE_TAG = "Defines the name of the NBT tag that stores info about tile entity active state.\nEmitter is considered active if the return value is greater than 0 or true.\nSet to an empty string if you don't need this function.";
   String COMMENT_IDENTITY_TAG = "This parameter is required only for those blocks that use NBT tags to fully identify themselves.\nThe format is <tagKey>=<value>\nIf the value is a string, it must be enclosed in double quotes. Example: id=\"ic2:generator\"\nLeave this blank if you don't need it.";
   String COMMENT_IS_ENTITY = "Set this to True only if this emitter is not a block or a tile but an entity.";
   String COMMENT_USE_DROPPED_ITEM_META = "If true, the meta data of the dropped item will be used instead of the block state meta.\nThis is usefull when the placed block has other meta than its item.";
   String COMMENT_RELATED_BLOCKS = "Defines a list of blocks which should be considered as part of this multiblock.";

   String getActiveStateTag();

   KeyValuePair getIdentityTag();

   boolean isEntity();

   boolean useDroppedItemMeta();

   Set<BlockState> getRelatedBlocks();

   default boolean isActive(World world, BlockPos pos) {
      TileEntity tile = world.getTileEntity(pos);
      return this.isActive(tile);
   }

   default boolean isActive(@Nullable INBTSerializable<NBTTagCompound> nbtSource) {
      String tag = this.getActiveStateTag();
      if (tag != null && !tag.isEmpty()) {
         if (nbtSource == null) {
            return false;
         } else {
            NBTTagCompound compound = (NBTTagCompound)nbtSource.serializeNBT();
            ForgeNBT.KeyPathPair pair = ForgeNBT.KeyPathPair.from(tag);
            compound = ForgeNBT.getNestedCompound(compound, pair.path);
            if (compound != null) {
               int intValue = compound.getInteger(pair.key);
               return intValue > 0;
            } else {
               return false;
            }
         }
      } else {
         return true;
      }
   }

   default boolean isIdentified(@Nullable INBTSerializable<NBTTagCompound> nbtSource) {
      KeyValuePair id = this.getIdentityTag();
      if (id != null && !id.key.isEmpty()) {
         if (nbtSource == null) {
            return false;
         } else {
            NBTTagCompound compound = (NBTTagCompound)nbtSource.serializeNBT();
            ForgeNBT.KeyPathPair pair = ForgeNBT.KeyPathPair.from(id.key);
            NBTBase tag = ForgeNBT.getTag(compound, pair);
            if (tag == null) {
               return false;
            } else {
               return id.value.isEmpty() ? true : id.value.equals(tag.toString());
            }
         }
      } else {
         return true;
      }
   }
}
