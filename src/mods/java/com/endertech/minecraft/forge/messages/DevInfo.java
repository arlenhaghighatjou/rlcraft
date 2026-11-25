package com.endertech.minecraft.forge.messages;

import com.endertech.common.Args;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.api.IWind;
import com.endertech.minecraft.forge.blocks.ForgeBlock;
import com.endertech.minecraft.forge.configs.ForgeKeys;
import com.endertech.minecraft.forge.configs.ForgeNBT;
import com.endertech.minecraft.forge.entities.ForgeEntity;
import com.endertech.minecraft.forge.items.ForgeItem;
import com.endertech.minecraft.forge.materials.ForgeMaterial;
import com.endertech.minecraft.forge.math.GameTime;
import com.endertech.minecraft.forge.tools.ForgeTool;
import com.endertech.minecraft.forge.units.ItemState;
import com.endertech.minecraft.forge.units.UnitId;
import com.endertech.minecraft.forge.world.ForgeWorld;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@EventBusSubscriber({Side.CLIENT})
public class DevInfo {
   static final GameTime UPDATE_INTERVAL = GameTime.quaterSecond();
   static final GameTime EXCEPTION_PRINT_INTERVAL = GameTime.seconds(10);
   protected static float rayTraceDistance = 10.0F;
   protected static boolean showInventoryTooltip = false;
   protected static boolean renderGameOverlay = false;
   protected static KeyBinding keyToggleDevInfo;
   private static RayTraceResult rayTraceResult = null;

   public DevInfo() {
   }

