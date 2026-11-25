package knightminer.inspirations.tools;

import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.tools.client.RenderModArrow;
import knightminer.inspirations.tools.entity.EntityModArrow;
import knightminer.inspirations.tools.item.ItemModArrow;
import knightminer.inspirations.tools.item.ItemWaypointCompass;
import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ColorHandlerEvent.Item;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ToolsClientProxy extends ClientProxy {
   public ToolsClientProxy() {
   }

   public void preInit() {
      super.preInit();
      RenderingRegistry.registerEntityRenderingHandler(EntityModArrow.class, RenderModArrow::new);
   }

   @SubscribeEvent
   public void registerModels(ModelRegistryEvent event) {
      setModelStateMapper(InspirationsTools.redstoneCharge, (new Builder()).ignore(new IProperty[]{BlockRedstoneCharge.FACING, BlockRedstoneCharge.QUICK}).build());
      registerItemModel(InspirationsTools.redstoneCharger);
      registerItemModel(InspirationsTools.woodenCrook);
      registerItemModel(InspirationsTools.stoneCrook);
      registerItemModel(InspirationsTools.boneCrook);
      registerItemModel(InspirationsTools.blazeCrook);
      registerItemModel(InspirationsTools.witherCrook);
      registerItemModel(InspirationsTools.northCompass);
      registerItemModel(InspirationsTools.barometer);
      registerItemModel(InspirationsTools.photometer);
      registerItemModel(InspirationsTools.waypointCompass);

      for(ItemModArrow.ArrowType type : ItemModArrow.ArrowType.values()) {
         registerItemModel(InspirationsTools.arrow, type.getMeta(), type.getName());
      }

   }

   @SubscribeEvent
   public void registerItemColors(Item event) {
      ItemColors itemColors = event.getItemColors();
      registerItemColors(itemColors, (stack, tintIndex) -> {
         EnumDyeColor color = EnumDyeColor.byMetadata(stack.getMetadata());
         if (tintIndex == 0) {
            if (color == EnumDyeColor.BLACK) {
               return 4473924;
            } else {
               return color == EnumDyeColor.WHITE ? 14540253 : color.colorValue;
            }
         } else {
            return tintIndex == 1 ? ItemWaypointCompass.getNeedleColor(color) : -1;
         }
      }, new net.minecraft.item.Item[]{InspirationsTools.waypointCompass});
   }

   @SubscribeEvent
   public static void fixShieldTooltip(ItemTooltipEvent event) {
      if (Config.fixShieldTooltip) {
         ItemStack stack = event.getItemStack();
         if (stack.getItem() == Items.SHIELD) {
            NBTTagCompound tags = stack.getSubCompound("BlockEntityTag");
            if (tags != null && tags.hasKey("Patterns") && stack.isItemEnchanted()) {
               NBTTagList patterns = tags.getTagList("Patterns", 10);
               event.getToolTip().add(patterns.tagCount() + 1, "");
            }

         }
      }
   }
}
