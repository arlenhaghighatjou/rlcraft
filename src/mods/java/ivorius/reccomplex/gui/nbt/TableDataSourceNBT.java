package ivorius.reccomplex.gui.nbt;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.GuiValidityStateIndicator;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellDefault;
import ivorius.reccomplex.gui.table.cell.TableCellIntTextField;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TableCellStringDouble;
import ivorius.reccomplex.gui.table.cell.TableCellStringLong;
import ivorius.reccomplex.gui.table.cell.TableCellTitle;
import ivorius.reccomplex.json.NBTTagEndSerializer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

@SideOnly(Side.CLIENT)
public class TableDataSourceNBT {
   private static final Object[] TYPE_LOOKUP = new Object[]{"End", Integer.valueOf(0), "Byte", Integer.valueOf(1), "Short", Integer.valueOf(2), "Int", Integer.valueOf(3), "Long", Integer.valueOf(4), "Float", Integer.valueOf(5), "Double", Integer.valueOf(6), "Bytes", Integer.valueOf(7), "String", Integer.valueOf(8), "List", Integer.valueOf(9), "Compound", Integer.valueOf(10), "Ints", Integer.valueOf(11)};

   public TableDataSourceNBT() {
   }

   public static TableCell cell(@Nullable NBTBase nbt, @Nonnull TableDelegate delegate, @Nonnull TableNavigator navigator) {
      TableCellDefault cell = rawCell(nbt, delegate, navigator);
      cell.setTooltip(Collections.singletonList(nbt != null ? (String)TYPE_LOOKUP[ArrayUtils.indexOf(TYPE_LOOKUP, Integer.valueOf(nbt.getId())) - 1] : "null"));
      return cell;
   }

