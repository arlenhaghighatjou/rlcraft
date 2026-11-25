package ejektaflex.bountiful.command;

import net.lax1dude.eaglercraft.v1_8.mojang.authlib.GameProfile;
import ejektaflex.bountiful.Bountiful;
import ejektaflex.bountiful.api.enum.EnumBountyRarity;
import ejektaflex.bountiful.api.ext.ExtMiscKt;
import ejektaflex.bountiful.api.logic.IBountyCreator;
import ejektaflex.bountiful.api.logic.pickable.PickableEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack;
import ejektaflex.bountiful.config.BountifulIO;
import ejektaflex.bountiful.item.ItemBounty;
import ejektaflex.bountiful.logic.BountyCreator;
import ejektaflex.bountiful.logic.error.BountyCreationException;
import ejektaflex.bountiful.registry.BountyRegistry;
import ejektaflex.bountiful.registry.RewardRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0011\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0001H\u0096\u0002J+\u0010\f\u001a\u00020\r2\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0016\u00a2\u0006\u0002\u0010\u0011J\u000e\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00100\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u0010H\u0016J=\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0010\u0018\u00010\u00132\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0016\u00a2\u0006\u0002\u0010\u0018J\u0010\u0010\u0019\u001a\u00020\u00102\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\u001a\u001a\u00020\u00042\u0006\u0010\u0007\u001a\u00020\bH\u0002J#\u0010\u001b\u001a\u00020\u00042\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u001c\u001a\u00020\nH\u0016\u00a2\u0006\u0002\u0010\u001dJ\u0018\u0010\u001e\u001a\u00020\r2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u001f\u001a\u00020 H\u0002\u00a8\u0006!"},
   d2 = {"Lejektaflex/bountiful/command/BountyCommand;", "Lnet/minecraft/command/ICommand;", "()V", "checkPermission", "", "server", "Lnet/minecraft/server/MinecraftServer;", "sender", "Lnet/minecraft/command/ICommandSender;", "compareTo", "", "other", "execute", "", "args", "", "", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;[Ljava/lang/String;)V", "getAliases", "", "getName", "getTabCompletions", "pos", "Lnet/minecraft/util/math/BlockPos;", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;[Ljava/lang/String;Lnet/minecraft/util/math/BlockPos;)Ljava/util/List;", "getUsage", "hasBasicPerms", "isUsernameIndex", "index", "([Ljava/lang/String;I)Z", "safeGivePlayer", "item", "Lnet/minecraft/item/ItemStack;", "Bountiful"}
)
public final class BountyCommand implements ICommand {
   public int compareTo(@NotNull ICommand other) {
      Intrinsics.checkParameterIsNotNull(other, "other");
      return 0;
   }

   @NotNull
   public String getName() {
      return "bountiful";
   }

   @NotNull
   public String getUsage(@NotNull ICommandSender sender) {
      Intrinsics.checkParameterIsNotNull(sender, "sender");
      return "/bountiful";
   }

   @NotNull
   public List<String> getAliases() {
      ArrayList aliases = new ArrayList();
      aliases.add("bo");
      aliases.add("bounty");
      return (List)aliases;
   }

   private final boolean hasBasicPerms(ICommandSender sender) {
      GameProfile var13;
      label44: {
         MinecraftServer var10000 = sender.getServer();
         if (var10000 != null) {
            PlayerList var11 = var10000.getPlayerList();
            if (var11 != null) {
               GameProfile[] var12 = var11.getOnlinePlayerProfiles();
               if (var12 != null) {
                  GameProfile[] var3 = var12;

                  for(GameProfile var8 : var3) {
                     String var14 = sender.getName();
                     Intrinsics.checkExpressionValueIsNotNull(var8, "it");
                     if (Intrinsics.areEqual(var14, var8.getName())) {
                        var13 = var8;
                        break label44;
                     }
                  }

                  var13 = null;
                  break label44;
               }
            }
         }

         var13 = null;
      }

      GameProfile playerProfile = var13;
      boolean var17;
      if (playerProfile != null) {
         MinecraftServer var15 = sender.getServer();
         if (var15 != null) {
            PlayerList var16 = var15.getPlayerList();
            if (var16 != null && var16.canSendCommands(playerProfile)) {
               var17 = true;
               return var17;
            }
         }
      }

      var17 = false;
      return var17;
   }

