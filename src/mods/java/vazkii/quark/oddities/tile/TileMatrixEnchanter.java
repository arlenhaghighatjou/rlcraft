package vazkii.quark.oddities.tile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.decoration.block.BlockCandle;
import vazkii.quark.decoration.feature.TallowAndCandles;
import vazkii.quark.oddities.feature.MatrixEnchanting;
import vazkii.quark.oddities.inventory.ContainerMatrixEnchanting;
import vazkii.quark.oddities.inventory.EnchantmentMatrix;

public class TileMatrixEnchanter extends TileMatrixEnchanterBase {
   public static final int OPER_ADD = 0;
   public static final int OPER_PLACE = 1;
   public static final int OPER_REMOVE = 2;
   public static final int OPER_ROTATE = 3;
   public static final int OPER_MERGE = 4;
   public static final String TAG_STACK_MATRIX = "quark:enchantingMatrix";
   private static final String TAG_MATRIX = "matrix";
   private static final String TAG_MATRIX_UUID_LESS = "uuidLess";
   private static final String TAG_MATRIX_UUID_MOST = "uuidMost";
   private static final String TAG_CHARGE = "charge";
   public EnchantmentMatrix matrix;
   private boolean matrixDirty = false;
   private UUID matrixId;
   public final Map<Enchantment, Integer> influences = new HashMap();
   public int bookshelfPower;
   public int enchantability;
   public int charge;

   public TileMatrixEnchanter() {
   }

   public void update() {
      super.update();
      ItemStack item = this.getStackInSlot(0);
      if (item.isEmpty()) {
         this.matrix = null;
         this.matrixDirty = true;
      } else {
         this.loadMatrix(item);
         if (this.world.getTotalWorldTime() % 20L == 0L || this.matrixDirty) {
            this.updateEnchantPower();
         }
      }

      if (this.charge <= 0 && !this.world.isRemote) {
         ItemStack lapis = this.getStackInSlot(1);
         if (!lapis.isEmpty()) {
            lapis.shrink(1);
            this.charge += MatrixEnchanting.chargePerLapis;
            this.sync();
         }
      }

      if (this.matrixDirty) {
         this.makeOutput();
         this.matrixDirty = false;
      }

   }

   public void onOperation(EntityPlayer player, int operation, int arg0, int arg1, int arg2) {
      if (this.matrix != null) {
         switch(operation) {
         case 0:
            this.apply((m) -> {
               return this.generateAndPay(m, player);
            });
            break;
         case 1:
            this.apply((m) -> {
               return m.place(arg0, arg1, arg2);
            });
            break;
         case 2:
            this.apply((m) -> {
               return m.remove(arg0);
            });
            break;
         case 3:
            this.apply((m) -> {
               return m.rotate(arg0);
            });
            break;
         case 4:
            this.apply((m) -> {
               return m.merge(arg0, arg1);
            });
         }

      }
   }

   private void apply(Predicate<EnchantmentMatrix> oper) {
      if (oper.test(this.matrix)) {
         ItemStack item = this.getStackInSlot(0);
         this.commitMatrix(item);
      }

   }

   private boolean generateAndPay(EnchantmentMatrix matrix, EntityPlayer player) {
      if (matrix.canGeneratePiece(this.bookshelfPower, this.enchantability) && matrix.validateXp(player, this.bookshelfPower)) {
         boolean creative = player.isCreative();
         int cost = matrix.getNewPiecePrice();
         if ((this.charge > 0 || creative) && matrix.generatePiece(this.influences, this.bookshelfPower)) {
            if (!creative) {
               player.addExperienceLevel(-cost);
            }

            this.charge = Math.max(this.charge - 1, 0);
         }
      }

      return true;
   }

   private void makeOutput() {
      if (!this.world.isRemote) {
         this.setInventorySlotContents(2, ItemStack.EMPTY);
         ItemStack in = this.getStackInSlot(0);
         if (!in.isEmpty() && this.matrix != null && !this.matrix.placedPieces.isEmpty()) {
            ItemStack out = in.copy();
            boolean book = false;
            if (out.getItem() == Items.BOOK) {
               out = new ItemStack(Items.ENCHANTED_BOOK);
               book = true;
            }

            Map<Enchantment, Integer> enchantments = new HashMap();
            Iterator var5 = this.matrix.placedPieces.iterator();

            while(var5.hasNext()) {
               int i = ((Integer)var5.next()).intValue();
               EnchantmentMatrix.Piece p = (EnchantmentMatrix.Piece)this.matrix.pieces.get(Integer.valueOf(i));
               if (p != null && p.enchant != null) {
                  for(Enchantment o : enchantments.keySet()) {
                     if (o == p.enchant || !p.enchant.isCompatibleWith(o) || !o.isCompatibleWith(p.enchant)) {
                        return;
                     }
                  }

                  enchantments.put(p.enchant, Integer.valueOf(p.level));
               }
            }

            if (book) {
               for(Entry<Enchantment, Integer> e : enchantments.entrySet()) {
                  ItemEnchantedBook.addEnchantment(out, new EnchantmentData((Enchantment)e.getKey(), ((Integer)e.getValue()).intValue()));
               }
            } else {
               EnchantmentHelper.setEnchantments(enchantments, out);
               ItemNBTHelper.getNBT(out).removeTag("quark:enchantingMatrix");
            }

            this.setInventorySlotContents(2, out);
         }

      }
   }

