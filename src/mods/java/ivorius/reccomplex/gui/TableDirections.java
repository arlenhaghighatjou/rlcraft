package ivorius.reccomplex.gui;

import ivorius.reccomplex.gui.table.cell.TableCellEnum;
import ivorius.reccomplex.utils.DirectionNames;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDirections {
   public TableDirections() {
   }

   public static TableCellEnum.Option<EnumFacing>[] getDirectionOptions(EnumFacing[] directions) {
      TableCellEnum.Option<EnumFacing>[] options = new TableCellEnum.Option[directions.length];

      for(int i = 0; i < options.length; ++i) {
         options[i] = new TableCellEnum.Option(directions[i], DirectionNames.of(directions[i]));
      }

      return options;
   }

   public static TableCellEnum.Option<EnumFacing>[] getDirectionOptions(EnumFacing[] directions, String nullTitle) {
      TableCellEnum.Option<EnumFacing>[] options = new TableCellEnum.Option[directions.length];

      for(int i = 0; i < options.length; ++i) {
         options[i] = new TableCellEnum.Option(directions[i], DirectionNames.of(directions[i], nullTitle));
      }

      return options;
   }
}
