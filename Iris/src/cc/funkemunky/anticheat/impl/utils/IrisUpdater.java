package cc.funkemunky.anticheat.impl.utils;

import cc.funkemunky.anticheat.Iris;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by George on 16/03/2019 at 14:15.
 */
public class IrisUpdater {

    private int project;
    private URL checkUrl;
    @Getter private String newVersion;

    public IrisUpdater() {
        this.newVersion = Iris.getInstance().getDescription().getVersion();
        this.project = 53721;

        try {
            this.checkUrl = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.project);
        } catch (MalformedURLException ignored) {

        }
    }

    public boolean checkForUpdates() throws Exception {
        URLConnection connection = checkUrl.openConnection();
        this.newVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
        return !Iris.getInstance().getDescription().getVersion().equals(newVersion);
    }
}
