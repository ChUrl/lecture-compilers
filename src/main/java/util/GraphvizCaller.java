package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class GraphvizCaller {

    private GraphvizCaller() {}

    public static void callGraphviz(CharSequence dot, String filename) {

        final Path dotFile = Paths.get(System.getProperty("user.dir") + "/" + filename + ".dot");
        try {
            Files.writeString(dotFile, dot);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ProcessBuilder dotCompile = new ProcessBuilder("dot", "-Tsvg", "-o" + filename + ".svg", filename + ".dot");
        try {
            dotCompile.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
