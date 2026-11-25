package greymerk.roguelike.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CommandRouteBase implements ICommandRouter {
   private final Map<String, ICommandRouter> routes = new HashMap();
   protected final CommandBase commandBase;

   public CommandRouteBase(CommandBase commandBase) {
      this.commandBase = commandBase;
   }

   protected void addRoute(String id, ICommandRouter route) {
      this.routes.put(id, route);
   }

   public void execute(CommandContext1_12 context, List<String> args) {
      if (args.size() > 0 && this.routes.containsKey(args.get(0))) {
         List<String> tail = new ArrayList(args);
         String head = (String)tail.remove(0);
         ((ICommandRouter)this.routes.get(head)).execute(context, tail);
      }

   }

   public List<String> getTabCompletion(List<String> args) {
      if (args.size() == 1) {
         return this.getListTabOptions((String)args.get(0), this.routes.keySet());
      } else {
         if (args.size() > 1) {
            List<String> tail = new ArrayList(args);
            String head = (String)tail.remove(0);
            if (this.routes.containsKey(head)) {
               return ((ICommandRouter)this.routes.get(head)).getTabCompletion(tail);
            }
         }

         return Collections.emptyList();
      }
   }

   protected List<String> getListTabOptions(String name, Collection<String> possibilities) {
      List<String> options = new ArrayList();

      for(String item : possibilities) {
         if (this.commandBase.doesStringStartWith(name, item)) {
            options.add(item);
         }
      }

      return options;
   }
}
