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
    public float default_walkspeed;
    public float goo_ball_hunger_gain;
    public int goo_ball_saturation;
    public float goo_bucket_hunger_gain;
    public int goo_bucket_saturation;
    public float goo_bucket_health_gain;
    public float goo_stick_hunger_gain;
    public float goo_stick_health_gain;

    public void expected() {
        this.expect("default felix hp", 10);
        this.expect("default felix walkspeed", 0.35F);
        this.expect("cum ball hunger gain", 1.0F);
        this.expect("cum ball saturation", 0);
        this.expect("cum bucket hunger gain", 3.0F);
        this.expect("cum bucket saturation", 1);
        this.expect("cum bucket health gain", 4.0F);
        this.expect("cum popsicle hunger gain", 2.0F);
        this.expect("cum popsicle health gain", 2.0F);
    }

    public void noFileInit() {
        if (Runtime.getRuntime().availableProcessors() > 8) {
            this.default_felix_hp = 10;
            this.default_walkspeed = 0.35F;
            this.goo_ball_hunger_gain = 1.0F;
            this.goo_ball_saturation = 0;
            this.goo_bucket_saturation = 1;
            this.goo_bucket_hunger_gain = 3.0F;
            this.goo_bucket_health_gain = 4.0F;
            this.goo_stick_hunger_gain = 2.0F;
            this.goo_stick_health_gain = 2.0F;
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
                case "default felix hp":
                    this.default_felix_hp = Integer.parseInt(val.toString());
                    break;
                case "default felix walkspeed":
                    this.default_walkspeed = Float.parseFloat(val.toString());
                    break;
                case "cum ball hunger gain":
                    this.goo_ball_hunger_gain = Float.parseFloat(val.toString());
                    break;
                case "cum ball saturation":
                    this.goo_ball_saturation = Integer.parseInt(val.toString());
                    break;
                case "cum bucket hunger gain":
                    this.goo_bucket_hunger_gain = Float.parseFloat(val.toString());
                    break;
                case "cum bucket saturation":
                    this.goo_bucket_saturation = Integer.parseInt(val.toString());
                    break;
                case "cum bucket health gain":
                    this.goo_bucket_health_gain = Float.parseFloat(val.toString());
                    break;
                case "cum popsicle hunger gain":
                    this.goo_stick_hunger_gain = Float.parseFloat(val.toString());
                    break;
                case "cum popsicle health gain":
                    this.goo_stick_health_gain = Float.parseFloat(val.toString());
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
        if (this.default_walkspeed < 0.35) {
            this.default_walkspeed = 0.35F;
        }

    }
}
