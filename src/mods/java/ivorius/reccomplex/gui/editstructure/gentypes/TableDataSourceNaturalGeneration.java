package ivorius.reccomplex.gui.editstructure.gentypes;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.RCGuiTables;
import ivorius.reccomplex.gui.editstructure.TableDataSourceBiomeGenList;
import ivorius.reccomplex.gui.editstructure.TableDataSourceDimensionGenList;
import ivorius.reccomplex.gui.editstructure.TableDataSourceNaturalGenLimitation;
import ivorius.reccomplex.gui.table.TableCells;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.cell.TableCellEnum;
import ivorius.reccomplex.gui.table.cell.TableCellMultiBuilder;
import ivorius.reccomplex.gui.table.cell.TitledCell;
import ivorius.reccomplex.gui.table.datasource.TableDataSourceSegmented;
import ivorius.reccomplex.world.gen.feature.selector.NaturalStructureSelector;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.NaturalGeneration;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

@SideOnly(Side.CLIENT)
public class TableDataSourceNaturalGeneration extends TableDataSourceSegmented {
   private TableNavigator navigator;
   private TableDelegate tableDelegate;
   private NaturalGeneration generationInfo;

   public TableDataSourceNaturalGeneration(TableNavigator navigator, TableDelegate delegate, NaturalGeneration generationInfo) {
      this.navigator = navigator;
      this.tableDelegate = delegate;
      this.generationInfo = generationInfo;
      this.addSegment(0, new TableDataSourceGenerationType(generationInfo, navigator, delegate));
      this.addSegment(1, new Supplier[]{() -> {
         TableCellEnum<String> cell = new TableCellEnum<String>("category", generationInfo.generationCategory, allGenerationCategories());
         cell.addListener((val) -> {
            generationInfo.generationCategory = val;
         });
         return new TitledCell(IvTranslations.get("reccomplex.generationInfo.natural.category"), cell);
      }});
      this.addSegment(2, new Supplier[]{() -> {
         return RCGuiTables.defaultWeightElement((val) -> {
            generationInfo.setGenerationWeight(TableCells.toDouble(val));
         }, generationInfo.getGenerationWeight());
      }});
      this.addSegment(3, TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return new TableDataSourceSelectivePlacer(generationInfo.placer, navigator, delegate);
      }, () -> {
         return IvTranslations.get("reccomplex.placer");
      }, () -> {
         return IvTranslations.getLines("reccomplex.placer.tooltip");
      }).withTitle("").buildDataSource());
      this.addSegment(4, TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return new TableDataSourceBiomeGenList(generationInfo.biomeWeights, delegate, navigator);
      }, () -> {
         return IvTranslations.get("reccomplex.gui.biomes");
      }, () -> {
         return IvTranslations.getLines("reccomplex.gui.biomes.tooltip");
      }).addNavigation(() -> {
         return new TableDataSourceDimensionGenList(generationInfo.dimensionWeights, delegate, navigator);
      }, () -> {
         return IvTranslations.get("reccomplex.gui.dimensions");
      }, () -> {
         return IvTranslations.getLines("reccomplex.gui.dimensions.tooltip");
      }).withTitle("").buildDataSource());
      TableCellMultiBuilder var10002 = TableCellMultiBuilder.create(navigator, delegate).addNavigation(() -> {
         return new TableDataSourceNaturalGenLimitation(generationInfo.spawnLimitation, delegate);
      });
      generationInfo.getClass();
      this.addSegment(5, var10002.enabled(generationInfo::hasLimitations).addAction(() -> {
         generationInfo.spawnLimitation = generationInfo.hasLimitations() ? null : new NaturalGeneration.SpawnLimitation();
      }, () -> {
         return generationInfo.hasLimitations() ? IvTranslations.get("reccomplex.gui.remove") : IvTranslations.get("reccomplex.gui.add");
      }, (Supplier)null).withTitle(IvTranslations.get("reccomplex.generationInfo.natural.limitations")).buildDataSource());
   }

   public static List<TableCellEnum.Option<String>> allGenerationCategories() {
      Set<String> categories = NaturalStructureSelector.CATEGORY_REGISTRY.activeIDs();
      return (List)categories.stream().map((category) -> {
         return Pair.of(category, NaturalStructureSelector.CATEGORY_REGISTRY.getActive(category));
      }).filter((p) -> {
         return ((NaturalStructureSelector.Category)p.getRight()).selectableInGUI();
      }).map((p) -> {
         return new TableCellEnum.Option<TableCellEnum.Option<String>>(p.getLeft(), ((NaturalStructureSelector.Category)p.getRight()).title(), ((NaturalStructureSelector.Category)p.getRight()).tooltip());
      }).sorted(Comparator.comparing((o) -> {
         return o.title;
      })).collect(Collectors.toList());
   }
}
