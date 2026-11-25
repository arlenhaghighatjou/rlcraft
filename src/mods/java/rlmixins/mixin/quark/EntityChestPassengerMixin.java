package rlmixins.mixin.quark;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import vazkii.quark.management.entity.EntityChestPassenger;

@Mixin({EntityChestPassenger.class})
public abstract class EntityChestPassengerMixin extends Entity implements IInventory {
   private boolean dropContentsWhenDead = true;

   public EntityChestPassengerMixin(World worldIn) {
      super(worldIn);
   }

   @Nullable
   public Entity changeDimension(int dimensionIn, ITeleporter teleporter) {
      this.dropContentsWhenDead = false;
      return super.changeDimension(dimensionIn, teleporter);
   }

   @Overwrite
   public void setDropItemsWhenDead(boolean value) {
      this.dropContentsWhenDead = value;
   }

   @Overwrite
   public void setDead() {
      if (!this.world.isRemote && this.dropContentsWhenDead) {
         InventoryHelper.dropInventoryItems(this.world, this, this);
         InventoryHelper.spawnItemStack(this.world, this.posX, this.posY, this.posZ, ((EntityChestPassenger)this).getChestType());
      }

      super.setDead();
   }
}
