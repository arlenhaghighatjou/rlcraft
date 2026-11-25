package prospector.traverse.shadow.shootingstar;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class BlockCompound {
   private String modid;
   private Block block;
   private ItemBlock item;
   private ResourceLocation registryName;
   private boolean noItemBlock;

   public BlockCompound(String modid, Block block, ItemBlock item) {
      this.noItemBlock = false;
      this.modid = modid;
      this.block = block;
      this.item = item;
      this.registryName = block.getRegistryName();
   }

   public BlockCompound(String modid, Block block) {
      this(modid, block, (ItemBlock)null);
   }

   public BlockCompound(String modid, Block block, boolean noItemBlock) {
      this(modid, block, (ItemBlock)null);
      if (noItemBlock) {
         this.noItemBlock = true;
      }

   }

   public Block getBlock() {
      return this.block;
   }

   public BlockCompound setBlock(Block block) {
      this.block = block;
      return this;
   }

   public ItemBlock getItem() {
      return this.item;
   }

   public BlockCompound setItem(ItemBlock item) {
      this.item = item;
      return this;
   }

   public String getModid() {
      return this.modid;
   }

   public ResourceLocation getRegistryName() {
      return this.registryName;
   }

   public boolean hasItemBlock() {
      return !this.noItemBlock;
   }
}
