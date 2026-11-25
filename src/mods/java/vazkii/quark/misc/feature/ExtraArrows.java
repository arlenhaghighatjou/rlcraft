package vazkii.quark.misc.feature;

import javax.annotation.Nonnull;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.client.render.RenderExtraArrow;
import vazkii.quark.misc.entity.EntityArrowEnder;
import vazkii.quark.misc.entity.EntityArrowExplosive;
import vazkii.quark.misc.entity.EntityArrowTorch;
import vazkii.quark.misc.item.ItemModArrow;

public class ExtraArrows extends Feature {
   public static Item arrow_ender;
   public static Item arrow_explosive;
   public static Item arrow_torch;
   public static boolean enableEnder;
   public static boolean enableExplosive;
   public static boolean enableTorch;
   public static double explosiveArrowPower;
   public static boolean explosiveArrowDestroysBlocks;

   public ExtraArrows() {
   }

   public void setupConfig() {
      enableEnder = this.loadPropBool("Enable Ender Arrow", "", true);
      enableExplosive = this.loadPropBool("Enable Explosive Arrow", "", true);
      enableTorch = this.loadPropBool("Enable Torch Arrow", "", true);
      explosiveArrowPower = this.loadPropDouble("Explosive Arrow Power", "", 2.0D);
      explosiveArrowDestroysBlocks = this.loadPropBool("Explosive Arrow Destroys Blocks", "", true);
   }

   public void preInit(FMLPreInitializationEvent event) {
      if (enableEnder) {
         String enderArrowName = "quark:arrow_ender";
         arrow_ender = new ItemModArrow("arrow_ender", (worldIn, stack, shooter) -> {
            return new EntityArrowEnder(worldIn, shooter);
         });
         EntityRegistry.registerModEntity(new ResourceLocation(enderArrowName), EntityArrowEnder.class, enderArrowName, 5, Quark.instance, 64, 10, true);
         BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(arrow_ender, new ExtraArrows.ArrowBehaviour(EntityArrowEnder::new));
         RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(arrow_ender), new Object[]{ProxyRegistry.newStack(Items.ARROW), ProxyRegistry.newStack(Items.ENDER_PEARL)});
      }

      if (enableExplosive) {
         String explosiveArrowName = "quark:arrow_explosive";
         arrow_explosive = new ItemModArrow("arrow_explosive", (worldIn, stack, shooter) -> {
            return new EntityArrowExplosive(worldIn, shooter);
         });
         EntityRegistry.registerModEntity(new ResourceLocation(explosiveArrowName), EntityArrowExplosive.class, explosiveArrowName, 6, Quark.instance, 64, 10, true);
         BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(arrow_explosive, new ExtraArrows.ArrowBehaviour(EntityArrowExplosive::new));
         RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(arrow_explosive), new Object[]{ProxyRegistry.newStack(Items.ARROW), ProxyRegistry.newStack(Items.GUNPOWDER), ProxyRegistry.newStack(Items.GUNPOWDER)});
      }

      if (enableTorch) {
         String torchArrowName = "quark:arrow_torch";
         arrow_torch = new ItemModArrow("arrow_torch", (worldIn, stack, shooter) -> {
            return new EntityArrowTorch(worldIn, shooter);
         });
         EntityRegistry.registerModEntity(new ResourceLocation(torchArrowName), EntityArrowTorch.class, torchArrowName, 7, Quark.instance, 64, 10, true);
         BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(arrow_torch, new ExtraArrows.ArrowBehaviour(EntityArrowTorch::new));
         RecipeHandler.addShapelessOreDictRecipe(ProxyRegistry.newStack(arrow_torch), new Object[]{ProxyRegistry.newStack(Items.ARROW), ProxyRegistry.newStack(Blocks.TORCH)});
      }

   }

   public void preInitClient() {
      if (enableEnder) {
         RenderingRegistry.registerEntityRenderingHandler(EntityArrowEnder.class, RenderExtraArrow.FACTORY_ENDER);
      }

      if (enableExplosive) {
         RenderingRegistry.registerEntityRenderingHandler(EntityArrowExplosive.class, RenderExtraArrow.FACTORY_EXPLOSIVE);
      }

      if (enableTorch) {
         RenderingRegistry.registerEntityRenderingHandler(EntityArrowTorch.class, RenderExtraArrow.FACTORY_TORCH);
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public static class ArrowBehaviour extends BehaviorProjectileDispense {
      private final ExtraArrows.ArrowBehaviour.ArrowProvider provider;

      public ArrowBehaviour(ExtraArrows.ArrowBehaviour.ArrowProvider provider) {
         this.provider = provider;
      }

      @Nonnull
      protected IProjectile getProjectileEntity(@Nonnull World worldIn, @Nonnull IPosition position, @Nonnull ItemStack stackIn) {
         EntityArrow arrow = this.provider.provide(worldIn, position);
         arrow.pickupStatus = PickupStatus.ALLOWED;
         return arrow;
      }

      public interface ArrowProvider {
         EntityArrow provide(World var1, IPosition var2);
      }
   }
}