   @SubscribeEvent
   public static void onKeyPressed(KeyInputEvent event) {
      if (ForgeMain.isDebugMode() && Keyboard.isKeyDown(70) && Keyboard.isKeyDown(42)) {
         showInventoryTooltip = !showInventoryTooltip;
         renderGameOverlay = !renderGameOverlay;
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onInventoryTooltip(ItemTooltipEvent event) {
      if (showInventoryTooltip) {
         ItemStack stack = event.getItemStack();
         if (!stack.isEmpty()) {
            List<String> lines = event.getToolTip();
            addItemInfo(stack, lines);
         }

      }
   }

   @SubscribeEvent
   public static void onRenderGameOverlay(Post event) {
      if (renderGameOverlay) {
         Minecraft mc = Minecraft.getMinecraft();
         GameSettings gs = ForgeKeys.getGameSettings();
         if (mc.currentScreen == null && event.getType().equals(ElementType.HOTBAR)) {
            try {
               List<String> lines = new ArrayList();
               addWorldInfo(mc.world, lines);
               lines.add("");
               boolean shouldRecalcTraceResult = rayTraceResult == null || UPDATE_INTERVAL.pastIn(mc.world);
               if (shouldRecalcTraceResult) {
                  if (ForgeKeys.isDown(gs.keyBindSprint)) {
                     rayTraceResult = mc.objectMouseOver;
                  } else {
                     rayTraceResult = ForgeEntity.rayTraceBlockBeingLookedAt(mc.player, event.getPartialTicks(), rayTraceDistance);
                  }
               }

               if (rayTraceResult != null) {
                  switch(rayTraceResult.typeOfHit) {
                  case BLOCK:
                     addBlockInfo(mc.world, rayTraceResult.getBlockPos(), lines);
                     break;
                  case ENTITY:
                     addEntityInfo(rayTraceResult.entityHit, lines);
                  }
               }

               GL11.glPushMatrix();
               GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
               int color = -2130706433;
               int x = 2;
               int y = 2;
               int step = 10;

               for(String line : lines) {
                  mc.fontRenderer.drawStringWithShadow(line, (float)x, (float)y, color);
                  y += step;
               }

               GL11.glPopMatrix();
            } catch (Exception var11) {
               if (EXCEPTION_PRINT_INTERVAL.pastIn(mc.world)) {
                  var11.printStackTrace();
               }
            }
         }

      }
   }

   protected static void addCaptionLine(List<String> lines, String caption) {
      lines.add("==" + caption + "==");
   }

   protected static void addInfoLine(List<String> lines, String caption, Object value) {
      lines.add(underlined(caption + ": ") + value);
   }

   protected static void addWorldInfo(WorldClient world, List<String> lines) {
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      BlockPos pos = player.getPosition();
      Biome biome = world.getBiome(pos);
      ForgeWorld.WorldData data = ForgeWorld.getData(world);
      IWind wind = ForgeWorld.getWindAt(world, pos);
      addCaptionLine(lines, "World Info");
      addInfoLine(lines, "Biome", biome.getRegistryName() + ":" + Biome.getIdForBiome(biome) + " (" + biome.getBiomeName() + ")");
      addInfoLine(lines, "Wind", Float.valueOf(wind.getStrengthIn(player.getHorizontalFacing())));
      addInfoLine(lines, "isRaining", Boolean.valueOf(world.isRaining()));
      addInfoLine(lines, "Strength", "rain = " + world.rainingStrength + ", thunder = " + world.thunderingStrength);
      addInfoLine(lines, "SmokeParticles", Integer.valueOf(data.smokeParticlesCount));
      addInfoLine(lines, "Time", Long.valueOf(world.getWorldTime()));
   }

   protected static void addBlockInfo(WorldClient world, BlockPos pos, List<String> lines) {
      IBlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      UnitId id = UnitId.from(state);
      EntityPlayerSP player = Minecraft.getMinecraft().player;
      addCaptionLine(lines, "Block Info");
      lines.add(formatedId(id) + " (" + Args.get("dropped_meta", Integer.valueOf(ForgeWorld.getBlockDroppedState(world, pos).getMeta())) + ")");
      addInfoLine(lines, "State", state);
      addInfoLine(lines, "StateId", Integer.valueOf(Block.getStateId(state)));
      addInfoLine(lines, "Unlocalized Name", block.getUnlocalizedName());
      addInfoLine(lines, "Material", ForgeMaterial.VanillaMaterials.getNameFor(state.getMaterial(), "???"));
      addInfoLine(lines, "Hardness", Float.valueOf(state.getBlockHardness(world, pos)));
      addInfoLine(lines, "Resistance", Float.valueOf(block.getExplosionResistance(player)));
      addInfoLine(lines, "Harvest level", Integer.valueOf(block.getHarvestLevel(state)));
      addInfoLine(lines, "Opacity", Integer.valueOf(world.getBlockLightOpacity(pos)));
      addInfoLine(lines, "CanSeeSky", Boolean.valueOf(ForgeBlock.isUnderOpenSky(world, pos)));
      addInfoLine(lines, "isUnderRain", Boolean.valueOf(ForgeBlock.isUnderRain(world, pos)));
      addInfoLine(lines, "PrecipitationHeight", Integer.valueOf(world.getPrecipitationHeight(pos).getY()));
      addInfoLine(lines, "BlockPos", Args.join(Args.get("x", Integer.valueOf(pos.getX())), Args.get("y", Integer.valueOf(pos.getY())), Args.get("z", Integer.valueOf(pos.getZ()))));
      EnumFacing side = rayTraceResult.sideHit;
      addInfoLine(lines, "Side", Args.join(Args.get("face", side), Args.get("shape", state.getBlockFaceShape(world, pos, side)), Args.get("isSolid", Boolean.valueOf(state.isSideSolid(world, pos, side)))));
      addInfoLine(lines, "CollisionBB", state.getCollisionBoundingBox(world, pos));
      addInfoLine(lines, "TileEntity", world.getTileEntity(pos));
      TileEntity tile = world.getTileEntity(pos);
      if (tile != null) {
         NBTTagCompound nbt = new NBTTagCompound();
         tile.writeToNBT(nbt);
         addCompoundInfo(nbt, lines);
      }

   }

   static void addEntityInfo(Entity entity, List<String> lines) {
      addCaptionLine(lines, "Entity Info");
      addInfoLine(lines, "id", EntityList.getKey(entity));
      addInfoLine(lines, "name", entity.getName());
      addInfoLine(lines, "isUnderRain", Boolean.valueOf(ForgeEntity.isUnderRain(entity)));
      NBTTagCompound nbt = new NBTTagCompound();
      addCompoundInfo(entity.writeToNBT(nbt), lines);
   }

   protected static void addItemInfo(ItemStack stack, List<String> lines) {
      Item item = stack.getItem();
      UnitId unitId = UnitId.from(ItemState.from(stack));
      if (item instanceof ForgeTool) {
         ForgeTool tool = (ForgeTool)item;
         addInfoLine(lines, "Harvest Level", Integer.valueOf(tool.getHarvestLevel()));
      }

      addInfoLine(lines, "Durability", Integer.valueOf(stack.getMaxDamage()));
      lines.add(formatedId(unitId));
      int burnTime = TileEntityFurnace.getItemBurnTime(stack);
      if (burnTime > 0) {
         addInfoLine(lines, "BurnTimePollution", Float.valueOf((float)burnTime / 2000.0F));
      }

      addInfoLine(lines, "Unlocalized name", item.getUnlocalizedName(stack));
      Object[] oreNames = ForgeItem.getOreDictNamesFor(stack).toArray();
      addInfoLine(lines, "Dict", Args.join(oreNames));
      addCompoundInfo(stack.getTagCompound(), lines);
   }

   protected static void addCompoundInfo(NBTTagCompound compound, List<String> lines) {
      if (compound != null) {
         lines.add(underlined("NBT: "));

         for(String key : compound.getKeySet()) {
            ForgeNBT.Types type = ForgeNBT.getType(compound, key);
            String line = reset() + type.toString() + " " + key;
            if (type.isOrdinal() || type == ForgeNBT.Types.STRING) {
               line = line + " = " + compound.getTag(key).toString();
            }

            lines.add(line);
         }
      } else {
         lines.add("no NBT");
      }

   }

   protected static String underlined(String string) {
      return ChatFormatting.UNDERLINE + string + ChatFormatting.RESET;
   }

   protected static ChatFormatting reset() {
      return ChatFormatting.RESET;
   }

   protected static String formatedId(UnitId id) {
      return underlined("ID: ") + id;
   }
}
