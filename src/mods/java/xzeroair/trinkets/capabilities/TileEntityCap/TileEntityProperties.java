package xzeroair.trinkets.capabilities.TileEntityCap;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.CapabilityBase;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.handlers.Counter;
import xzeroair.trinkets.util.helpers.BlockHelperUtil;

public class TileEntityProperties extends CapabilityBase<TileEntityProperties, TileEntity> {
   protected boolean hasEssence = false;
   protected int essence = -1;

   public TileEntityProperties(TileEntity te) {
      super(te);
   }

   public NBTTagCompound getTag() {
      NBTTagCompound teTag = ((TileEntity)this.object).getTileData();
      if (teTag != null) {
         String capTag = "xat.TrinketTE";
         if (!teTag.hasKey("xat.TrinketTE")) {
            teTag.setTag("xat.TrinketTE", new NBTTagCompound());
         }

         this.tag = teTag.getCompoundTag("xat.TrinketTE");
      }

      return super.getTag();
   }

   public TileEntityProperties setHasEssence(boolean has) {
      return this.setHasEssence(has, TrinketsConfig.SERVER.mana.essence_amount);
   }

   public TileEntityProperties setHasEssence(boolean has, int essence) {
      this.hasEssence = has;
      this.essence = essence;
      return this;
   }

   public void onUpdate() {
      if (this.hasEssence) {
         this.updateEssence();
      }

   }

   private void updateEssence() {
      World world = ((TileEntity)this.object).getWorld();
      BlockPos tePos = ((TileEntity)this.object).getPos();
      if (world != null) {
         if (this.hasEssence()) {
            if (this.getEssence() > 0) {
               int teEssence = this.getEssence();
               BlockPos pos1 = ((TileEntity)this.object).getPos().add(-10, -10, -10);
               BlockPos pos2 = ((TileEntity)this.object).getPos().add(10, 10, 10);
               boolean skip = BlockHelperUtil.isBlockNearby(world, new AxisAlignedBB(pos1, pos2), (state, pos) -> {
                  Block block = state.getBlock();
                  TileEntity te = world.getTileEntity(pos);
                  boolean isSelf = tePos.equals(pos);
                  return te != null && !isSelf && te != this.object ? ((Boolean)Capabilities.getTEProperties(te, Boolean.valueOf(false), (prop, matches) -> {
                     return prop.hasEssence();
                  })).booleanValue() : false;
               });
               if (skip) {
                  return;
               }

               List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, (new AxisAlignedBB(((TileEntity)this.object).getPos())).grow(4.0D));
               if (!entities.isEmpty()) {
                  for(EntityLivingBase e : entities) {
                     Capabilities.getMagicStats(e, (prop) -> {
                        if (e.isSneaking()) {
                           Counter counter = this.getTickHandler().getCounter("absorb.cooldown", TrinketsConfig.SERVER.mana.essence_cooldown, false, true, true, false);
                           if (counter.Tick()) {
                              double currentBonus = prop.getBonusMana();
                              double addedAmount = 1.0D;
                              prop.setBonusMana(currentBonus + addedAmount);
                              this.setEssence(teEssence - 1);
                           }
                        }

                     });
                  }
               }

               if (teEssence != this.getEssence()) {
                  this.saveToNBT(this.getTag());
                  ((TileEntity)this.object).markDirty();
               }
            }

            if (this.getEssence() <= 0) {
               ((TileEntity)this.object).getWorld().setBlockToAir(((TileEntity)this.object).getPos());
            }
         }

      }
   }

   public boolean hasEssence() {
      return this.hasEssence;
   }

   public int getEssence() {
      return this.essence;
   }

   public void setEssence(int essence) {
      if (this.essence != essence) {
         this.essence = essence;
      }

   }

   public NBTTagCompound saveToNBT(NBTTagCompound compound) {
      compound.setBoolean("provides", this.hasEssence);
      compound.setInteger("essence", this.essence);
      return compound;
   }

   public void loadFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("provides")) {
         this.hasEssence = compound.getBoolean("provides");
      }

      if (compound.hasKey("essence")) {
         this.essence = compound.getInteger("essence");
      }

   }
}
