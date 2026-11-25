package ivorius.reccomplex.gui.table.datasource;

import java.util.Arrays;
import java.util.List;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceMulti extends TableDataSourceSegmented {
   public TableDataSourceMulti(List<TableDataSource> sources) {
      for(int i = 0; i < sources.size(); ++i) {
         this.addSegment(i, (TableDataSource)sources.get(i));
      }

   }

   public TableDataSourceMulti(TableDataSource... sources) {
      this(Arrays.asList(sources));
   }
}
