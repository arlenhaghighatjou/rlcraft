package wiresegal.classyhats.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SpreadBuilder;
import kotlin.ranges.RangesKt;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wiresegal.classyhats.ClassyHatsConfig;
import wiresegal.classyhats.ClassyHatsContent;
import wiresegal.classyhats.capability.CapabilityHatContainer;
import wiresegal.classyhats.capability.data.IHatContainer;
import wiresegal.classyhats.event.CapabilityEvents;
import wiresegal.classyhats.item.ItemHat;
import wiresegal.classyhats.util.HatData;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J-\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u000e\u0010\u000e\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u000fH\u0016¢\u0006\u0002\u0010\u0010J\b\u0010\u0011\u001a\u00020\u0004H\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016J;\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00040\u00072\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\u000f2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016H\u0016¢\u0006\u0002\u0010\u0017J\u0014\u0010\u0018\u001a\u00020\u00042\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u0004H\u0002J\u0012\u0010\u0018\u001a\u00020\u00042\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0016J#\u0010\u001a\u001a\u00020\u001b2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\u000f2\u0006\u0010\u001c\u001a\u00020\u0013H\u0016¢\u0006\u0002\u0010\u001dR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082D¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00040\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"},
   d2 = {"Lwiresegal/classyhats/command/CommandChangeHat;", "Lnet/minecraft/command/CommandBase;", "()V", "msgFail", "", "msgSuccess", "pages", "", "execute", "", "server", "Lnet/minecraft/server/MinecraftServer;", "sender", "Lnet/minecraft/command/ICommandSender;", "args", "", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;[Ljava/lang/String;)V", "getName", "getRequiredPermissionLevel", "", "getTabCompletions", "targetPos", "Lnet/minecraft/util/math/BlockPos;", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;[Ljava/lang/String;Lnet/minecraft/util/math/BlockPos;)Ljava/util/List;", "getUsage", "key", "isUsernameIndex", "", "index", "([Ljava/lang/String;I)Z", "classyhats"}
)
public final class CommandChangeHat extends CommandBase {
   private static final String msgFail = "commands.replaceitem.failed";
   private static final String msgSuccess = "commands.replaceitem.success";
   private static final List<String> pages;
   public static final CommandChangeHat INSTANCE;

   @NotNull
   public String getName() {
      return "replacehat";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }

