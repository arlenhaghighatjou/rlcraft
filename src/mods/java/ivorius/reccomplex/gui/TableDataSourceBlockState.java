package ivorius.reccomplex.gui;

import com.google.common.collect.Lists;
import ivorius.ivtoolkit.blocks.BlockStates;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.gui.editstructure.transformers.TableDataSourceBTNatural;
import ivorius.reccomplex.gui.table.GuiTable;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCell;
import ivorius.reccomplex.gui.table.cell.TableCellButton;
import ivorius.reccomplex.gui.table.cell.TableCellEnum;
import ivorius.reccomplex.gui.table.cell.TableCellIntSlider;
import ivorius.reccomplex.gui.table.cell.TableCellMulti;
import ivorius.reccomplex.gui.table.cell.TableCellString;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableDataSourceBlockState extends TableDataSourceSegmented {
   private String block;
   private int meta;
   private Consumer<IBlockState> consumer;
   private String blockTitle;
   private String metadataTitle;
   private TableCellString idCell;
   private TableCellIntSlider metaCell;
   private TableNavigator navigator;
   private TableDelegate delegate;
   private boolean showExtendedProperties;

   public TableDataSourceBlockState(IBlockState state, Consumer<IBlockState> consumer, TableNavigator navigator, TableDelegate delegate, String blockTitle, String metadataTitle) {
      this.block = "";
      this.setBlockState(state);
      this.consumer = consumer;
      this.blockTitle = blockTitle;
      this.metadataTitle = metadataTitle;
      this.navigator = navigator;
      this.delegate = delegate;
      this.addSegment(0, new Supplier[]{() -> {
         TableCellString cell = this.idCell = TableDataSourceBTNatural.cellForBlock("block", this.block);
         cell.addListener((p) -> {
            this.valueChanged();
         });
         return (void)(new TitledCell("blockID", blockTitle, cell));
      }, () -> {
         TableCellIntSlider cell = this.metaCell = new TableCellIntSlider("metadata", this.meta, 0, 15);
         cell.addListener((p) -> {
            this.valueChanged();
         });
         return (void)(new TitledCell("blockMeta", metadataTitle, cell));
      }});
   }

   public TableDataSourceBlockState(IBlockState state, Consumer<IBlockState> consumer, TableNavigator navigator, TableDelegate delegate) {
      this(state, consumer, navigator, delegate, IvTranslations.get("reccomplex.gui.block"), IvTranslations.get("reccomplex.gui.metadata"));
   }

   public boolean isShowExtendedProperties() {
      return this.showExtendedProperties;
   }

   public TableDataSourceBlockState setShowExtendedProperties(boolean showExtendedProperties) {
      this.showExtendedProperties = showExtendedProperties;
      return this;
   }

   public int numberOfSegments() {
      return this.showExtendedProperties ? 3 : 2;
   }

   public int sizeOfSegment(int segment) {
      switch(segment) {
      case 1:
         IBlockState state = this.computeBlockState();
         return state != null ? this.getSortedPropertyNames(state, false).size() : 0;
      case 2:
         IBlockState state = this.computeBlockState();
         return state != null ? this.getSortedPropertyNames(state, true).size() : 0;
      default:
         return super.sizeOfSegment(segment);
      }
   }

   public TableCell cellForIndexInSegment(GuiTable table, int index, int segment) {
      switch(segment) {
      case 1:
         return this.getPropertyElement(index, false);
      case 2:
         return this.getPropertyElement(index, true);
      default:
         return super.cellForIndexInSegment(table, index, segment);
      }
   }

   @Nonnull
   protected <T extends Comparable<T>> TitledCell getPropertyElement(int index, boolean extended) {
      IBlockState state = this.computeBlockState();
      IProperty<T> name = (IProperty)this.getSortedPropertyNames(state, extended).get(index);
      List<T> properties = this.<T>getSortedProperties(name);
      T currentProperty = state.getValue(name);
      if (properties.size() <= 4) {
         List<TableCellButton> buttons = (List)properties.stream().map((property) -> {
            TableCellButton button = new TableCellButton((String)null, (String)null, name.getName(property));
            button.setEnabled(!extended);
            button.addAction(() -> {
               this.setBlockStateAndNotify(state.withProperty(name, property));
               this.delegate.reloadData();
            });
            if (property == currentProperty) {
               button.setEnabled(false);
            }

            return button;
         }).collect(Collectors.toList());
         return new TitledCell(name.getName(), new TableCellMulti(buttons));
      } else {
         List<T> sorted = Lists.newArrayList(name.getAllowedValues());
         Collections.sort(sorted);
         TableCellEnum<T> cell = new TableCellEnum<T>((String)null, state.getValue(name), (List)sorted.stream().map((t1) -> {
            return new TableCellEnum.Option(t1, name.getName(currentProperty));
         }).collect(Collectors.toList()));
         cell.addListener((t) -> {
            this.setBlockStateAndNotify(state.withProperty(name, t));
            this.delegate.reloadData();
         });
         cell.setEnabled(!extended);
         return new TitledCell(name.getName(), cell);
      }
   }

   protected <T extends Comparable<T>> List<T> getSortedProperties(IProperty<T> name) {
      List<T> values = Lists.newArrayList(name.getAllowedValues());
      Collections.sort(values);
      return values;
   }

   @Nonnull
   protected List<IProperty<?>> getSortedPropertyNames(IBlockState state, boolean extended) {
      List<IProperty<?>> names = Lists.newArrayList(state.getPropertyKeys());
      names.removeIf((name) -> {
         return name.getAllowedValues().stream().mapToInt((obj) -> {
            return BlockStates.toMetadata(state.withProperty(name, obj));
         }).distinct().count() < 2L != extended;
      });
      names.sort(Comparator.comparing(IProperty::getName));
      return names;
   }

   public void valueChanged() {
      IBlockState before = this.computeBlockState();
      this.block = (String)this.idCell.getPropertyValue();
      this.meta = ((Integer)this.metaCell.getPropertyValue()).intValue();
      TableDataSourceBTNatural.setStateForBlockTextfield(this.idCell);
      IBlockState state;
      this.setBlockStateAndNotify(state = this.computeBlockState());
      if (state != before) {
         TableCells.reloadExcept(this.delegate, "blockID", "blockMeta");
      }

   }

   protected void setBlockStateAndNotify(IBlockState c) {
      if (c != null) {
         this.consumer.accept(c);
         this.setBlockState(c);
      }

   }

   public void setBlockState(IBlockState state) {
      this.block = ((ResourceLocation)Block.REGISTRY.getNameForObject(state.getBlock())).toString();
      this.meta = BlockStates.toMetadata(state);
   }

   public IBlockState computeBlockState() {
      try {
         Block block = RecurrentComplex.mcRegistry.blockFromID(new ResourceLocation(this.block));
         return block != null ? BlockStates.fromMetadata(block, this.meta) : null;
      } catch (Exception var2) {
         return null;
      }
   }
}
