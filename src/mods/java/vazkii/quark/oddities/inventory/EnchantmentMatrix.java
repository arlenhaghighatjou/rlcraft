package vazkii.quark.oddities.inventory;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandom;
import vazkii.quark.oddities.feature.MatrixEnchanting;

public class EnchantmentMatrix {
   public static final int MATRIX_WIDTH = 5;
   public static final int MATRIX_HEIGHT = 5;
   private static final int PIECE_VARIANTS = 8;
   private static final String TAG_PIECES = "pieces";
   private static final String TAG_PIECE_ID = "id";
   private static final String TAG_BENCHED_PIECES = "benchedPieces";
   private static final String TAG_PLACED_PIECES = "placedPieces";
   private static final String TAG_COUNT = "count";
   private static final String TAG_TYPE_COUNT = "typeCount";
   public final Map<Enchantment, Integer> totalValue = new HashMap();
   public final Map<Integer, EnchantmentMatrix.Piece> pieces = new HashMap();
   public List<Integer> benchedPieces = new ArrayList();
   public List<Integer> placedPieces = new ArrayList();
   public int[][] matrix;
   public int count;
   public int typeCount;
   public final boolean book;
   public final ItemStack target;
   public final EaglercraftRandom rng;

   public EnchantmentMatrix(ItemStack target, EaglercraftRandom rng) {
      this.target = target;
      this.rng = rng;
      this.book = target.getItem() == Items.BOOK;
      this.computeMatrix();
   }

   public boolean canGeneratePiece(int bookshelfPower, int enchantability) {
      if (enchantability == 0) {
         return false;
      } else if (this.book) {
         if (!MatrixEnchanting.allowBooks) {
            return false;
         } else {
            int bookshelfCount = Math.max(0, Math.min(bookshelfPower - 1, MatrixEnchanting.maxBookshelves)) / 7;
            int maxCount = MatrixEnchanting.baseMaxPieceCountBook + bookshelfCount;
            return this.count < maxCount;
         }
      } else {
         int bookshelfCount = (Math.min(bookshelfPower, MatrixEnchanting.maxBookshelves) + 1) / 2;
         int enchantabilityCount = Math.min(bookshelfPower, enchantability) / 2;
         int maxCount = MatrixEnchanting.baseMaxPieceCount + bookshelfCount + enchantabilityCount;
         return this.count < maxCount;
      }
   }

   public boolean validateXp(EntityPlayer player, int bookshelfPower) {
      return player.isCreative() || player.experienceLevel >= this.getMinXpLevel(bookshelfPower) && player.experienceLevel >= this.getNewPiecePrice();
   }

   public int getMinXpLevel(int bookshelfPower) {
      float scale = MatrixEnchanting.minLevelScaleFactor;
      int cutoff = MatrixEnchanting.minLevelCutoff;
      if (this.book) {
         return (int)((float)Math.min(bookshelfPower, MatrixEnchanting.maxBookshelves) * MatrixEnchanting.minLevelScaleFactorBook);
      } else {
         return this.count > cutoff ? (int)((float)cutoff * scale) - cutoff + this.count : (int)((float)this.count * scale);
      }
   }

   public int getNewPiecePrice() {
      return 1 + (MatrixEnchanting.piecePriceScale == 0 ? 0 : this.count / MatrixEnchanting.piecePriceScale);
   }

   public boolean generatePiece(Map<Enchantment, Integer> influences, int bookshelfPower) {
      EnchantmentMatrix.EnchantmentDataWrapper data = this.generateRandomEnchantment(influences, bookshelfPower);
      if (data == null) {
         return false;
      } else {
         int type = -1;

         for(EnchantmentMatrix.Piece p : this.pieces.values()) {
            if (p.enchant == data.enchantment) {
               type = p.type;
            }
         }

         if (type == -1) {
            type = this.typeCount % 8;
            ++this.typeCount;
         }

         EnchantmentMatrix.Piece piece = new EnchantmentMatrix.Piece(data, type);
         piece.generateBlocks();
         this.pieces.put(Integer.valueOf(this.count), piece);
         this.totalValue.put(piece.enchant, Integer.valueOf(((Integer)this.totalValue.getOrDefault(piece.enchant, Integer.valueOf(0))).intValue() + piece.getValue()));
         this.benchedPieces.add(Integer.valueOf(this.count));
         ++this.count;
         if (this.book && this.count == 1) {
            for(int i = 0; i < 2; ++i) {
               if (this.rng.nextBoolean()) {
                  ++this.count;
               }
            }
         }

         return true;
      }
   }

