package org.orecruncher.dsurround.client.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilityEntityData;
import org.orecruncher.dsurround.capabilities.entitydata.IEntityData;
import org.orecruncher.dsurround.client.fx.BlockEffect;
import org.orecruncher.dsurround.client.handlers.EffectManager;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.handlers.FxHandler;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.blockstate.BlockStateMatcher;
import org.orecruncher.dsurround.registry.blockstate.BlockStateUtil;
import org.orecruncher.dsurround.registry.footstep.BlockMap;
import org.orecruncher.lib.ItemStackUtil;
import org.orecruncher.lib.MCHelper;
import org.orecruncher.lib.WorldUtils;
import org.orecruncher.lib.gui.TextPanel;
import org.orecruncher.lib.gui.Panel.Reference;

@SideOnly(Side.CLIENT)
public class InspectionHUD extends Gui implements IGuiOverlay {
   private static final String TEXT_BLOCKSTATE = TextFormatting.DARK_PURPLE + "<BlockState>";
   private static final String TEXT_FOOTSTEP_ACOUSTICS = TextFormatting.DARK_PURPLE + "<Footstep Accoustics>";
   private static final String TEXT_BLOCK_EFFECTS = TextFormatting.DARK_PURPLE + "<Block Effects>";
   private static final String TEXT_ALWAYS_ON_EFFECTS = TextFormatting.DARK_PURPLE + "<Always On Effects>";
   private static final String TEXT_BLOCK_SOUNDS = TextFormatting.DARK_PURPLE + "<Block Sounds>";
   private static final String TEXT_DICTIONARY_NAMES = TextFormatting.DARK_PURPLE + "<Dictionary Names>";
   private final TextPanel textPanel = new TextPanel();

   private static List<String> gatherOreNames(ItemStack stack) {
      List<String> result = new ArrayList();
      if (ItemStackUtil.isValidItemStack(stack)) {
         for(int i : OreDictionary.getOreIDs(stack)) {
            result.add(OreDictionary.getOreName(i));
         }
      }

      return result;
   }

   private void gatherBlockText(ItemStack stack, List<String> text, IBlockState state, BlockPos pos) {
      if (ItemStackUtil.isValidItemStack(stack)) {
         text.add(TextFormatting.RED + stack.getDisplayName());
         String itemName = ItemStackUtil.getItemName(stack);
         if (itemName != null) {
            text.add("ITEM: " + itemName);
            text.add(TextFormatting.DARK_AQUA + stack.getItem().getClass().getName());
         }
      }

      if (state != null) {
         BlockStateMatcher info = BlockStateMatcher.create(state);
         text.add("BLOCK: " + info.toString());
         text.add(TextFormatting.DARK_AQUA + info.getBlock().getClass().getName());
         text.add("Material: " + MCHelper.getMaterialName(state.getMaterial()));
         SoundType st = state.getBlock().getSoundType(state, EnvironStateHandler.EnvironState.getWorld(), pos, EnvironStateHandler.EnvironState.getPlayer());
         text.add("Step Sound: " + st.getStepSound().getSoundName().toString());
         if (RegistryManager.FOOTSTEPS.hasFootprint(state)) {
            text.add("Footprints Generated");
         }

         text.add(TEXT_BLOCKSTATE);
         NBTTagCompound nbt = new NBTTagCompound();
         NBTUtil.writeBlockState(nbt, state);
         text.add(nbt.toString());
         BlockMap bm = RegistryManager.FOOTSTEPS.getBlockMap();
         List<String> data = new ArrayList();
         bm.collectData(state, data);
         if (data.size() > 0) {
            text.add(TEXT_FOOTSTEP_ACOUSTICS);

            for(String s : data) {
               text.add(TextFormatting.GOLD + s);
            }
         }

         data = BlockStateUtil.getStateData(state);
         BlockEffect[] effects = data.getEffects();
         if (effects.length > 0) {
            text.add(TEXT_BLOCK_EFFECTS);

            for(BlockEffect e : effects) {
               text.add(TextFormatting.GOLD + e.getEffectType().getName());
            }
         }

         effects = data.getAlwaysOnEffects();
         if (effects.length > 0) {
            text.add(TEXT_ALWAYS_ON_EFFECTS);

            for(BlockEffect e : effects) {
               text.add(TextFormatting.GOLD + e.getEffectType().getName());
            }
         }

         SoundEffect[] sounds = data.getSounds();
         if (sounds.length > 0) {
            text.add(TEXT_BLOCK_SOUNDS);
            text.add(TextFormatting.DARK_GREEN + "Chance: 1 in " + data.getChance());

            for(SoundEffect s : sounds) {
               text.add(TextFormatting.GOLD + s.toString());
            }
         }
      }

      List<String> oreNames = gatherOreNames(stack);
      if (oreNames.size() > 0) {
         text.add(TEXT_DICTIONARY_NAMES);

         for(String ore : oreNames) {
            text.add(TextFormatting.GOLD + ore);
         }
      }

   }