   public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) {
      Intrinsics.checkParameterIsNotNull(server, "server");
      Intrinsics.checkParameterIsNotNull(sender, "sender");
      Intrinsics.checkParameterIsNotNull(args, "args");
      if (args.length < 2) {
         throw (Throwable)(new WrongUsageException(getUsage$default(this, (String)null, 1, (Object)null), new Object[0]));
      } else {
         String var4 = args[1];
         switch(var4.hashCode()) {
         case 3433103:
            if (!var4.equals("page")) {
               throw (Throwable)(new WrongUsageException(getUsage$default(this, (String)null, 1, (Object)null), new Object[0]));
            }

            if (args.length < 4) {
               throw (Throwable)(new WrongUsageException(this.getUsage("page"), new Object[0]));
            }

            EntityPlayerMP player = CommandBase.getPlayer(server, sender, args[0]);
            CapabilityHatContainer.Companion var21 = CapabilityHatContainer.Companion;
            Intrinsics.checkExpressionValueIsNotNull(player, "player");
            IHatContainer cap = var21.getCapability((EntityPlayer)player);
            String var17 = "page\\.(\\d+)\\.(\\d+)";
            MatchResult var22 = (new Regex(var17)).matchEntire((CharSequence)args[2]);
            if (var22 == null) {
               throw (Throwable)(new CommandException(this.getUsage("invalid.page"), new Object[]{args[2]}));
            }

            MatchResult match = var22;
            hatId = args[3];
            String hatIdx = (String)match.getGroupValues().get(1);
            int hatPage = Integer.parseInt(hatIdx);
            String pos = (String)match.getGroupValues().get(2);
            int hatIdx = Integer.parseInt(pos);
            if (0 > hatPage) {
               throw (Throwable)(new CommandException(this.getUsage("invalid.page"), new Object[]{args[2]}));
            }

            if (20 <= hatPage) {
               throw (Throwable)(new CommandException(this.getUsage("invalid.page"), new Object[]{args[2]}));
            }

            if (0 > hatIdx) {
               throw (Throwable)(new CommandException(this.getUsage("invalid.page"), new Object[]{args[2]}));
            }

            if (50 <= hatIdx) {
               throw (Throwable)(new CommandException(this.getUsage("invalid.page"), new Object[]{args[2]}));
            }

            int pos = hatPage * 50 + hatIdx;
            ItemStack stack = Intrinsics.areEqual(var17, "none") ^ true ? ItemHat.ofHat$default(ClassyHatsContent.INSTANCE.getHAT(), var17, 0, 2, (Object)null) : ItemStack.EMPTY;
            ItemHat var23 = ClassyHatsContent.INSTANCE.getHAT();
            ItemStack var26 = cap.getHats().getStackInSlot(pos);
            Intrinsics.checkExpressionValueIsNotNull(var26, "cap.hats.getStackInSlot(pos)");
            HatData var24 = var23.getHat(var26);
            ItemHat var27 = ClassyHatsContent.INSTANCE.getHAT();
            Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
            if (Intrinsics.areEqual(var24, var27.getHat(stack))) {
               throw (Throwable)(new CommandException(msgFail, new Object[]{args[2], Integer.valueOf(1), stack.getTextComponent()}));
            }

            cap.getHats().setStackInSlot(pos, stack);
            CommandBase.notifyCommandListener(sender, (ICommand)this, msgSuccess, new Object[]{args[2], Integer.valueOf(1), stack.getTextComponent()});
            CapabilityEvents.INSTANCE.syncDataFor((EntityPlayer)player, player);
            break;
         case 588913375:
            if (var4.equals("equipped")) {
               if (args.length < 3) {
                  throw (Throwable)(new WrongUsageException(this.getUsage("equipped"), new Object[0]));
               }

               Entity player = CommandBase.getEntity(server, sender, args[0]);
               String hatId = args[2];
               ItemStack stack = Intrinsics.areEqual(hatId, "none") ^ true ? ItemHat.ofHat$default(ClassyHatsContent.INSTANCE.getHAT(), hatId, 0, 2, (Object)null) : ItemStack.EMPTY;
               if (player instanceof EntityPlayerMP) {
                  IHatContainer cap = CapabilityHatContainer.Companion.getCapability((EntityPlayer)player);
                  HatData var10000 = ClassyHatsContent.INSTANCE.getHAT().getHat(cap.getEquipped());
                  ItemHat var25 = ClassyHatsContent.INSTANCE.getHAT();
                  Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
                  if (Intrinsics.areEqual(var10000, var25.getHat(stack))) {
                     throw (Throwable)(new CommandException(msgFail, new Object[]{"equipped", Integer.valueOf(1), stack.isEmpty() ? "Air" : stack.getTextComponent()}));
                  }

                  cap.setEquipped(stack);
                  CommandBase.notifyCommandListener(sender, (ICommand)this, msgSuccess, new Object[]{"equipped", Integer.valueOf(1), stack.isEmpty() ? "Air" : stack.getTextComponent()});
                  CapabilityEvents.INSTANCE.syncDataFor((EntityPlayer)player, (EntityPlayerMP)player);
                  break;
               } else {
                  if (ClassyHatsConfig.INSTANCE.getNames().contains(String.valueOf(EntityList.getKey(player))) && player instanceof EntityLiving) {
                     String prev = ((EntityLiving)player).getEntityData().getString(CapabilityEvents.INSTANCE.getCustomKey());
                     if (Intrinsics.areEqual(prev, hatId)) {
                        String var29 = msgFail;
                        Object[] var31 = new Object[]{"equipped", Integer.valueOf(1), null};
                        Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
                        var31[2] = stack.isEmpty() ? "Air" : stack.getTextComponent();
                        throw (Throwable)(new CommandException(var29, var31));
                     }

                     player.readFromNBT(player.writeToNBT(new NBTTagCompound()));
                     if (Intrinsics.areEqual(hatId, "none")) {
                        ((EntityLiving)player).getEntityData().setString(CapabilityEvents.INSTANCE.getCustomKey(), "");
                     } else {
                        ((EntityLiving)player).getEntityData().setString(CapabilityEvents.INSTANCE.getCustomKey(), hatId);
                     }

                     ICommand var10001 = (ICommand)this;
                     String var28 = msgSuccess;
                     Object[] var30 = new Object[]{"equipped", Integer.valueOf(1), null};
                     Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
                     var30[2] = stack.isEmpty() ? "Air" : stack.getTextComponent();
                     CommandBase.notifyCommandListener(sender, var10001, var28, var30);
                     break;
                  }

                  String var10002 = msgFail;
                  Object[] var10003 = new Object[]{"equipped", Integer.valueOf(1), null};
                  Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
                  var10003[2] = stack.isEmpty() ? "Air" : stack.getTextComponent();
                  throw (Throwable)(new CommandException(var10002, var10003));
               }
            }

            throw (Throwable)(new WrongUsageException(getUsage$default(this, (String)null, 1, (Object)null), new Object[0]));
         default:
            throw (Throwable)(new WrongUsageException(getUsage$default(this, (String)null, 1, (Object)null), new Object[0]));
         }

      }
   }

   private final String getUsage(String key) {
      return "command.classyhats.replacehat.usage" + (key == null ? "" : '.' + key);
   }

   @NotNull
   public String getUsage(@Nullable ICommandSender sender) {
      return getUsage$default(this, (String)null, 1, (Object)null);
   }

   @NotNull
   public List<String> getTabCompletions(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args, @Nullable BlockPos targetPos) {
      Intrinsics.checkParameterIsNotNull(server, "server");
      Intrinsics.checkParameterIsNotNull(sender, "sender");
      Intrinsics.checkParameterIsNotNull(args, "args");
      switch(args.length) {
      case 1:
         String[] var10001 = server.getOnlinePlayerNames();
         List var17 = CommandBase.getListOfStringsMatchingLastWord(args, (String[])Arrays.copyOf(var10001, var10001.length));
         Intrinsics.checkExpressionValueIsNotNull(var17, "CommandBase.getListOfStr…server.onlinePlayerNames)");
         return var17;
      case 2:
         List var10000 = CommandBase.getListOfStringsMatchingLastWord(args, new String[]{"equipped", "page"});
         Intrinsics.checkExpressionValueIsNotNull(var10000, "CommandBase.getListOfStr…args, \"equipped\", \"page\")");
         return var10000;
      default:
         String var5 = args[1];
         switch(var5.hashCode()) {
         case 3433103:
            if (var5.equals("page")) {
               switch(args.length) {
               case 3:
                  List var22 = CommandBase.getListOfStringsMatchingLastWord(args, (Collection)pages);
                  Intrinsics.checkExpressionValueIsNotNull(var22, "CommandBase.getListOfStr…hingLastWord(args, pages)");
                  return var22;
               case 4:
                  SpreadBuilder var24 = new SpreadBuilder(2);
                  var24.add("none");
                  Collection var13 = (Collection)ClassyHatsConfig.INSTANCE.getHats().keySet();
                  SpreadBuilder var15 = var24;
                  SpreadBuilder var14 = var24;
                  if (var13 == null) {
                     throw new TypeCastException("null cannot be cast to non-null type java.util.Collection<T>");
                  }

                  Object[] var20 = var13.toArray(new String[0]);
                  if (var20 == null) {
                     throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
                  }

                  Object[] var16 = var20;
                  var15.addSpread(var16);
                  List var21 = CommandBase.getListOfStringsMatchingLastWord(args, (String[])var14.toArray(new String[var14.size()]));
                  Intrinsics.checkExpressionValueIsNotNull(var21, "CommandBase.getListOfStr…                        )");
                  return var21;
               }
            }
            break;
         case 588913375:
            if (var5.equals("equipped") && args.length == 3) {
               SpreadBuilder var23 = new SpreadBuilder(2);
               var23.add("none");
               Collection $receiver$iv = (Collection)ClassyHatsConfig.INSTANCE.getHats().keySet();
               SpreadBuilder var11 = var23;
               SpreadBuilder var10 = var23;
               if ($receiver$iv == null) {
                  throw new TypeCastException("null cannot be cast to non-null type java.util.Collection<T>");
               }

               Object[] var18 = $receiver$iv.toArray(new String[0]);
               if (var18 == null) {
                  throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
               }

               Object[] var12 = var18;
               var11.addSpread(var12);
               List var19 = CommandBase.getListOfStringsMatchingLastWord(args, (String[])var10.toArray(new String[var10.size()]));
               Intrinsics.checkExpressionValueIsNotNull(var19, "CommandBase.getListOfStr…                        )");
               return var19;
            }
         }

         return CollectionsKt.emptyList();
      }
   }

   public boolean isUsernameIndex(@NotNull String[] args, int index) {
      Intrinsics.checkParameterIsNotNull(args, "args");
      return index == 0;
   }

   private CommandChangeHat() {
   }

   static {
      CommandChangeHat var0 = new CommandChangeHat();
      INSTANCE = var0;
      Iterable $receiver$iv = (Iterable)RangesKt.until(0, 20);
      Collection destination$iv$iv = (Collection)(new ArrayList());
      Iterator var4 = $receiver$iv.iterator();

      while(var4.hasNext()) {
         int element$iv$iv = ((IntIterator)var4).nextInt();
         int k = element$iv$iv;
         Iterable $receiver$iv = (Iterable)RangesKt.until(0, 50);
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));
         Iterator var10 = $receiver$iv.iterator();

         while(var10.hasNext()) {
            int item$iv$iv = ((IntIterator)var10).nextInt();
            String var14 = "page." + k + '.' + item$iv$iv;
            destination$iv$iv.add(var14);
         }

         Iterable list$iv$iv = (Iterable)((List)destination$iv$iv);
         CollectionsKt.addAll(destination$iv$iv, list$iv$iv);
      }

      pages = (List)destination$iv$iv;
   }
}
