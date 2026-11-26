package hunternif.mc.atlas.item;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.RegistrarAntiqueAtlas;
import hunternif.mc.atlas.core.AtlasData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.marker.MarkersData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class RecipeAtlasCombining extends RecipeBase {
   public RecipeAtlasCombining() {
      this.register(MinecraftForge.EVENT_BUS);
   }

   public boolean matches(InventoryCrafting inv, World world) {
      return this.matches(inv);
   }

   private boolean matches(IInventory inv) {
      int atlasesFound = 0;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
            ++atlasesFound;
         }
      }

      return atlasesFound > 1;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      ItemStack firstAtlas = ItemStack.EMPTY;
      List<Integer> atlasIds = new ArrayList(9);

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (!stack.isEmpty() && stack.getItem() == RegistrarAntiqueAtlas.ATLAS) {
            if (firstAtlas.isEmpty()) {
               firstAtlas = stack;
            } else {
               atlasIds.add(Integer.valueOf(stack.getItemDamage()));
            }
         }
      }

      return atlasIds.size() < 1 ? ItemStack.EMPTY : firstAtlas.copy();
   }

   public boolean canFit(int width, int height) {
      return true;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   public void register(EventBus bus) {
      bus.register(ItemCraftedEvent.class, this::onCrafted);
   }

   public void onCrafted(ItemCraftedEvent event) {
      if (event.crafting.getItem() == RegistrarAntiqueAtlas.ATLAS && this.matches(event.craftMatrix)) {
         World world = event.player.getEntityWorld();
         if (!world.isRemote) {
            int atlasID = world.getUniqueDataId("aAtlas");
            AtlasData destBiomes = AntiqueAtlasMod.atlasData.getAtlasData(atlasID, world);
            destBiomes.markDirty();
            MarkersData destMarkers = AntiqueAtlasMod.markersData.getMarkersData(atlasID, world);
            destMarkers.markDirty();

            for(int i = 0; i < event.craftMatrix.getSizeInventory(); ++i) {
               ItemStack stack = event.craftMatrix.getStackInSlot(i);
               if (!stack.isEmpty()) {
                  AtlasData srcBiomes = AntiqueAtlasMod.atlasData.getAtlasData(stack, world);
                  if (destBiomes != null && srcBiomes != null && destBiomes != srcBiomes) {
                     Iterator srcMarkers = srcBiomes.getVisitedDimensions().iterator();

                     while(srcMarkers.hasNext()) {
                        int dim = ((Integer)srcMarkers.next()).intValue();
                        destBiomes.getDimensionData(dim).addData(srcBiomes.getDimensionData(dim));
                     }
                  }

                  MarkersData srcMarkers = AntiqueAtlasMod.markersData.getMarkersData(stack, world);
                  if (destMarkers != null && srcMarkers != null && destMarkers != srcMarkers) {
                     Iterator var15 = srcMarkers.getVisitedDimensions().iterator();

                     while(var15.hasNext()) {
                        int dim = ((Integer)var15.next()).intValue();

                        for(Marker marker : srcMarkers.getMarkersDataInDimension(dim).getAllMarkers()) {
                           destMarkers.createAndSaveMarker(marker.getType(), marker.getLabel(), dim, marker.getX(), marker.getZ(), marker.isVisibleAhead());
                        }
                     }
                  }
               }
            }

            event.crafting.setItemDamage(atlasID);
         }
      }
   }
}
