package ejektaflex.bountiful.proxy;

import ejektaflex.bountiful.ContentRegistry;
import ejektaflex.bountiful.api.BountifulAPI;
import ejektaflex.bountiful.api.data.IBountyData;
import ejektaflex.bountiful.api.enum.EnumBountyRarity;
import ejektaflex.bountiful.item.ItemBounty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\u000fB\u0005\u00a2\u0006\u0002\u0010\u0002J \u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002J \u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002J\u0010\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u000eH\u0007\u00a8\u0006\u0010"},
   d2 = {"Lejektaflex/bountiful/proxy/ClientProxy;", "Lejektaflex/bountiful/proxy/CommonProxy;", "()V", "registerBountyRenderer", "", "item", "Lnet/minecraft/item/Item;", "meta", "", "id", "", "registerItemRenderer", "registerModels", "event", "Lnet/minecraftforge/client/event/ModelRegistryEvent;", "BountyItemDefinition", "Bountiful"}
)
public final class ClientProxy extends CommonProxy {
   private final void registerItemRenderer(Item item, int meta, String id) {
      ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("bountiful:" + id, "inventory"));
   }

   private final void registerBountyRenderer(Item item, int meta, String id) {
      Object[] $receiver$iv = EnumBountyRarity.values();
      Collection destination$iv$iv = (Collection)(new ArrayList($receiver$iv.length));

      for(Object item$iv$iv : $receiver$iv) {
         String var14 = ((EnumBountyRarity)item$iv$iv).name();
         if (var14 == null) {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
         }

         String var10000 = var14.toLowerCase();
         Intrinsics.checkExpressionValueIsNotNull(var10000, "(this as java.lang.String).toLowerCase()");
         String var19 = var10000;
         destination$iv$iv.add(var19);
      }

      for(String rarity : (List)destination$iv$iv) {
         ModelBakery.registerItemVariants(item, new ResourceLocation[]{new ResourceLocation("bountiful:" + id + '-' + rarity)});
      }

      ModelLoader.setCustomMeshDefinition(item, new ClientProxy.BountyItemDefinition(id));
   }

   @SubscribeEvent
   public final void registerModels(@NotNull ModelRegistryEvent event) {
      Intrinsics.checkParameterIsNotNull(event, "event");

      for(Object element$iv : (Iterable)ContentRegistry.INSTANCE.getItems()) {
         Item it = (Item)element$iv;
         if (it instanceof ItemBounty) {
            ResourceLocation var10003 = ((ItemBounty)it).getRegistryName();
            if (var10003 == null) {
               Intrinsics.throwNpe();
            }

            Intrinsics.checkExpressionValueIsNotNull(var10003, "it.registryName!!");
            String var8 = var10003.getResourcePath();
            Intrinsics.checkExpressionValueIsNotNull(var8, "it.registryName!!.path");
            this.registerBountyRenderer(it, 0, var8);
         } else {
            ResourceLocation var9 = it.getRegistryName();
            if (var9 == null) {
               Intrinsics.throwNpe();
            }

            Intrinsics.checkExpressionValueIsNotNull(var9, "it.registryName!!");
            String var10 = var9.getResourcePath();
            Intrinsics.checkExpressionValueIsNotNull(var10, "it.registryName!!.path");
            this.registerItemRenderer(it, 0, var10);
         }
      }

   }

   public ClientProxy() {
   }

   @SideOnly(Side.CLIENT)
   @Metadata(
      mv = {1, 1, 13},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u000b"},
      d2 = {"Lejektaflex/bountiful/proxy/ClientProxy$BountyItemDefinition;", "Lnet/minecraft/client/renderer/ItemMeshDefinition;", "id", "", "(Ljava/lang/String;)V", "getId", "()Ljava/lang/String;", "getModelLocation", "Lnet/minecraft/client/renderer/block/model/ModelResourceLocation;", "stack", "Lnet/minecraft/item/ItemStack;", "Bountiful"}
   )
   private static final class BountyItemDefinition implements ItemMeshDefinition {
      @NotNull
      private final String id;

      @NotNull
      public ModelResourceLocation getModelLocation(@NotNull ItemStack stack) {
         Intrinsics.checkParameterIsNotNull(stack, "stack");
         String locid = "bountiful:" + this.id;

         ModelResourceLocation data;
         try {
            IBountyData data = BountifulAPI.INSTANCE.toBountyData(stack);
            EnumBountyRarity rarity = EnumBountyRarity.Companion.getRarityFromInt(data.getRarity());
            StringBuilder var21 = (new StringBuilder()).append(locid).append('-');
            String var16 = rarity.name();
            StringBuilder var17 = var21;
            if (var16 == null) {
               throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
            }

            String var22 = var16.toLowerCase();
            Intrinsics.checkExpressionValueIsNotNull(var22, "(this as java.lang.String).toLowerCase()");
            String var18 = var22;
            var22 = var17.append(var18).toString();
            String var10 = "inventory";
            String var11 = var22;
            data = new ModelResourceLocation(var11, var10);
         } catch (Exception var14) {
            StringBuilder var10000 = (new StringBuilder()).append(locid).append('-');
            String var5 = EnumBountyRarity.Common.name();
            StringBuilder var8 = var10000;
            if (var5 == null) {
               throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
            }

            String var19 = var5.toLowerCase();
            Intrinsics.checkExpressionValueIsNotNull(var19, "(this as java.lang.String).toLowerCase()");
            String var9 = var19;
            var19 = var8.append(var9).toString();
            String var12 = "inventory";
            String var13 = var19;
            data = new ModelResourceLocation(var13, var12);
         }

         return data;
      }

      @NotNull
      public final String getId() {
         return this.id;
      }

      public BountyItemDefinition(@NotNull String id) {
         Intrinsics.checkParameterIsNotNull(id, "id");
         super();
         this.id = id;
      }
   }
}
