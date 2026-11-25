package ivorius.reccomplex.gui.table.cell;

import com.google.common.collect.Lists;
import ivorius.ivtoolkit.tools.IvGsonHelper;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.table.Bounds;
import ivorius.reccomplex.gui.table.GuiTable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TableCellEnum<T> extends TableCellPropertyDefault<T> {
   public static final String LEFT_ARROW = TextFormatting.BOLD + "\u2190";
   public static final String RIGHT_ARROW = TextFormatting.BOLD + "\u2192";
   protected GuiButton leftButton;
   protected GuiButton rightButton;
   private List<TableCellEnum.Option<T>> options;

   public TableCellEnum(String id, T value, List<TableCellEnum.Option<T>> options) {
      super(id, value);
      this.options = Lists.newArrayList(options);
   }

   @SafeVarargs
   public TableCellEnum(String id, T value, TableCellEnum.Option<T>... options) {
      this(id, value, Arrays.asList(options));
   }

   public static <T extends Enum> List<TableCellEnum.Option<T>> options(T[] values, String baseKey, boolean tooltip) {
      return options(Arrays.asList(values), baseKey, tooltip);
   }

   public static <T> List<TableCellEnum.Option<T>> options(T[] values, Function<T, String> titleFunc, Function<T, List<String>> tooltipFunc) {
      return options(Arrays.asList(values), titleFunc, tooltipFunc);
   }

   public static <T extends Enum> List<TableCellEnum.Option<T>> options(List<T> values, String baseKey, boolean tooltip) {
      return options(values, (input) -> {
         return IvTranslations.get(baseKey + IvGsonHelper.serializedName(input));
      }, tooltip ? (input) -> {
         return IvTranslations.getLines(baseKey + IvGsonHelper.serializedName(input) + ".tooltip");
      } : null);
   }

   public static <T> List<TableCellEnum.Option<T>> options(List<T> values, Function<T, String> titleFunc, Function<T, List<String>> tooltipFunc) {
      return (List)values.stream().map((input) -> {
         return new TableCellEnum.Option<TableCellEnum.Option<T>>(input, titleFunc != null ? (String)titleFunc.apply(input) : null, tooltipFunc != null ? (List)tooltipFunc.apply(input) : null);
      }).sorted(Comparator.comparing((o) -> {
         return o.title;
      })).collect(Collectors.toList());
   }

   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      if (this.leftButton != null) {
         this.leftButton.enabled = enabled;
      }

      if (this.rightButton != null) {
         this.rightButton.enabled = enabled;
      }

   }

   public void initGui(GuiTable screen) {
      super.initGui(screen);
      Bounds bounds = this.bounds();
      int buttonY = bounds.getMinY() + (bounds.getHeight() - 20) / 2;
      int presetButtonWidth = bounds.getWidth() - 40;
      boolean canChange = (this.options.size() > 1 || this.options.size() == 1 && !Objects.equals(this.getPropertyValue(), ((TableCellEnum.Option)this.options.get(0)).value)) && this.enabled;
      this.leftButton = new GuiButton(-1, bounds.getMinX(), buttonY, 19, 20, LEFT_ARROW);
      this.leftButton.visible = !this.isHidden();
      this.leftButton.enabled = canChange;
      screen.addButton(this, 0, this.leftButton);
      this.rightButton = new GuiButton(-1, bounds.getMinX() + 20 + presetButtonWidth + 1, buttonY, 19, 20, RIGHT_ARROW);
      this.rightButton.visible = !this.isHidden();
      this.rightButton.enabled = canChange;
      screen.addButton(this, 1, this.rightButton);
   }

   public void setHidden(boolean hidden) {
      super.setHidden(hidden);
      if (this.leftButton != null) {
         this.leftButton.visible = !hidden;
      }

      if (this.rightButton != null) {
         this.rightButton.visible = !hidden;
      }

   }

   public void buttonClicked(int buttonID) {
      super.buttonClicked(buttonID);
      if (buttonID == 0 || buttonID == 1) {
         this.move(buttonID == 0 ? -1 : 1);
      }

   }

   public void move(int plus) {
      this.setPropertyValue(((TableCellEnum.Option)this.options.get(((this.findIndex(this.getPropertyValue()) + plus) % this.options.size() + this.options.size()) % this.options.size())).value);
      this.alertListenersOfChange();
   }

   public void draw(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      FontRenderer fontRenderer = this.getFontRenderer();
      Bounds bounds = this.bounds();
      TableCellEnum.Option<T> cOption = this.currentOption();
      String option = cOption != null ? cOption.title : this.getPropertyValue().toString();
      int width = fontRenderer.getStringWidth(option);
      fontRenderer.drawString(option, (float)(bounds.getCenterX() - width / 2), (float)(bounds.getCenterY() - 4), -1, true);
      super.draw(screen, mouseX, mouseY, partialTicks);
   }

   public void drawFloating(GuiTable screen, int mouseX, int mouseY, float partialTicks) {
      super.drawFloating(screen, mouseX, mouseY, partialTicks);
      TableCellEnum.Option<T> option = this.currentOption();
      if (option != null && option.tooltip != null) {
         Bounds bounds = this.bounds();
         screen.drawTooltipRect(option.tooltip, Bounds.fromSize(bounds.getMinX() + 20, bounds.getMinY(), bounds.getWidth() - 40, bounds.getHeight()), mouseX, mouseY, this.getFontRenderer());
      }

   }

   private TableCellEnum.Option<T> currentOption() {
      int index = this.findIndex(this.getPropertyValue());
      return index >= 0 ? (TableCellEnum.Option)this.options.get(index) : null;
   }

   protected int findIndex(T option) {
      for(int i = 0; i < this.options.size(); ++i) {
         if (Objects.equals(((TableCellEnum.Option)this.options.get(i)).value, option)) {
            return i;
         }
      }

      return -1;
   }

   public static class Option<T> {
      public T value;
      public String title;
      public List<String> tooltip;

      public Option(T value, String title) {
         this.value = value;
         this.title = title;
      }

      public Option(T value, String title, List<String> tooltip) {
         this.value = value;
         this.title = title;
         this.tooltip = tooltip;
      }
   }
}
