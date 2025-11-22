package penguin.felix.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import penguin.serpentine.core.Config;

public class FelixConfig extends Config {
    public FelixConfig(String modId) {
            super(modId);
    }

    public int default_felix_hp;

    public void expected() {
        this.expect("default_felix_hp", 10);
    }

    public void noFileInit() {
        if (Runtime.getRuntime().availableProcessors() > 8) {
            this.default_felix_hp = 10;
        }

    }

    public void save() {
        Path path = this.getFilePath();
        StringBuilder sb = new StringBuilder();
        this.getValues().forEach((key, value) -> {
            sb.append(key).append(" = ").append(value).append("\n");
        });

        try {
            Files.writeString(path, sb.toString());
        } catch (IOException var4) {
            var4.printStackTrace();
            System.err.println("Failed to save config: " + String.valueOf(path));
        }

    }

    public void updateValues(Map<String, Object> updates) {
        Map<String, Object> updatesCopy = new LinkedHashMap();

        String key;
        Object val;
        for(Iterator var3 = updates.entrySet().iterator(); var3.hasNext(); updatesCopy.put(key, val)) {
            Map.Entry<String, Object> entry = (Map.Entry)var3.next();
            key = (String)entry.getKey();
            val = entry.getValue();
            switch (key) {
                case "default_felix_hp":
                this.default_felix_hp = Integer.parseInt(val.toString());
                break;
                default:
                throw new IllegalArgumentException("Unknown key: " + key);
            }
        }

        super.updateValues(updatesCopy);
    }

    public void afterLoad() {
        if (this.default_felix_hp < 1) {
            this.default_felix_hp = 1;
        }

    }
}
