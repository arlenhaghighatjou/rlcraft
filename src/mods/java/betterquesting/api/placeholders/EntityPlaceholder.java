package betterquesting.api.placeholders;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityPlaceholder extends Entity {
   private final EntityItem eItem;
   private NBTTagCompound original = new NBTTagCompound();

   public EntityPlaceholder(World world) {
      super(world);
      this.eItem = new EntityItem(world);
      this.eItem.setItem(new ItemStack(ItemPlaceholder.placeholder));
   }

   public EntityPlaceholder SetOriginalTags(NBTTagCompound tags) {
      this.original = tags;
      return this;
   }

   public NBTTagCompound GetOriginalTags() {
      return this.original;
   }

   public EntityItem GetItemEntity() {
      return this.eItem;
   }

   protected void entityInit() {
   }

   protected void readEntityFromNBT(NBTTagCompound tags) {
      this.original = tags.getCompoundTag("original");
   }

   protected void writeEntityToNBT(NBTTagCompound tags) {
      tags.setTag("original", this.original);
   }
}
