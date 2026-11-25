package me.lucko.spark.common.command.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.lucko.spark.common.activitylog.Activity;
import me.lucko.spark.common.command.Command;
import me.lucko.spark.common.command.CommandModule;
import me.lucko.spark.common.command.CommandResponseHandler;
import me.lucko.spark.common.command.tabcomplete.TabCompleter;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.TextComponent;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.feature.pagination.Pagination;
import me.lucko.spark.lib.adventure.text.format.NamedTextColor;
import me.lucko.spark.lib.adventure.text.format.TextDecoration;

public class ActivityLogModule implements CommandModule, Pagination.Renderer.RowRenderer<Activity> {
   private final Pagination.Builder pagination = Pagination.builder().renderer(new Pagination.Renderer() {
      public Component renderEmpty() {
         return CommandResponseHandler.applyPrefix(Component.text("There are no entries present in the log."));
      }

      public Component renderUnknownPage(int page, int pages) {
         return CommandResponseHandler.applyPrefix(Component.text("Unknown page selected. " + pages + " total pages."));
      }
   }).resultsPerPage(4);

   public ActivityLogModule() {
   }

   public Collection<Component> renderRow(Activity activity, int index) {
      List<Component> reply = new ArrayList(5);
      reply.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text(">", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))).append(Component.space())).append(Component.text("#" + (index + 1), NamedTextColor.WHITE))).append(Component.text(" - ", NamedTextColor.DARK_GRAY))).append(Component.text(activity.getType(), NamedTextColor.YELLOW))).append(Component.text(" - ", NamedTextColor.DARK_GRAY))).append(Component.text(formatDateDiff(activity.getTime()), NamedTextColor.GRAY))).build());
      reply.add(((TextComponent.Builder)((TextComponent.Builder)Component.text().content("  ").append(Component.text("Created by: ", NamedTextColor.GRAY))).append(Component.text(activity.getUser().getName(), NamedTextColor.WHITE))).build());
      TextComponent.Builder valueComponent = (TextComponent.Builder)Component.text().content(activity.getDataValue()).color(NamedTextColor.WHITE);
      if (activity.getDataType().equals("url")) {
         valueComponent.clickEvent(ClickEvent.openUrl(activity.getDataValue()));
      }

      reply.add(((TextComponent.Builder)((TextComponent.Builder)Component.text().content("  ").append(Component.text(Character.toUpperCase(activity.getDataType().charAt(0)) + activity.getDataType().substring(1) + ": ", NamedTextColor.GRAY))).append(valueComponent)).build());
      reply.add(Component.space());
      return reply;
   }

   public void registerCommands(Consumer<Command> consumer) {
      consumer.accept(Command.builder().aliases("activity", "activitylog", "log").argumentUsage("page", "page no").executor((platform, sender, resp, arguments) -> {
         List<Activity> log = platform.getActivityLog().getLog();
         log.removeIf(Activity::shouldExpire);
         if (log.isEmpty()) {
            resp.replyPrefixed(Component.text("There are no entries present in the log."));
         } else {
            int page = Math.max(1, arguments.intFlag("page"));
            Pagination<Activity> activityPagination = this.pagination.<Activity>build(Component.text("Recent spark activity", NamedTextColor.GOLD), this, (value) -> {
               return (void)("/" + platform.getPlugin().getCommandName() + " activity --page " + value);
            });
            List var10000 = activityPagination.render(log, page);
            Objects.requireNonNull(resp);
            var10000.forEach(resp::reply);
         }
      }).tabCompleter((platform, sender, arguments) -> {
         return (void)TabCompleter.completeForOpts(arguments, "--page");
      }).build());
   }

   private static String formatDateDiff(long time) {
      long seconds = (System.currentTimeMillis() - time) / 1000L;
      if (seconds <= 0L) {
         return "now";
      } else {
         long minute = seconds / 60L;
         seconds = seconds % 60L;
         long hour = minute / 60L;
         minute = minute % 60L;
         long day = hour / 24L;
         hour = hour % 24L;
         StringBuilder sb = new StringBuilder();
         if (day != 0L) {
            sb.append(day).append("d ");
         }

         if (hour != 0L) {
            sb.append(hour).append("h ");
         }

         if (minute != 0L) {
            sb.append(minute).append("m ");
         }

         if (seconds != 0L) {
            sb.append(seconds).append("s");
         }

         return sb.toString().trim() + " ago";
      }
   }
}
