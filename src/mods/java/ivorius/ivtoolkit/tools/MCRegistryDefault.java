package ivorius.ivtoolkit.tools;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MCRegistryDefault implements MCRegistry {
   public static final MCRegistryDefault INSTANCE = new MCRegistryDefault();

   public MCRegistryDefault() {
   }

   public Item itemFromID(ResourceLocation itemID) {
      return (Item)Item.REGISTRY.getObject(itemID);
   }

   public ResourceLocation idFromItem(Item item) {
      return (ResourceLocation)Item.REGISTRY.getNameForObject(item);
   }

   public void modifyItemStackCompound(NBTTagCompound compound, ResourceLocation itemID) {
   }

   public Block blockFromID(ResourceLocation blockID) {
      return (Block)Block.REGISTRY.getObject(blockID);
   }

   public ResourceLocation idFromBlock(Block block) {
      return (ResourceLocation)Block.REGISTRY.getNameForObject(block);
   }

   public TileEntity loadTileEntity(World world, NBTTagCompound compound) {
      return TileEntity.create(world, compound);
   }
}