   private EnchantmentMatrix.EnchantmentDataWrapper generateRandomEnchantment(Map<Enchantment, Integer> influences, int bookshelfPower) {
      int level = this.book ? MatrixEnchanting.bookEnchantability + this.rng.nextInt(Math.max(1, bookshelfPower) * 2) : 0;
      List<EnchantmentMatrix.Piece> marked = (List)this.pieces.values().stream().filter((p) -> {
         return p.marked;
      }).collect(Collectors.toList());
      List<EnchantmentMatrix.EnchantmentDataWrapper> validEnchants = new ArrayList();

      for(Enchantment enchantment : Enchantment.REGISTRY) {
         if ((!enchantment.isTreasureEnchantment() || MatrixEnchanting.allowTreasures) && !MatrixEnchanting.disallowedEnchantments.contains(Objects.toString(enchantment.getRegistryName())) && (enchantment.canApplyAtEnchantingTable(this.target) || this.book && enchantment.isAllowedOnBooks())) {
            int enchantLevel = 1;
            if (this.book) {
               for(int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; --i) {
                  if (level >= enchantment.getMinEnchantability(i) && level <= enchantment.getMaxEnchantability(i)) {
                     enchantLevel = i;
                     break;
                  }
               }
            }

            int valueAdded = getValue(enchantment, enchantLevel);
            int currentValue = ((Integer)this.totalValue.getOrDefault(enchantment, Integer.valueOf(0))).intValue();
            if (valueAdded + currentValue <= getValue(enchantment, enchantment.getMaxLevel()) + getMaxXP(enchantment, enchantment.getMaxLevel())) {
               EnchantmentMatrix.EnchantmentDataWrapper wrapper = new EnchantmentMatrix.EnchantmentDataWrapper(enchantment, enchantLevel);
               wrapper.normalizeRarity(influences, marked);
               validEnchants.add(wrapper);
            }
         }
      }

      if (validEnchants.isEmpty()) {
         return null;
      } else {
         int total = 0;

         for(EnchantmentMatrix.EnchantmentDataWrapper wrapper : validEnchants) {
            total += wrapper.itemWeight;
         }

         if (total == 0) {
            for(EnchantmentMatrix.EnchantmentDataWrapper wrapper : validEnchants) {
               ++wrapper.itemWeight;
            }
         }

         return (EnchantmentMatrix.EnchantmentDataWrapper)WeightedRandom.getRandomItem(this.rng, validEnchants);
      }
   }

   public boolean place(int id, int x, int y) {
      EnchantmentMatrix.Piece p = (EnchantmentMatrix.Piece)this.pieces.get(Integer.valueOf(id));
      if (p != null && this.benchedPieces.contains(Integer.valueOf(id)) && this.canPlace(p, x, y)) {
         p.x = x;
         p.y = y;
         this.benchedPieces.remove(Integer.valueOf(id));
         this.placedPieces.add(Integer.valueOf(id));
         this.computeMatrix();
         return true;
      } else {
         return false;
      }
   }

   public boolean remove(int id) {
      EnchantmentMatrix.Piece p = (EnchantmentMatrix.Piece)this.pieces.get(Integer.valueOf(id));
      if (p != null && this.placedPieces.contains(Integer.valueOf(id))) {
         this.placedPieces.remove(Integer.valueOf(id));
         this.benchedPieces.add(Integer.valueOf(id));
         this.computeMatrix();
         return true;
      } else {
         return false;
      }
   }

