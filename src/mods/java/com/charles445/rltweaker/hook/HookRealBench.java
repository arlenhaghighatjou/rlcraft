package com.charles445.rltweaker.hook;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.WorldServer;

public class HookRealBench {
   public static boolean reflectionFailed = false;
   @Nullable
   public static Class c_ASMHooks;
   @Nullable
   public static Method m_ASMHooks_getTile;
   @Nullable
   public static Class c_WorkbenchTile;
   @Nullable
   public static Field f_mResult;

   public HookRealBench() {
   }

   public static NonNullList<ItemStack> getResultSlotInit() {
      return NonNullList.withSize(1, ItemStack.EMPTY);
   }

   public static void writeSlots(NBTTagCompound nbt, NonNullList<ItemStack> mResult) {
      if (((ItemStack)mResult.get(0)).isEmpty()) {
         nbt.removeTag("Result");
      } else {
         NBTTagCompound result = new NBTTagCompound();
         ((ItemStack)mResult.get(0)).writeToNBT(result);
         nbt.setTag("Result", result);
      }

   }

   public static void readSlots(NBTTagCompound nbt, NonNullList<ItemStack> mResult) {
      if (nbt.hasKey("Result")) {
         mResult.set(0, new ItemStack(nbt.getCompoundTag("Result")));
      } else {
         mResult.set(0, ItemStack.EMPTY);
      }

   }

   @Nullable
   private static TileEntity getTileFromASMHooks(ContainerWorkbench container) {
      if (reflectionFailed) {
         return null;
      } else {
         try {
            return (TileEntity)m_ASMHooks_getTile.invoke((Object)null, container);
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var2) {
            reflectionFailed = true;
            throw new RuntimeException("Failed to invoke ASMHooks.getTile after successful reflection setup", var2);
         }
      }
   }

   @Nullable
   private static NonNullList<ItemStack> getResultFromTile(TileEntity tile) {
      if (reflectionFailed) {
         return null;
      } else {
         try {
            return (NonNullList)f_mResult.get(c_WorkbenchTile.cast(tile));
         } catch (IllegalAccessException | IllegalArgumentException var2) {
            reflectionFailed = true;
            throw new RuntimeException("Failed to get WorkbenchTile.mResult after successful reflection setup", var2);
         }
      }
   }

   static {
      try {
         c_ASMHooks = Class.forName("pw.prok.realbench.asm.ASMHooks");
         m_ASMHooks_getTile = ReflectUtil.findMethod(c_ASMHooks, "getTile", ContainerWorkbench.class);
         c_WorkbenchTile = Class.forName("pw.prok.realbench.WorkbenchTile");
         f_mResult = ReflectUtil.findField(c_WorkbenchTile, "mResult");
      } catch (Exception var1) {
         reflectionFailed = true;
         var1.printStackTrace();
      }

   }

   public static class Result extends InventoryCraftResult {
      private ContainerWorkbench container;
      private TileEntity mTile;
      private NonNullList<ItemStack> _stackResult = NonNullList.withSize(1, ItemStack.EMPTY);
      private static boolean setupReflection = false;
      private static boolean reflectedSuccessfully = false;
      private static Field f_Field_modifiers = null;
      private static Field f_InventoryCraftResult_stackResult = null;

      public Result(Container con) {
         if (!setupReflection) {
            this.setupReflection();
         }

         this.setup(con);
      }

      private void setupReflection() {
         setupReflection = true;

         try {
            f_Field_modifiers = ReflectUtil.findField(Field.class, "modifiers");
            f_InventoryCraftResult_stackResult = ReflectUtil.findFieldAny(InventoryCraftResult.class, "stackResult", "stackResult");
            f_Field_modifiers.setInt(f_InventoryCraftResult_stackResult, f_InventoryCraftResult_stackResult.getModifiers() & -17);
            reflectedSuccessfully = true;
            RLTweaker.logger.info("HookRealBench reflected successfully!");
         } catch (Exception var2) {
            var2.printStackTrace();
            ErrorUtil.logSilent("RealBench Compatibility Reflection Setup");
         }

      }

      private NonNullList<ItemStack> getStackResult() {
         if (reflectedSuccessfully) {
            try {
               return (NonNullList)f_InventoryCraftResult_stackResult.get(this);
            } catch (IllegalAccessException | IllegalArgumentException var2) {
               ErrorUtil.logSilent("RealBench Compatibility Reflection Invocation Get");
            }
         }

         return this._stackResult;
      }

      private void setStackResult(NonNullList<ItemStack> stack) {
         if (reflectedSuccessfully) {
            try {
               f_InventoryCraftResult_stackResult.set(this, stack);
               return;
            } catch (IllegalAccessException | IllegalArgumentException var3) {
               ErrorUtil.logSilent("RealBench Compatibility Reflection Invocation Set");
            }
         }

         this._stackResult = stack;
      }

      private void setup(Container con) {
         this.container = con instanceof ContainerWorkbench ? (ContainerWorkbench)con : null;
         if (this.container != null) {
            this.mTile = HookRealBench.getTileFromASMHooks(this.container);
            if (this.mTile != null) {
               NonNullList<ItemStack> stackGet = HookRealBench.getResultFromTile(this.mTile);
               if (stackGet != null) {
                  this.setStackResult(stackGet);
               }
            }
         }
      }

      public void markDirty() {
         if (this.mTile != null) {
            this.mTile.markDirty();
            if (!this.mTile.getWorld().isRemote) {
               WorldServer world = (WorldServer)this.mTile.getWorld();
               world.getPlayerChunkMap().markBlockForUpdate(this.mTile.getPos());
            }
         }
      }

      public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
         this.getStackResult().set(0, stack);
         this.markDirty();
      }

      @MethodsReturnNonnullByDefault
      public ItemStack decrStackSize(int index, int count) {
         ItemStack var3;
         try {
            var3 = ItemStackHelper.getAndRemove(this.getStackResult(), 0);
         } finally {
            this.markDirty();
         }

         return var3;
      }

      public boolean isEmpty() {
         for(ItemStack itemstack : this.getStackResult()) {
            if (!itemstack.isEmpty()) {
               return false;
            }
         }

         return true;
      }

      public ItemStack getStackInSlot(int index) {
         return (ItemStack)this.getStackResult().get(0);
      }

      public ItemStack removeStackFromSlot(int index) {
         return ItemStackHelper.getAndRemove(this.getStackResult(), 0);
      }

      public void clear() {
         this.getStackResult().clear();
      }
   }
}