   @Nonnull
   public static TableCellDefault rawCell(@Nullable NBTBase nbt, @Nonnull TableDelegate delegate, @Nonnull TableNavigator navigator) {
      if (nbt == null) {
         return new TableCellTitle((String)null, "null");
      } else if (nbt instanceof NBTTagEnd) {
         return new TableCellTitle((String)null, "-");
      } else if (nbt instanceof NBTTagByte) {
         NBTTagByte cNBT = (NBTTagByte)nbt;
         TableCellIntTextField cell = new TableCellIntTextField((String)null, cNBT.getInt());
         cell.addListener((value) -> {
            if (value.intValue() >= -128 && value.intValue() <= 127) {
               ReflectionHelper.setPrivateValue(NBTTagByte.class, cNBT, Byte.valueOf((byte)value.intValue()), "data", "data");
               cell.setValidityState(GuiValidityStateIndicator.State.VALID);
            } else {
               cell.setValidityState(GuiValidityStateIndicator.State.INVALID);
            }

         });
         return cell;
      } else if (nbt instanceof NBTTagShort) {
         NBTTagShort cNBT = (NBTTagShort)nbt;
         TableCellIntTextField cell = new TableCellIntTextField((String)null, cNBT.getInt());
         cell.addListener((value) -> {
            if (value.intValue() >= -32768 && value.intValue() <= 32767) {
               ReflectionHelper.setPrivateValue(NBTTagShort.class, cNBT, Short.valueOf((short)value.intValue()), "data", "data");
               cell.setValidityState(GuiValidityStateIndicator.State.VALID);
            } else {
               cell.setValidityState(GuiValidityStateIndicator.State.INVALID);
            }

         });
         return cell;
      } else if (nbt instanceof NBTTagInt) {
         NBTTagInt cNBT = (NBTTagInt)nbt;
         TableCellIntTextField cell = new TableCellIntTextField((String)null, cNBT.getInt());
         cell.addListener((value) -> {
            ReflectionHelper.setPrivateValue(NBTTagInt.class, cNBT, value, "data", "data");
         });
         return cell;
      } else if (nbt instanceof NBTTagLong) {
         NBTTagLong cNBT = (NBTTagLong)nbt;
         TableCellStringLong cell = new TableCellStringLong((String)null, cNBT.getLong());
         cell.addListener((value) -> {
            ReflectionHelper.setPrivateValue(NBTTagLong.class, cNBT, value, "data", "data");
         });
         return cell;
      } else if (nbt instanceof NBTTagFloat) {
         NBTTagFloat cNBT = (NBTTagFloat)nbt;
         TableCellStringDouble cell = new TableCellStringDouble((String)null, cNBT.getDouble());
         cell.addListener((value) -> {
            ReflectionHelper.setPrivateValue(NBTTagFloat.class, cNBT, Float.valueOf((float)value.doubleValue()), "data", "data");
         });
         return cell;
      } else if (nbt instanceof NBTTagDouble) {
         NBTTagDouble cNBT = (NBTTagDouble)nbt;
         TableCellStringDouble cell = new TableCellStringDouble((String)null, cNBT.getDouble());
         cell.addListener((value) -> {
            ReflectionHelper.setPrivateValue(NBTTagDouble.class, cNBT, value, "data", "data");
         });
         return cell;
      } else if (nbt instanceof NBTTagByteArray) {
         NBTTagByteArray cNBT = (NBTTagByteArray)nbt;
         Stream var21 = Arrays.stream(ArrayUtils.toObject(cNBT.getByteArray())).map(String::valueOf);
         var21.getClass();
         TableCellString cell = new TableCellString((String)null, String.join(",", var21::iterator));
         cell.setShowsValidityState(true);
         cell.addListener((value) -> {
            byte[] bytes = parseBytes(value);
            if (bytes == null) {
               cell.setValidityState(GuiValidityStateIndicator.State.INVALID);
            } else {
               cell.setValidityState(GuiValidityStateIndicator.State.VALID);
               ReflectionHelper.setPrivateValue(NBTTagByteArray.class, cNBT, bytes, "data", "data");
            }

         });
         return cell;
      } else if (nbt instanceof NBTTagString) {
         NBTTagString cNBT = (NBTTagString)nbt;
         TableCellString cell = new TableCellString((String)null, cNBT.getString());
         cell.addListener((value) -> {
            ReflectionHelper.setPrivateValue(NBTTagString.class, cNBT, value, "data", "data");
         });
         return cell;
      } else if (nbt instanceof NBTTagList) {
         return TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
            return new TableDataSourceNBTList(delegate, navigator, (NBTTagList)nbt);
         }).build();
      } else if (nbt instanceof NBTTagCompound) {
         return TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
            return new TableDataSourceNBTTagCompound(delegate, navigator, (NBTTagCompound)nbt);
         }).build();
      } else if (nbt instanceof NBTTagIntArray) {
         NBTTagIntArray cNBT = (NBTTagIntArray)nbt;
         Stream var10004 = Arrays.stream(ArrayUtils.toObject(cNBT.getIntArray())).map(String::valueOf);
         var10004.getClass();
         TableCellString cell = new TableCellString((String)null, String.join(",", var10004::iterator));
         cell.setShowsValidityState(true);
         cell.addListener((value) -> {
            int[] ints = parseInts(value);
            if (ints == null) {
               cell.setValidityState(GuiValidityStateIndicator.State.INVALID);
            } else {
               cell.setValidityState(GuiValidityStateIndicator.State.VALID);
               ReflectionHelper.setPrivateValue(NBTTagIntArray.class, cNBT, ints, "intArray", "intArray");
            }

         });
         return cell;
      } else {
         RecurrentComplex.logger.error("Unexpected nbt type: " + nbt.getClass());
         throw new InternalError();
      }
   }

   private static byte[] parseBytes(String string) {
      if (string.isEmpty()) {
         return new byte[0];
      } else {
         String[] strings = string.split(",");
         byte[] bytes = new byte[strings.length];

         for(int i = 0; i < strings.length; ++i) {
            try {
               bytes[i] = Byte.parseByte(strings[i]);
            } catch (NumberFormatException var5) {
               return null;
            }
         }

         return bytes;
      }
   }

   private static int[] parseInts(String string) {
      if (string.isEmpty()) {
         return new int[0];
      } else {
         String[] strings = string.split(",");
         int[] ints = new int[strings.length];

         for(int i = 0; i < strings.length; ++i) {
            try {
               ints[i] = Integer.parseInt(strings[i]);
            } catch (NumberFormatException var5) {
               return null;
            }
         }

         return ints;
      }
   }

   @Nonnull
   public static TableCellButton addButton(int id, Consumer<NBTBase> consumer) {
      TableCellButton button = TableCells.add(true, (String)TYPE_LOOKUP[id * 2], (String)TYPE_LOOKUP[id * 2], (List)null);
      button.addAction(() -> {
         consumer.accept(typeSupplier(id).get());
      });
      return button;
   }

   public static Supplier<NBTBase> typeSupplier(int id) {
      switch(id) {
      case 0:
         return NBTTagEndSerializer::createNBTTagEnd;
      case 1:
         return () -> {
            return new NBTTagByte((byte)0);
         };
      case 2:
         return () -> {
            return new NBTTagShort((short)0);
         };
      case 3:
         return () -> {
            return new NBTTagInt(0);
         };
      case 4:
         return () -> {
            return new NBTTagLong(0L);
         };
      case 5:
         return () -> {
            return new NBTTagFloat(0.0F);
         };
      case 6:
         return () -> {
            return new NBTTagDouble(0.0D);
         };
      case 7:
         return () -> {
            return new NBTTagByteArray(new byte[0]);
         };
      case 8:
         return NBTTagString::new;
      case 9:
         return NBTTagList::new;
      case 10:
         return NBTTagCompound::new;
      case 11:
         return () -> {
            return new NBTTagIntArray(new int[0]);
         };
      default:
         throw new IllegalStateException();
      }
   }

   @Nonnull
   public static TableCellButton typeButton(int id, Runnable runnable) {
      TableCellButton button = TableCells.add(true, (String)TYPE_LOOKUP[id * 2], "Set to " + (String)TYPE_LOOKUP[id * 2], (List)null);
      button.addAction(runnable);
      return button;
   }
}
