package noppes.vc.client;

import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import com.arlenh7.eaglercraft.awt.Color;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import noppes.vc.CommonProxy;
import noppes.vc.EntityHolyHandGrenade;
import noppes.vc.EntityProjectile;
import noppes.vc.VCArmors;
import noppes.vc.VCBlocks;
import noppes.vc.VCItems;
import noppes.vc.VCWeapons;
import noppes.vc.VariedCommodities;
import noppes.vc.client.fx.EntityElementalStaffFX;
import noppes.vc.client.gui.GuiBigSign;
import noppes.vc.client.gui.GuiCarpentryBench;
import noppes.vc.client.gui.GuiCrate;
import noppes.vc.client.gui.GuiRecipes;
import noppes.vc.client.gui.GuiTradingBlock;
import noppes.vc.client.renderer.RenderHolyHandGrenade;
import noppes.vc.client.renderer.RenderProjectile;
import noppes.vc.constants.GuiType;
import noppes.vc.containers.ContainerCarpentryBench;
import noppes.vc.containers.ContainerCrate;
import noppes.vc.containers.ContainerTradingBlock;

public class ClientProxy extends CommonProxy {
   public static FontContainer Font = null;
   private ModelSkirtArmor model = new ModelSkirtArmor();

   public ClientProxy() {
   }

   public void preinit() {
      super.preinit();
      Font = new FontContainer("Default", 18);
      VariedCommodities.Channel.register(ClientCustomPacketEvent.class, new PacketHandlerClient()::onPacketData);
   }

   public void init() {
      RenderingRegistry.registerEntityRenderingHandler(EntityProjectile.class, new RenderProjectile());
      RenderingRegistry.registerEntityRenderingHandler(EntityHolyHandGrenade.class, new RenderHolyHandGrenade());
   }

   public void postinit() {
      ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new VCResourceLoader());
      Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
         float[] color = EntitySheep.getDyeRgb(EnumDyeColor.byDyeDamage(stack.getItemDamage()));
         return (new Color(color[0], color[1], color[2])).getRGB();
      }, new Item[]{VCWeapons.elemental_staff, VCItems.orb, VCItems.orb_broken, Item.getItemFromBlock(VCBlocks.crystal)});
      Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
         return tintIndex > 0 ? -1 : ((ItemArmor)stack.getItem()).getColor(stack);
      }, new Item[]{VCItems.paper_crown, VCArmors.leather_skirt});
      Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
         return state.getMapColor(world, pos).colorValue;
      }, new Block[]{VCBlocks.crystal});
   }

   public ModelBiped getSkirtModel() {
      return this.model;
   }

   public void spawnParticle(EntityLivingBase player, String string, Object... ob) {
      if ("Spell".equals(string)) {
         int color = ((Integer)ob[0]).intValue();
         int number = ((Integer)ob[1]).intValue();

         for(int i = 0; i < number; ++i) {
            EaglercraftRandom rand = player.getRNG();
            double x = (rand.nextDouble() - 0.5D) * (double)player.width;
            double y = (double)player.getEyeHeight();
            double z = (rand.nextDouble() - 0.5D) * (double)player.width;
            double f = (rand.nextDouble() - 0.5D) * 2.0D;
            double f1 = -rand.nextDouble();
            double f2 = (rand.nextDouble() - 0.5D) * 2.0D;
            Minecraft.getMinecraft().effectRenderer.addEffect(new EntityElementalStaffFX(player, x, y, z, f, f1, f2, color));
         }
      } else if ("Block".equals(string)) {
         BlockPos pos = (BlockPos)ob[0];
         int id = ((Integer)ob[1]).intValue();
         Block block = Block.getBlockById(id & 4095);
         Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(pos, block.getDefaultState());
      }

   }

   public void spawnParticle(EnumParticleTypes particle, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) {
      Minecraft mc = Minecraft.getMinecraft();
      double xx = mc.getRenderViewEntity().posX - x;
      double yy = mc.getRenderViewEntity().posY - y;
      double zz = mc.getRenderViewEntity().posZ - z;
      if (xx * xx + yy * yy + zz * zz <= 256.0D) {
         Particle fx = mc.effectRenderer.spawnEffectParticle(particle.getParticleID(), x, y, z, motionX, motionY, motionZ, new int[0]);
         if (fx != null) {
            if (particle == EnumParticleTypes.FLAME) {
               ((ParticleFlame)fx).flameScale = scale;
            } else if (particle == EnumParticleTypes.SMOKE_NORMAL) {
               ((ParticleSmokeNormal)fx).smokeParticleScale = scale;
            }

         }
      }
   }

   public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
      if (ID > GuiType.values().length) {
         return null;
      } else {
         GuiType gui = GuiType.values()[ID];
         Container container = this.getContainer(gui, player, world, x, y, z);
         return this.getGui(gui, container, x, y, z);
      }
   }

   private GuiScreen getGui(GuiType gui, Container container, int x, int y, int z) {
      if (gui == GuiType.TRADING_BLOCK) {
         return new GuiTradingBlock((ContainerTradingBlock)container);
      } else if (gui == GuiType.CRATE) {
         return new GuiCrate((ContainerCrate)container);
      } else if (gui == GuiType.CARPENTRY_BENCH) {
         return new GuiCarpentryBench((ContainerCarpentryBench)container);
      } else if (gui == GuiType.BIG_SIGN) {
         return new GuiBigSign(x, y, z);
      } else {
         return gui == GuiType.VCRECIPES ? new GuiRecipes() : null;
      }
   }

   public void openGui(GuiType gui, int x, int y, int z) {
      Minecraft minecraft = Minecraft.getMinecraft();
      Container container = this.getContainer(gui, minecraft.player, minecraft.world, x, y, z);
      GuiScreen guiscreen = this.getGui(gui, container, x, y, z);
      if (guiscreen != null) {
         minecraft.displayGuiScreen(guiscreen);
      }

   }

   public void openGui(EntityPlayer player, Object guiscreen) {
      Minecraft minecraft = Minecraft.getMinecraft();
      if (player.world.isRemote && guiscreen instanceof GuiScreen) {
         minecraft.displayGuiScreen((GuiScreen)guiscreen);
      }
   }
}
