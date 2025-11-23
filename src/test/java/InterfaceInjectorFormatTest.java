import dev.puzzleshq.jigsaw.bytecode.inject.format.InterfaceInjectorFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InterfaceInjectorFormatTest {

    public static void main(String[] args) throws IOException {
        InterfaceInjectorFormat.parseInjector(new String(Files.readAllBytes(Paths.get("src/main/resources/injected_interfaces.inject"))));
    }

}