   public boolean rotate(int id) {
      EnchantmentMatrix.Piece p = (EnchantmentMatrix.Piece)this.pieces.get(Integer.valueOf(id));
      if (p != null && this.benchedPieces.contains(Integer.valueOf(id))) {
         p.rotate();
         return true;
      } else {
         return false;
      }
   }

   public boolean merge(int placed, int hover) {
      EnchantmentMatrix.Piece placedPiece = (EnchantmentMatrix.Piece)this.pieces.get(Integer.valueOf(placed));
      EnchantmentMatrix.Piece hoveredPiece = (EnchantmentMatrix.Piece)this.pieces.get(Integer.valueOf(hover));
      if (placedPiece != null && hoveredPiece != null && this.placedPieces.contains(Integer.valueOf(placed)) && this.benchedPieces.contains(Integer.valueOf(hover))) {
         Enchantment enchant = placedPiece.enchant;
         if (hoveredPiece.enchant == enchant && placedPiece.level < enchant.getMaxLevel()) {
            placedPiece.xp += hoveredPiece.getValue();

            for(int max = placedPiece.getMaxXP(); placedPiece.xp >= max && placedPiece.level < enchant.getMaxLevel(); max = placedPiece.getMaxXP()) {
               ++placedPiece.level;
               placedPiece.xp -= max;
            }

            if (hoveredPiece.marked) {
               placedPiece.marked = true;
            }

            this.benchedPieces.remove(Integer.valueOf(hover));
            this.pieces.remove(Integer.valueOf(hover));
            return true;
         }
      }

      return false;
   }

   public void writeToNBT(NBTTagCompound cmp) {
      NBTTagList list = new NBTTagList();

      for(Integer i : this.pieces.keySet()) {
         NBTTagCompound pieceTag = new NBTTagCompound();
         pieceTag.setInteger("id", i.intValue());
         if (((EnchantmentMatrix.Piece)this.pieces.get(i)).enchant != null) {
            ((EnchantmentMatrix.Piece)this.pieces.get(i)).writeToNBT(pieceTag);
            list.appendTag(pieceTag);
         }
      }

      cmp.setTag("pieces", list);
      cmp.setIntArray("benchedPieces", this.packList(this.benchedPieces));
      cmp.setIntArray("placedPieces", this.packList(this.placedPieces));
      cmp.setInteger("count", this.count);
      cmp.setInteger("typeCount", this.typeCount);
   }

   public void readFromNBT(NBTTagCompound cmp) {
      this.pieces.clear();
      this.totalValue.clear();
      NBTTagList plist = cmp.getTagList("pieces", cmp.getId());

      for(int i = 0; i < plist.tagCount(); ++i) {
         NBTTagCompound pieceTag = plist.getCompoundTagAt(i);
         int id = pieceTag.getInteger("id");
         EnchantmentMatrix.Piece piece = new EnchantmentMatrix.Piece();
         piece.readFromNBT(pieceTag);
         this.pieces.put(Integer.valueOf(id), piece);
         this.totalValue.put(piece.enchant, Integer.valueOf(((Integer)this.totalValue.getOrDefault(piece.enchant, Integer.valueOf(0))).intValue() + piece.getValue()));
      }

      this.benchedPieces = this.unpackList(cmp.getIntArray("benchedPieces"));
      this.placedPieces = this.unpackList(cmp.getIntArray("placedPieces"));
      this.count = cmp.getInteger("count");
      this.typeCount = cmp.getInteger("typeCount");
      this.computeMatrix();
   }

   private void computeMatrix() {
      this.matrix = new int[5][5];

      for(int i = 0; i < 5; ++i) {
         for(int j = 0; j < 5; ++j) {
            this.matrix[i][j] = -1;
         }
      }

      for(Integer i : this.placedPieces) {
         EnchantmentMatrix.Piece p = (EnchantmentMatrix.Piece)this.pieces.get(i);

         for(int[] b : p.blocks) {
            this.matrix[p.x + b[0]][p.y + b[1]] = i.intValue();
         }
      }

   }

