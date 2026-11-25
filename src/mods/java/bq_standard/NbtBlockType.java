package bq_standard;

import betterquesting.api.placeholders.ItemPlaceholder;
import betterquesting.api.utils.BigItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class NbtBlockType {
   public Block b = Blocks.LOG;
   public int m = -1;
   public int n = 1;
   public String oreDict = "";
   public NBTTagCompound tags = new NBTTagCompound();

   public NbtBlockType() {
   }

   public NbtBlockType(Block block) {
      this.b = block;
      this.oreDict = "";
      this.tags = new NBTTagCompound();
   }

   public NbtBlockType(IBlockState state) {
      this.b = state.getBlock();
      this.m = this.b.getMetaFromState(state);
      this.oreDict = "";
      this.tags = new NBTTagCompound();
   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      json.setString("blockID", this.b.getRegistryName().toString());
      json.setInteger("meta", this.m);
      json.setTag("nbt", this.tags);
      json.setInteger("amount", this.n);
      json.setString("oreDict", this.oreDict);
      return json;
   }

   public void readFromNBT(NBTTagCompound json) {
      this.b = (Block)Block.REGISTRY.getObject(new ResourceLocation(json.getString("blockID")));
      this.m = json.getInteger("meta");
      this.tags = json.getCompoundTag("nbt");
      this.n = json.getInteger("amount");
      this.oreDict = json.getString("oreDict");
   }

   public BigItemStack getItemStack() {
      BigItemStack stack;
      if (this.b == null) {
         stack = new BigItemStack(ItemPlaceholder.placeholder, this.n, 0);
         stack.getBaseStack().setStackDisplayName("NULL");
      } else {
         stack = new BigItemStack(this.b, this.n, this.m);
      }

      stack.setOreDict(this.oreDict);
      return stack;
   }
}
