package invtweaks.forge;

import invtweaks.InvTweaks;
import invtweaks.InvTweaksConfigManager;
import invtweaks.api.IItemTreeCategory;
import invtweaks.api.IItemTreeItem;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(
   value = {Side.CLIENT},
   modid = "inventorytweaks"
)
public class ToolTipEvent {
   public ToolTipEvent() {
   }

   @SubscribeEvent
   public static void tTipEvent(ItemTooltipEvent event) {
      ItemStack current = event.getItemStack();
      if (!current.isEmpty()) {
         InvTweaksConfigManager cfgManager = InvTweaks.getConfigManager();
         if (cfgManager != null) {
            if (cfgManager.getConfig().getProperty("enableToolTipTreePath").equals("true")) {
               List<IItemTreeItem> items = cfgManager.getConfig().getTree().getItems(current.getItem().getRegistryName().toString(), current.getItemDamage(), current.getTagCompound());
               if (items.isEmpty()) {
                  return;
               }

               Set<String> paths = new HashSet();
               IItemTreeCategory root = cfgManager.getConfig().getTree().getRootCategory();
               int unsortedZone = cfgManager.getConfig().getTree().getLastTreeOrder();
               int minOrder = Integer.MAX_VALUE;

               for(IItemTreeItem item : items) {
                  String path = item.getPath();
                  String altPath = root.findKeywordPath(item.getName());
                  int itemOrder = item.getOrder();
                  minOrder = Integer.min(minOrder, itemOrder);
                  if (minOrder <= unsortedZone && itemOrder > unsortedZone) {
                     break;
                  }

                  if (!path.equals(altPath) && itemOrder <= unsortedZone) {
                     if (!paths.contains(path)) {
                        event.getToolTip().add(TextFormatting.DARK_GRAY + "T:" + path + " (" + item.getOrder() + ")");
                        paths.add(path);
                     }

                     if (!paths.contains(altPath)) {
                        event.getToolTip().add(TextFormatting.DARK_GRAY + "M:" + altPath + " (" + item.getOrder() + ")");
                        paths.add(altPath);
                     }
                  } else if (!paths.contains(path)) {
                     event.getToolTip().add(TextFormatting.DARK_GRAY + path + " (" + item.getOrder() + ")");
                     paths.add(path);
                  }
               }
            }

         }
      }
   }
}
