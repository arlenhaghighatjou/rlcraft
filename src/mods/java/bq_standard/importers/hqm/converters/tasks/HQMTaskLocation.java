package bq_standard.importers.hqm.converters.tasks;

import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.JsonHelper;
import bq_standard.tasks.TaskLocation;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class HQMTaskLocation {
   public HQMTaskLocation() {
   }

   public ITask[] convertTask(JsonObject json) {
      List<ITask> tList = new ArrayList();

      for(JsonElement element : JsonHelper.GetArray(json, "locations")) {
         if (element instanceof JsonObject) {
            JsonObject jLoc = element.getAsJsonObject();
            TaskLocation task = new TaskLocation();
            task.name = JsonHelper.GetString(jLoc, "name", "New Location");
            task.x = JsonHelper.GetNumber(jLoc, "posX", Integer.valueOf(0)).intValue();
            task.y = JsonHelper.GetNumber(jLoc, "posY", Integer.valueOf(0)).intValue();
            task.z = JsonHelper.GetNumber(jLoc, "posZ", Integer.valueOf(0)).intValue();
            task.dim = JsonHelper.GetNumber(jLoc, "dim", Integer.valueOf(0)).intValue();
            task.range = JsonHelper.GetNumber(jLoc, "radius", Integer.valueOf(-1)).intValue();
            task.hideInfo = JsonHelper.GetString(jLoc, "", "").equalsIgnoreCase("NONE");
            tList.add(task);
         }
      }

      return (ITask[])tList.toArray(new ITask[0]);
   }
}
