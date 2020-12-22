package cc.funkemunky.anticheat.api.utils.updater;

import cc.funkemunky.anticheat.Iris;
import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.updater.UpdaterUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Updater {

    public static void downloadAppropriateVersion() {
        File pluginLocation = UpdaterUtils.findPluginFile(Atlas.getInstance().getDescription().getName());
        String link = "https://github.com/funkemunky/Atlas/releases/download/%ver%/Atlas.jar".replaceAll("%ver%", Iris.getInstance().getRequiredVersionsOfAtlas()[0]);

        try {
            InputStream in = new URL(link).openStream();
            Files.copy(in, Paths.get(pluginLocation.getPath()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
