package ivorius.reccomplex.utils;

import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MCRegistryRemapping implements MCRegistry {
   protected MCRegistry parent;
   protected FMLRemapper remapper;

   public MCRegistryRemapping(MCRegistry parent, FMLRemapper remapper) {
      this.parent = parent;
      this.remapper = remapper;
   }

   public Item itemFromID(ResourceLocation itemID) {
      return this.parent.itemFromID(this.remapper.mapItem(itemID));
   }

   public ResourceLocation idFromItem(Item item) {
      return this.parent.idFromItem(item);
   }

   public void modifyItemStackCompound(NBTTagCompound compound, ResourceLocation itemID) {
      ResourceLocation mapped = this.remapper.remapItem(itemID);
      if (mapped != null) {
         itemID = mapped;
         compound.setString("id", mapped.toString());
      }

      this.parent.modifyItemStackCompound(compound, itemID);
   }

   public Block blockFromID(ResourceLocation blockID) {
      return this.parent.blockFromID(this.remapper.mapBlock(blockID));
   }

   public ResourceLocation idFromBlock(Block block) {
      return this.parent.idFromBlock(block);
   }

   public TileEntity loadTileEntity(World world, NBTTagCompound compound) {
      ResourceLocation remap = this.remapper.remapTileEntity(new ResourceLocation(compound.getString("id")));
      if (remap != null) {
         NBTTagCompound copy = compound.copy();
         copy.setString("id", remap.toString());
         return this.parent.loadTileEntity(world, copy);
      } else {
         return this.parent.loadTileEntity(world, compound);
      }
   }
}
