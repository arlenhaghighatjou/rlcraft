package slimeknights.mantle.configurate.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public interface CommentHandler {
   Optional<String> extractHeader(BufferedReader var1) throws IOException;

   Collection<String> toComment(Collection<String> var1);
}
