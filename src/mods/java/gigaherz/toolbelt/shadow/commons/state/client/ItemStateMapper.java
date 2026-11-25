package gigaherz.toolbelt.shadow.commons.state.client;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import gigaherz.toolbelt.shadow.commons.state.IItemState;
import gigaherz.toolbelt.shadow.commons.state.IItemStateManager;
import gigaherz.toolbelt.shadow.commons.state.StatefulItem;
import java.util.Map;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

public class ItemStateMapper implements ItemMeshDefinition {
   public final Map<StatefulItem, ItemStateMapper> STATE_MAPPERS = Maps.newHashMap();
   final StatefulItem item;

   public <T extends Item & StatefulItem> ItemStateMapper(T item) {
      this.item = (StatefulItem)item;
   }

   public void registerAllModelsExplicitly() {
      IItemStateManager manager = this.item.getStateManager();
      UnmodifiableIterator var2 = manager.getStateTable().iterator();

      while(var2.hasNext()) {
         IItemState state = (IItemState)var2.next();
         ModelLoader.setCustomModelResourceLocation((Item)this.item, state.getMetadata(), this.getModelLocation(state));
      }

   }

   private ModelResourceLocation getModelLocation(IItemState state) {
      return new ModelResourceLocation(((Item)this.item).getRegistryName(), state.toString());
   }

   public ModelResourceLocation getModelLocation(ItemStack stack) {
      if (stack.getItem() != this.item) {
         throw new IllegalArgumentException("The stack's item is not the expected item!");
      } else {
         IItemState state = IItemStateManager.lookup(stack);
         if (state == null) {
            state = this.item.getDefaultState();
         }

         return this.getModelLocation(state);
      }
   }
}