   public boolean canPlace(EnchantmentMatrix.Piece p, int x, int y) {
      for(int[] b : p.blocks) {
         int bx = b[0] + x;
         int by = b[1] + y;
         if (bx < 0 || by < 0 || bx >= 5 || by >= 5) {
            return false;
         }

         if (this.matrix[bx][by] != -1) {
            return false;
         }
      }

      return true;
   }

   private int[] packList(List<Integer> list) {
      int[] arr = new int[list.size()];

      for(int i = 0; i < arr.length; ++i) {
         arr[i] = ((Integer)list.get(i)).intValue();
      }

      return arr;
   }

   private List<Integer> unpackList(int[] arr) {
      List<Integer> list = new ArrayList(arr.length);

      for(int anArr : arr) {
         list.add(Integer.valueOf(anArr));
      }

      return list;
   }

   public static int getMaxXP(Enchantment enchantment, int level) {
      if (level >= enchantment.getMaxLevel()) {
         return 0;
      } else {
         switch(enchantment.getRarity()) {
         case COMMON:
            return level;
         case UNCOMMON:
            return level / 2 + 1;
         default:
            return 1;
         }
      }
   }

   public static int getValue(Enchantment enchantment, int level) {
      int total = 1;

      for(int i = 1; i < level; ++i) {
         total += getMaxXP(enchantment, i);
      }

      return total;
   }

   private static class EnchantmentDataWrapper extends EnchantmentData {
      private boolean marked;
      private int influence;

      public EnchantmentDataWrapper(Enchantment enchantmentObj, int enchLevel) {
         super(enchantmentObj, enchLevel);
      }

      public void normalizeRarity(Map<Enchantment, Integer> influences, List<EnchantmentMatrix.Piece> markedEnchants) {
         if (MatrixEnchanting.normalizeRarity) {
            this.itemWeight *= 10000;
            switch(this.enchantment.getRarity()) {
            case COMMON:
               this.itemWeight = 80000;
               break;
            case UNCOMMON:
               this.itemWeight = 40000;
               break;
            case RARE:
               this.itemWeight = 25000;
               break;
            case VERY_RARE:
               this.itemWeight = 5000;
            }

            this.influence = ((Integer)influences.getOrDefault(this.enchantment, Integer.valueOf(0))).intValue();
            float multiplier = 1.0F + (float)this.influence * MatrixEnchanting.influencePower;
            this.itemWeight = (int)((float)this.itemWeight * multiplier);
            boolean mark = true;

            for(EnchantmentMatrix.Piece other : markedEnchants) {
               if (other.enchant == this.enchantment) {
                  this.itemWeight = (int)((float)this.itemWeight * MatrixEnchanting.dupeMultiplier);
                  mark = false;
                  break;
               }

               if (!other.enchant.isCompatibleWith(this.enchantment) || !this.enchantment.isCompatibleWith(other.enchant)) {
                  this.itemWeight = (int)((float)this.itemWeight * MatrixEnchanting.incompatibleMultiplier);
                  mark = false;
                  break;
               }
            }

            if (mark) {
               this.marked = true;
            }
         }

      }
   }