   private void gatherEntityText(@Nonnull Entity entity, @Nonnull List<String> text) {
      try {
         ResourceLocation key = EntityList.getKey(entity);
         String keyName;
         if (key != null) {
            keyName = key.toString();
         } else {
            keyName = "No ID Found";
         }

         text.add(TextFormatting.DARK_AQUA + entity.getName());
         text.add(keyName);
         text.add(entity.getClass().getName());
         Set<String> tags = entity.getTags();
         if (tags.size() > 0) {
            text.add(TextFormatting.GOLD + "Entity Tags");
            text.addAll(tags);
         }

         IEntityData data = CapabilityEntityData.getCapability(entity);
         if (data != null) {
            text.add(((NBTTagCompound)data.serializeNBT()).toString());
         }

         text.add(TextFormatting.GOLD + "Effects");
         text.addAll(((FxHandler)EffectManager.instance().lookupService(FxHandler.class)).getEffects(entity));
      } catch (Exception var7) {
         text.add(TextFormatting.RED + "!! ERROR !!");
      }

   }

   private static boolean isHolding() {
      ItemStack held = EnvironStateHandler.EnvironState.getPlayer().getHeldItem(EnumHand.MAIN_HAND);
      return !held.isEmpty() && held.getItem() == Items.CARROT_ON_A_STICK;
   }

   public InspectionHUD() {
   }

   public void doTick(int tickRef) {
      if (tickRef != 0 && tickRef % 5 == 0) {
         this.textPanel.resetText();
         if (ModOptions.logging.enableDebugLogging && isHolding()) {
            List<String> data = new ArrayList();
            RayTraceResult current = Minecraft.getMinecraft().objectMouseOver;
            if (current != null) {
               if (current.entityHit != null) {
                  this.gatherEntityText(current.entityHit, data);
               } else {
                  BlockPos targetBlock = current.getBlockPos();
                  IBlockState state = WorldUtils.getBlockState(EnvironStateHandler.EnvironState.getWorld(), targetBlock);
                  if (!WorldUtils.isAirBlock(state)) {
                     ItemStack stack = state.getBlock().getPickBlock(state, current, EnvironStateHandler.EnvironState.getWorld(), targetBlock, EnvironStateHandler.EnvironState.getPlayer());
                     this.gatherBlockText(stack, data, state, targetBlock);
                  }
               }
            }

            if (data.size() > 0) {
               this.textPanel.setText(data);
            }
         }
      }

   }

   public void doRender(@Nonnull Pre event) {
      if (event.getType() == ElementType.TEXT && this.textPanel.hasText()) {
         int centerX = event.getResolution().getScaledWidth() / 2;
         int centerY = 80;
         this.textPanel.render(centerX, 80, Reference.TOP_CENTER);
      }

   }
}
