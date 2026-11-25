package vazkii.quark.misc.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreIngredient;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.client.ClientReflectiveAccessor;
import vazkii.quark.base.handler.BeaconReplacementHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.potion.PotionMod;
import vazkii.quark.world.feature.Biotite;
import vazkii.quark.world.feature.UndergroundBiomes;

public class ExtraPotions extends Feature {
   public static Potion dangerSight;
   private boolean started = false;
   public static boolean enableHaste;
   public static boolean enableResistance;
   public static boolean enableDangerSight;
   public static boolean forceQuartzForResistance;
   public static boolean forceClownfishForDangerSight;
   public static String[] replacements;
   private static final ResourceLocation BEACON_GUI_TEXTURES = new ResourceLocation("textures/gui/container/beacon.png");

   public ExtraPotions() {
   }

   public void setupConfig() {
      enableHaste = this.loadPropBool("Enable Haste Potion", "", true);
      enableResistance = this.loadPropBool("Enable Resistance Potion", "", true);
      enableDangerSight = this.loadPropBool("Enable Danger Sight Potion", "", true);
      forceQuartzForResistance = this.loadPropBool("Force Quartz for Resistance", "Always use Quartz instead of Biotite, even if Biotite is available.", false);
      forceClownfishForDangerSight = this.loadPropBool("Force Clownfish for Danger Sight", "Always use Clownfish instead of Glowshroom, even if Glowshroom is available.", forceClownfishForDangerSight);
      String comment = "A list of potentially context sensitive replacements to apply to the beacon effect list.\nThis system allows the potion effects in a beacon to change based on if there's specific blocks on top of the pyramid at the same layer as the beacon.\nLines are processed in the order you add them.\nEach line is a comma separated list in the following format:\nblock,meta,layer,index,potion\n\nEach value is as follows:\nblock: A block ID of a block to check against when applying this replacement. You can leave this empty if you always want to apply the replacement\nmeta: The metadata of the block to check against, or -1 if any metadata will work. You must include this even if block is empty\nlayer: The layer of the beacon that contains the effect you want to replace (range: 0-3)\nindex: The effect in that layer you want to replace (range: 0-1, just 0 if it's layers 2 or 3)\npotion: The ID for the potion to replace. Note: modded potions won't display the icons properly\n\nExamples:\nminecraft:sea_lantern,-1,0,1,minecraft:water_breathing -> Replace Haste with Water Breathing if there's a Sea Lantern next to the beacon\n,-1,1,0,minecraft:levitation -> Always replace Resistance with Levitation";
      replacements = this.loadPropStringList("Beacon Replacements", comment, new String[0]);
      if (this.started) {
         BeaconReplacementHandler.parse(replacements);
      }

      this.started = true;
   }

   public void postPreInit() {
      if (enableHaste) {
         addStandardBlend(MobEffects.HASTE, Items.PRISMARINE_CRYSTALS, MobEffects.MINING_FATIGUE);
      }

      if (enableResistance) {
         addStandardBlend(MobEffects.RESISTANCE, Biotite.biotite != null && !forceQuartzForResistance ? Biotite.biotite : Items.QUARTZ);
      }

      if (enableDangerSight) {
         dangerSight = (new PotionMod("danger_sight", false, 575715, 1)).setBeneficial();
         addStandardBlend(dangerSight, UndergroundBiomes.glowshroom != null && !forceClownfishForDangerSight ? UndergroundBiomes.glowshroom : new ItemStack(Items.FISH, 1, 2), (Potion)null, 3600, 9600, 0);
      }

   }