   public static class Piece {
      private static final int[][][] PIECE_TYPES = new int[][][]{{{0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}}, {{0, 0}, {-1, 0}, {1, 0}, {-1, -1}, {0, -1}}, {{0, 0}, {-1, 0}, {1, 0}, {-1, 1}, {1, 1}}, {{0, 0}, {-1, 0}, {1, 0}, {-1, -1}, {1, 1}}, {{0, 0}, {-1, 0}, {1, 0}, {1, -1}, {1, 1}}, {{0, 0}, {-1, 0}, {1, 0}, {0, -1}, {1, 1}}, {{0, 0}, {-1, 0}, {0, -1}, {-1, -1}, {1, 1}}, {{0, 0}, {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {1, 1}}, {{0, 0}, {-1, 0}, {0, -1}, {-1, -1}, {-1, 1}, {1, -1}}, {{0, 0}, {-1, 0}, {0, -1}, {-1, -1}, {-1, 1}, {1, 1}}, {{0, 0}, {-1, 0}, {1, 0}, {-1, -1}, {1, -1}, {1, 1}}, {{0, 0}, {-1, 0}, {1, 0}, {0, -1}, {-1, -1}, {1, 1}}};
      private static final String TAG_COLOR = "color";
      private static final String TAG_TYPE = "type";
      private static final String TAG_ENCHANTMENT = "enchant";
      private static final String TAG_LEVEL = "level";
      private static final String TAG_BLOCK_COUNT = "blockCount";
      private static final String TAG_BLOCK = "block";
      private static final String TAG_X = "x";
      private static final String TAG_Y = "y";
      private static final String TAG_XP = "xp";
      private static final String TAG_MARKED = "marked";
      private static final String TAG_INFLUENCE = "influence";
      public Enchantment enchant;
      public int level;
      public int color;
      public int type;
      public int x;
      public int y;
      public int xp;
      public int[][] blocks;
      public boolean marked;
      public int influence;

      public Piece() {
      }

      public Piece(EnchantmentMatrix.EnchantmentDataWrapper wrapper, int type) {
         this.enchant = wrapper.enchantment;
         this.level = wrapper.enchantmentLevel;
         this.marked = wrapper.marked;
         this.influence = wrapper.influence;
         this.type = type;
         EaglercraftRandom rng = new EaglercraftRandom((long)Objects.toString(this.enchant.getRegistryName()).hashCode());
         float h = rng.nextFloat();
         float s = rng.nextFloat() * 0.2F + 0.8F;
         float b = rng.nextFloat() * 0.25F + 0.75F;
         this.color = Color.HSBtoRGB(h, s, b);
      }

      public void generateBlocks() {
         int type = (int)(Math.random() * (double)PIECE_TYPES.length);
         int[][] copyPieces = PIECE_TYPES[type];
         this.blocks = new int[copyPieces.length][2];

         for(int i = 0; i < this.blocks.length; ++i) {
            this.blocks[i][0] = copyPieces[i][0];
            this.blocks[i][1] = copyPieces[i][1];
         }

         int rotations = (int)(Math.random() * 4.0D);

         for(int i = 0; i < rotations; ++i) {
            this.rotate();
         }

      }

      public void rotate() {
         for(int[] b : this.blocks) {
            int x = b[0];
            int y = b[1];
            b[0] = y;
            b[1] = -x;
         }

      }

      public int getMaxXP() {
         return EnchantmentMatrix.getMaxXP(this.enchant, this.level);
      }

      public int getValue() {
         return EnchantmentMatrix.getValue(this.enchant, this.level) + this.xp;
      }

      public void writeToNBT(NBTTagCompound cmp) {
         cmp.setInteger("color", this.color);
         cmp.setInteger("type", this.type);
         if (this.enchant != null) {
            cmp.setString("enchant", Objects.toString(this.enchant.getRegistryName()));
         }

         cmp.setInteger("level", this.level);
         cmp.setInteger("x", this.x);
         cmp.setInteger("y", this.y);
         cmp.setInteger("xp", this.xp);
         cmp.setBoolean("marked", this.marked);
         cmp.setInteger("influence", this.influence);
         cmp.setInteger("blockCount", this.blocks.length);

         for(int i = 0; i < this.blocks.length; ++i) {
            cmp.setIntArray("block" + i, this.blocks[i]);
         }

      }

      public void readFromNBT(NBTTagCompound cmp) {
         this.color = cmp.getInteger("color");
         this.type = cmp.getInteger("type");
         this.enchant = Enchantment.getEnchantmentByLocation(cmp.getString("enchant"));
         this.level = cmp.getInteger("level");
         this.x = cmp.getInteger("x");
         this.y = cmp.getInteger("y");
         this.xp = cmp.getInteger("xp");
         this.marked = cmp.getBoolean("marked");
         this.influence = cmp.getInteger("influence");
         this.blocks = new int[cmp.getInteger("blockCount")][2];

         for(int i = 0; i < this.blocks.length; ++i) {
            this.blocks[i] = cmp.getIntArray("block" + i);
         }

      }
   }
}
