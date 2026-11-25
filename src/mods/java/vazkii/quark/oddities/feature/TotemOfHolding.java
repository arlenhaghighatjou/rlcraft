package vazkii.quark.oddities.feature;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.client.AtlasSpriteHelper;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;
import vazkii.quark.oddities.client.render.RenderTotemOfHolding;
import vazkii.quark.oddities.entity.EntityTotemOfHolding;
import vazkii.quark.oddities.item.ItemSoulCompass;
import vazkii.quark.world.feature.Wraiths;

public class TotemOfHolding extends Feature {
   private static final String TAG_LAST_TOTEM = "quark:lastTotemOfHolding";
   private static final String TAG_DEATH_X = "quark:deathX";
   private static final String TAG_DEATH_Z = "quark:deathZ";
   private static final String TAG_DEATH_DIM = "quark:deathDim";
   @SideOnly(Side.CLIENT)
   public static EaglerTextureAtlasSprite totemSprite;
   public static Item soul_compass;
   public static boolean darkSoulsMode;
   public static boolean enableOnPK;
   public static boolean destroyItems;
   public static boolean anyoneCollect;
   public static boolean enableSoulCompass;

   public TotemOfHolding() {
   }

   public void setupConfig() {
      darkSoulsMode = this.loadPropBool("Dark Souls Mode", "Set this to false to remove the behaviour where totems destroy themselves if the player dies again.", true);
      enableOnPK = this.loadPropBool("Spawn Totem on PVP Kill", "", false);
      destroyItems = this.loadPropBool("Destroy Lost Items", "Set this to true to make it so that if a totem is destroyed, the items it holds are destroyed alongside it rather than dropped", false);
      anyoneCollect = this.loadPropBool("Allow Anyone to Collect", "Set this to false to only allow the owner of a totem to collect its items rather than any player", true);
      enableSoulCompass = this.loadPropBool("Enable Soul Compass", "", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      if (enableSoulCompass) {
         soul_compass = new ItemSoulCompass();
      }

      String totemName = "quark:totem_of_holding";
      EntityRegistry.registerModEntity(new ResourceLocation(totemName), EntityTotemOfHolding.class, totemName, 15, Quark.instance, 64, 128, false);
   }

   public void postPreInit() {
      if (enableSoulCompass) {
         RecipeHandler.addShapelessOreDictRecipe(new ItemStack(soul_compass), new Object[]{Wraiths.soul_bead == null ? new ItemStack(Blocks.SOUL_SAND) : new ItemStack(Wraiths.soul_bead), new ItemStack(Items.COMPASS)});
      }

   }

   public void preInitClient() {
      RenderingRegistry.registerEntityRenderingHandler(EntityTotemOfHolding.class, RenderTotemOfHolding.factory());
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onTextureStitch(TextureStitchEvent event) {
      totemSprite = AtlasSpriteHelper.forName(event.getMap(), new ResourceLocation("quark", "items/holding_totem"));
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onPlayerDrops(PlayerDropsEvent event) {
      List<EntityItem> drops = event.getDrops();
      if (!event.isCanceled() && (enableOnPK || !(event.getSource().getTrueSource() instanceof EntityPlayer))) {
         EntityPlayer player = event.getEntityPlayer();
         NBTTagCompound data = player.getEntityData();
         NBTTagCompound persistent = data.getCompoundTag("PlayerPersisted");
         if (!drops.isEmpty()) {
            EntityTotemOfHolding totem = new EntityTotemOfHolding(player.world);
            totem.setPosition(player.posX, Math.max(3.0D, player.posY + 1.0D), player.posZ);
            totem.setOwner(player);
            totem.setCustomNameTag(player.getDisplayNameString());
            drops.stream().filter(Objects::nonNull).map(EntityItem::getItem).filter((stack) -> {
               return !stack.isEmpty();
            }).forEach(totem::addItem);
            if (!player.world.isRemote) {
               player.world.spawnEntity(totem);
            }

            persistent.setString("quark:lastTotemOfHolding", totem.getUniqueID().toString());
            event.setCanceled(true);
         } else {
            persistent.setString("quark:lastTotemOfHolding", "");
         }

         BlockPos pos = player.getPosition();
         persistent.setInteger("quark:deathX", pos.getX());
         persistent.setInteger("quark:deathZ", pos.getZ());
         persistent.setInteger("quark:deathDim", player.world.provider.getDimension());
         if (!data.hasKey("PlayerPersisted")) {
            data.setTag("PlayerPersisted", persistent);
         }
      }

   }

   public static String getTotemUUID(EntityPlayer player) {
      NBTTagCompound cmp = player.getEntityData().getCompoundTag("PlayerPersisted");
      return cmp.hasKey("quark:lastTotemOfHolding") ? cmp.getString("quark:lastTotemOfHolding") : "";
   }

   public static BlockPos getPlayerDeathPosition(Entity e) {
      if (e instanceof EntityPlayer) {
         NBTTagCompound cmp = e.getEntityData().getCompoundTag("PlayerPersisted");
         if (cmp.hasKey("quark:lastTotemOfHolding")) {
            int x = cmp.getInteger("quark:deathX");
            int z = cmp.getInteger("quark:deathZ");
            int dim = cmp.getInteger("quark:deathDim");
            return new BlockPos(x, dim, z);
         }
      }

      return new BlockPos(0, -1, 0);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
