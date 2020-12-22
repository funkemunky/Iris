package cc.funkemunky.anticheat.api.utils.menu.mask;

import cc.funkemunky.anticheat.api.utils.menu.Menu;
import cc.funkemunky.anticheat.api.utils.menu.button.Button;

public interface Mask {

    Mask setButton(char key, Button button);

    Mask setMaskPattern(String... maskPattern);

    void applyTo(Menu menu);
}