   public void postInit() {
      BeaconReplacementHandler.parse(replacements);
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void clientTick(ClientTickEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      if (enableDangerSight && event.phase == Phase.START && mc.player != null && mc.player.getActivePotionEffect(dangerSight) != null && !mc.isGamePaused()) {
         int range = 12;
         World world = mc.world;

         for(BlockPos pos : BlockPos.getAllInBox(mc.player.getPosition().add(-range, -range, -range), mc.player.getPosition().add(range, range, range))) {
            if ((double)world.rand.nextFloat() < 0.1D && this.canMobsSpawnInPos(world, pos)) {
               float x = (float)pos.getX() + 0.3F + world.rand.nextFloat() * 0.4F;
               float y = (float)pos.getY();
               float z = (float)pos.getZ() + 0.3F + world.rand.nextFloat() * 0.4F;
               world.spawnParticle(EnumParticleTypes.SPELL_MOB, (double)x, (double)y, (double)z, (double)world.rand.nextFloat() < 0.9D ? 0.0D : 1.0D, 0.0D, 0.0D, new int[0]);
            }
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onGuiOpen(GuiOpenEvent event) {
      if (event.getGui() instanceof GuiBeacon) {
         Minecraft mc = Minecraft.getMinecraft();
         TileEntity lookTe = mc.world.getTileEntity(mc.objectMouseOver.getBlockPos());
         if (lookTe instanceof TileEntityBeacon) {
            BeaconReplacementHandler.update((TileEntityBeacon)lookTe);
         }
      }

   }

   private boolean canMobsSpawnInPos(World world, BlockPos pos) {
      if (world.isSideSolid(pos.down(), EnumFacing.UP) && !world.isBlockNormalCube(pos, false) && !world.isBlockNormalCube(pos.up(), false) && !world.getBlockState(pos).getMaterial().isLiquid()) {
         IBlockState state = world.getBlockState(pos);
         if ((!ModuleLoader.isFeatureEnabled(BlackAsh.class) || state.getBlock() != BlackAsh.black_ash) && world.getBlockState(pos.down(2)).getBlock() != BlackAsh.black_ash) {
            int lightLevel = world.getLightFor(EnumSkyBlock.BLOCK, pos);
            return lightLevel < 8 && (world.isAirBlock(pos) || state.getCollisionBoundingBox(world, pos) == null);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void addStandardBlend(Potion type, Object reagent) {
      addStandardBlend(type, reagent, (Potion)null);
   }

   public static void addStandardBlend(Potion type, Object reagent, Potion negation) {
      addStandardBlend(type, reagent, negation, 3600, 9600, 1800);
   }

   public static void addStandardBlend(Potion type, Object reagent, Potion negation, int normalTime, int longTime, int strongTime) {
      ResourceLocation loc = type.getRegistryName();
      if (loc != null) {
         String baseName = loc.getResourcePath();
         boolean hasStrong = strongTime > 0;
         PotionType normalType = addPotion(new PotionEffect(type, normalTime), baseName, baseName);
         PotionType longType = addPotion(new PotionEffect(type, longTime), baseName, "long_" + baseName);
         PotionType strongType = !hasStrong ? null : addPotion(new PotionEffect(type, strongTime, 1), baseName, "strong_" + baseName);
         if (reagent instanceof Item) {
            reagent = Ingredient.fromItem((Item)reagent);
         } else if (reagent instanceof Block) {
            reagent = Ingredient.fromStacks(new ItemStack[]{ProxyRegistry.newStack((Block)reagent)});
         } else if (reagent instanceof ItemStack) {
            reagent = Ingredient.fromStacks(new ItemStack[]{(ItemStack)reagent});
         } else if (reagent instanceof String) {
            reagent = new OreIngredient((String)reagent);
         }

         if (!(reagent instanceof Ingredient)) {
            throw new IllegalArgumentException("Reagent can't be " + reagent.getClass());
         }

         PotionHelper.addMix(PotionTypes.AWKWARD, (Ingredient)reagent, normalType);
         PotionHelper.addMix(PotionTypes.WATER, (Ingredient)reagent, PotionTypes.MUNDANE);
         if (hasStrong) {
            PotionHelper.addMix(normalType, Items.GLOWSTONE_DUST, strongType);
         }

         PotionHelper.addMix(normalType, Items.REDSTONE, longType);
         if (negation != null) {
            ResourceLocation negationLoc = negation.getRegistryName();
            if (negationLoc != null) {
               String negationBaseName = negationLoc.getResourcePath();
               PotionType normalNegationType = addPotion(new PotionEffect(negation, normalTime), negationBaseName, negationBaseName);
               PotionType longNegationType = addPotion(new PotionEffect(negation, longTime), negationBaseName, "long_" + negationBaseName);
               PotionType strongNegationType = !hasStrong ? null : addPotion(new PotionEffect(negation, strongTime, 1), negationBaseName, "strong_" + negationBaseName);
               PotionHelper.addMix(normalType, Items.FERMENTED_SPIDER_EYE, normalNegationType);
               if (hasStrong) {
                  PotionHelper.addMix(strongType, Items.FERMENTED_SPIDER_EYE, strongNegationType);
                  PotionHelper.addMix(normalNegationType, Items.GLOWSTONE_DUST, strongNegationType);
               }

               PotionHelper.addMix(longType, Items.FERMENTED_SPIDER_EYE, longNegationType);
               PotionHelper.addMix(normalNegationType, Items.REDSTONE, longNegationType);
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static boolean renderPotion(GuiButton button, GuiBeacon beacon, Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
      if (!ModuleLoader.isFeatureEnabled(ExtraPotions.class)) {
         return false;
      } else {
         Potion potion = ClientReflectiveAccessor.getPotion(button);
         if (isVanilla(potion)) {
            return false;
         } else {
            if (button.visible) {
               minecraft.getTextureManager().bindTexture(BEACON_GUI_TEXTURES);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               boolean hovered = mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.width && mouseY < button.y + button.height;
               ClientReflectiveAccessor.setButtonHovered(button, hovered);
               int width = 0;
               int height = 219;
               if (!button.enabled) {
                  width += button.width * 2;
               } else if (ClientReflectiveAccessor.getButtonSelected(button)) {
                  width += button.width;
               } else if (hovered) {
                  width += button.width * 3;
               }

               beacon.drawTexturedModalRect(button.x, button.y, width, height, button.width, button.height);
               potion.renderInventoryEffect(new PotionEffect(potion), beacon, button.x - 4, button.y - 5, ClientReflectiveAccessor.getZLevel(beacon));
            }

            return true;
         }
      }
   }

   private static PotionType addPotion(PotionEffect eff, String baseName, String name) {
      PotionType type = (PotionType)(new PotionType(baseName, new PotionEffect[]{eff})).setRegistryName(new ResourceLocation("quark", name));
      ProxyRegistry.register(type);
      return type;
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }
}
