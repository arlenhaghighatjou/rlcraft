package ivorius.ivtoolkit.maze.components;

import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.Set;

public interface MazeComponent<C> {
   Set<MazeRoom> rooms();

   Map<MazePassage, C> exits();

   Multimap<MazePassage, MazePassage> reachability();
}