   private final void safeGivePlayer(ICommandSender sender, ItemStack item) {
      if (this.hasBasicPerms(sender)) {
         Entity var10000 = sender.getCommandSenderEntity();
         if (var10000 == null) {
            throw new TypeCastException("null cannot be cast to non-null type net.minecraft.entity.player.EntityPlayer");
         }

         ItemHandlerHelper.giveItemToPlayer((EntityPlayer)var10000, item);
      }

   }

   public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) throws CommandException {
      Intrinsics.checkParameterIsNotNull(server, "server");
      Intrinsics.checkParameterIsNotNull(sender, "sender");
      Intrinsics.checkParameterIsNotNull(args, "args");
      if (args.length != 0) {
         String curr = args[0];
         if (Intrinsics.areEqual(curr, "bounties")) {
            if (this.hasBasicPerms(sender)) {
               ExtMiscKt.sendMessage(sender, "\u00a76Bounties:");

               for(PickableEntry bo : BountyRegistry.INSTANCE.getItems()) {
                  ExtMiscKt.sendMessage(sender, bo.toString());
               }
            }
         } else if (Intrinsics.areEqual(curr, "rewards")) {
            if (this.hasBasicPerms(sender)) {
               ExtMiscKt.sendMessage(sender, "\u00a76Rewards:");

               for(PickedEntryStack re : RewardRegistry.INSTANCE.getItems()) {
                  ExtMiscKt.sendMessage(sender, re.toString());
               }
            }
         } else if (Intrinsics.areEqual(curr, "reload")) {
            try {
               ExtMiscKt.sendTranslation(sender, "bountiful.reloading.data");
               List bountyBackup = BountyRegistry.INSTANCE.backup();

               for(String msg : BountifulIO.INSTANCE.safeHotReloadAll()) {
                  ExtMiscKt.sendMessage(sender, msg);
               }

               if (BountyRegistry.INSTANCE.getItems().size() < Bountiful.INSTANCE.getConfig().getBountyAmountRange().getLast()) {
                  ExtMiscKt.sendTranslation(sender, "bountiful.toofew.bounties");
                  BountyRegistry.INSTANCE.restore(bountyBackup);
               }

               ExtMiscKt.sendTranslation(sender, "bountiful.reloaded.data");
            } catch (BountyCreationException var17) {
               StringBuilder var10001 = (new StringBuilder()).append("\u00a74");
               String var10002 = var17.getMessage();
               if (var10002 == null) {
                  Intrinsics.throwNpe();
               }

               ExtMiscKt.sendMessage(sender, var10001.append(var10002).toString());
            }
         } else if (Intrinsics.areEqual(curr, "gen")) {
            switch(ArraysKt.drop(args, 1).size()) {
            case 0:
               BountyCreator var47 = BountyCreator.INSTANCE;
               World var10003 = sender.getEntityWorld();
               Intrinsics.checkExpressionValueIsNotNull(var10003, "sender.entityWorld");
               this.safeGivePlayer(sender, IBountyCreator.DefaultImpls.createStack$default(var47, var10003, (EnumBountyRarity)null, 2, (Object)null));
               break;
            default:
               EnumBountyRarity[] var25 = EnumBountyRarity.values();

               EnumBountyRarity var39;
               label93: {
                  for(EnumBountyRarity it : var25) {
                     String $i$f$mapTo = it.name();
                     if ($i$f$mapTo == null) {
                        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
                     }

                     String var10000 = $i$f$mapTo.toLowerCase();
                     Intrinsics.checkExpressionValueIsNotNull(var10000, "(this as java.lang.String).toLowerCase()");
                     if (StringsKt.startsWith$default(var10000, (String)CollectionsKt.first(ArraysKt.drop(args, 1)), false, 2, (Object)null)) {
                        var39 = it;
                        break label93;
                     }
                  }

                  var39 = null;
               }

               EnumBountyRarity rarity = var39;
               if (rarity != null) {
                  BountyCreator var48 = BountyCreator.INSTANCE;
                  World var50 = sender.getEntityWorld();
                  Intrinsics.checkExpressionValueIsNotNull(var50, "sender.entityWorld");
                  this.safeGivePlayer(sender, var48.createStack(var50, rarity));
               } else {
                  ExtMiscKt.sendTranslation(sender, "bountiful.command.gen.invalid");
               }
            }
         } else if (Intrinsics.areEqual(curr, "expire")) {
            Entity var40 = sender.getCommandSenderEntity();
            if (var40 == null) {
               throw new TypeCastException("null cannot be cast to non-null type net.minecraft.entity.player.EntityPlayer");
            }

            EntityPlayer player = (EntityPlayer)var40;
            ItemStack holding = player.getHeldItemMainhand();
            Intrinsics.checkExpressionValueIsNotNull(holding, "holding");
            if (holding.getItem() instanceof ItemBounty) {
               Item var41 = holding.getItem();
               if (var41 == null) {
                  throw new TypeCastException("null cannot be cast to non-null type ejektaflex.bountiful.item.ItemBounty");
               }

               ((ItemBounty)var41).tryExpireBountyTime(holding);
            }
         } else if (Intrinsics.areEqual(curr, "wt")) {
            StringBuilder var45 = (new StringBuilder()).append("Time: ");
            World var49 = sender.getEntityWorld();
            Intrinsics.checkExpressionValueIsNotNull(var49, "sender.entityWorld");
            ExtMiscKt.sendMessage(sender, var45.append(var49.getTotalWorldTime()).toString());
         } else if (Intrinsics.areEqual(curr, "entities")) {
            IForgeRegistry var42 = ForgeRegistries.ENTITIES;
            Intrinsics.checkExpressionValueIsNotNull(ForgeRegistries.ENTITIES, "ForgeRegistries.ENTITIES");
            Set var43 = var42.getEntries();
            Intrinsics.checkExpressionValueIsNotNull(var43, "ForgeRegistries.ENTITIES.entries");
            Iterable $receiver$iv = (Iterable)var43;
            Collection destination$iv$iv = (Collection)(new ArrayList());

            for(Object element$iv$iv : $receiver$iv) {
               Entry it = (Entry)element$iv$iv;
               Object var46 = it.getValue();
               Intrinsics.checkExpressionValueIsNotNull(var46, "it.value");
               if (EntityLiving.class.isAssignableFrom(((EntityEntry)var46).getEntityClass())) {
                  destination$iv$iv.add(element$iv$iv);
               }
            }

            $receiver$iv = (Iterable)((List)destination$iv$iv);
            destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));

            for(Object item$iv$iv : $receiver$iv) {
               Entry var38 = (Entry)item$iv$iv;
               var43 = (Set)var38.getValue();
               Intrinsics.checkExpressionValueIsNotNull(var43, "it.value");
               String var16 = String.valueOf(((EntityEntry)var43).getRegistryName());
               destination$iv$iv.add(var16);
            }

            List names = CollectionsKt.sorted((Iterable)((List)destination$iv$iv));
            ExtMiscKt.sendMessage(sender, CollectionsKt.joinToString$default((Iterable)names, (CharSequence)", ", (CharSequence)null, (CharSequence)null, 0, (CharSequence)null, (Function1)null, 62, (Object)null));
            System.out.println(names);
         }
      } else {
         ExtMiscKt.sendMessage(sender, "Valid commands: '/bo reload', '/bo entities', '/bo gen [c/u/r/e]' (op), '/bo expire' (op)");
      }

   }

   public boolean checkPermission(@NotNull MinecraftServer server, @NotNull ICommandSender sender) {
      Intrinsics.checkParameterIsNotNull(server, "server");
      Intrinsics.checkParameterIsNotNull(sender, "sender");
      return sender instanceof EntityPlayer;
   }

   @Nullable
   public List<String> getTabCompletions(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args, @Nullable BlockPos pos) {
      Intrinsics.checkParameterIsNotNull(server, "server");
      Intrinsics.checkParameterIsNotNull(sender, "sender");
      Intrinsics.checkParameterIsNotNull(args, "args");
      return null;
   }

   public boolean isUsernameIndex(@NotNull String[] args, int index) {
      Intrinsics.checkParameterIsNotNull(args, "args");
      return false;
   }

   public BountyCommand() {
   }
}
