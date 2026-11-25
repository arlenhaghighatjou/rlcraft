package ivorius.reccomplex.world.gen.feature.structure.generic.generation;

import com.google.gson.JsonObject;
import ivorius.reccomplex.client.rendering.MazeVisualizationContext;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.world.gen.feature.structure.Placer;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import java.util.Random;
import java.util.Stack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class GenerationType {
   public static Stack<Random> idRandomizers = new Stack();
   @Nonnull
   protected String id;

   public GenerationType(@Nonnull String id) {
      this.id = id;
   }

   public static String randomID(Class<? extends GenerationType> type) {
      Random random = new Random();
      return String.format("%s_%s", StructureRegistry.GENERATION_TYPES.iDForType(type), Integer.toHexString(random.nextInt()));
   }

   public static String randomID(String type) {
      Random random = new Random();
      return String.format("%s_%s", type, Integer.toHexString(random.nextInt()));
   }

   public static String readID(JsonObject object) {
      String id = JsonUtils.getString(object, "id", (String)null);
      if (id == null || id.length() == 0) {
         id = Integer.toHexString(((Random)idRandomizers.peek()).nextInt());
      }

      return id;
   }

   @Nonnull
   public String id() {
      return this.id;
   }

   public void setID(@Nonnull String id) {
      this.id = id;
   }

   public abstract String displayString();

   @Nullable
   public abstract Placer placer();

   @SideOnly(Side.CLIENT)
   public abstract TableDataSource tableDataSource(MazeVisualizationContext var1, TableNavigator var2, TableDelegate var3);

   static {
      idRandomizers.push(new Random(-559038737L));
   }
}
