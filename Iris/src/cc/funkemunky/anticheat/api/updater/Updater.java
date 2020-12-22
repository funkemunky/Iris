package cc.funkemunky.anticheat.api.updater;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Getter
@Init
public class Updater {
    private int update = - 1, currentUpdate = 8;
    private String version;
    private boolean importantUpdate = false;

    @ConfigSetting(path = "updater", name = "checkForUpdates")
    private boolean checkForUpdates = true;

    public Updater() {
        if(checkForUpdates) {
            String[] toSort = readFromUpdaterPastebin();

            if(toSort.length > 0) {
                update = Integer.parseInt(toSort[0]);
                version = toSort[1];
            } else {
                version = "N/A";
            }
        } else {
            version = Atlas.getInstance().getDescription().getVersion();

        }
    }

    private String[] readFromUpdaterPastebin() {
        try {
            URL url = new URL("https://pastebin.com/raw/fX2Ebkpz");
            URLConnection connection = url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = reader.readLine();

            if(line != null) return line.split(";");
        } catch(Exception e) {
            e.printStackTrace();
        }

        return new String[0];
    }

    public boolean needsToUpdate() {
        return update > currentUpdate;
    }

    public boolean needsToUpdateIfImportant() {
        return importantUpdate && update > currentUpdate;
    }
}