   private void loadMatrix(ItemStack stack) {
      if (this.matrix == null || this.matrix.target != stack) {
         if (this.matrix != null) {
            this.matrixDirty = true;
         }

         this.matrix = null;
         if (stack.isItemEnchantable()) {
            this.matrix = new EnchantmentMatrix(stack, this.world.rand);
            this.matrixDirty = true;
            this.makeUUID();
            if (ItemNBTHelper.verifyExistence(stack, "quark:enchantingMatrix")) {
               NBTTagCompound cmp = ItemNBTHelper.getCompound(stack, "quark:enchantingMatrix", true);
               if (cmp != null) {
                  this.matrix.readFromNBT(cmp);
               }
            }
         }
      }

   }

   private void commitMatrix(ItemStack stack) {
      if (!this.world.isRemote) {
         NBTTagCompound cmp = new NBTTagCompound();
         this.matrix.writeToNBT(cmp);
         ItemNBTHelper.setCompound(stack, "quark:enchantingMatrix", cmp);
         this.matrixDirty = true;
         this.makeUUID();
         this.sync();
      }
   }

   private void makeUUID() {
      if (!this.world.isRemote) {
         this.matrixId = UUID.randomUUID();
      }

   }

   private void updateEnchantPower() {
      ItemStack item = this.getStackInSlot(0);
      this.influences.clear();
      if (!item.isEmpty()) {
         this.enchantability = item.getItem().getItemEnchantability(item);
         float power = 0.0F;

         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               if ((j != 0 || k != 0) && this.world.isAirBlock(this.pos.add(k, 0, j)) && this.world.isAirBlock(this.pos.add(k, 1, j))) {
                  power = power + this.getEnchantPowerAt(this.world, this.pos.add(k * 2, 0, j * 2));
                  power = power + this.getEnchantPowerAt(this.world, this.pos.add(k * 2, 1, j * 2));
                  if (k != 0 && j != 0) {
                     power = power + this.getEnchantPowerAt(this.world, this.pos.add(k * 2, 0, j));
                     power = power + this.getEnchantPowerAt(this.world, this.pos.add(k * 2, 1, j));
                     power = power + this.getEnchantPowerAt(this.world, this.pos.add(k, 0, j * 2));
                     power = power + this.getEnchantPowerAt(this.world, this.pos.add(k, 1, j * 2));
                  }
               }
            }
         }

         this.bookshelfPower = Math.min((int)power, MatrixEnchanting.maxBookshelves);
      }
   }

   private float getEnchantPowerAt(World world, BlockPos pos) {
      if (MatrixEnchanting.allowInfluencing) {
         IBlockState state = world.getBlockState(pos);
         Block block = state.getBlock();
         if (block == TallowAndCandles.candle) {
            EnumDyeColor ord = ((BlockCandle.Variants)state.getValue(TallowAndCandles.candle.variantProp)).color;

            for(Enchantment e : (List)MatrixEnchanting.candleInfluences.get(ord)) {
               int curr = ((Integer)this.influences.getOrDefault(e, Integer.valueOf(0))).intValue();
               if (curr < MatrixEnchanting.influenceMax) {
                  this.influences.put(e, Integer.valueOf(curr + 1));
               }
            }
         }
      }

      return ForgeHooks.getEnchantPower(world, pos);
   }

   public void writeSharedNBT(NBTTagCompound cmp) {
      super.writeSharedNBT(cmp);
      NBTTagCompound matrixCmp = new NBTTagCompound();
      if (this.matrix != null) {
         this.matrix.writeToNBT(matrixCmp);
         cmp.setTag("matrix", matrixCmp);
         if (this.matrixId != null) {
            cmp.setLong("uuidLess", this.matrixId.getLeastSignificantBits());
            cmp.setLong("uuidMost", this.matrixId.getMostSignificantBits());
         }
      }

      cmp.setInteger("charge", this.charge);
   }

   public void readSharedNBT(NBTTagCompound cmp) {
      super.readSharedNBT(cmp);
      if (cmp.hasKey("matrix")) {
         long least = cmp.getLong("uuidLess");
         long most = cmp.getLong("uuidMost");
         UUID newId = new UUID(most, least);
         if (!newId.equals(this.matrixId)) {
            NBTTagCompound matrixCmp = cmp.getCompoundTag("matrix");
            this.matrixId = newId;
            this.matrix = new EnchantmentMatrix(this.getStackInSlot(0), new Random());
            this.matrix.readFromNBT(matrixCmp);
         }
      } else {
         this.matrix = null;
      }

      this.charge = cmp.getInteger("charge");
   }

   @Nonnull
   public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
      return new ContainerMatrixEnchanting(playerInventory, this);
   }
}
