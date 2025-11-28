package penguin.felix.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import penguin.serpentine.core.example.ExampleConfig;
import penguin.serpentine.network.ConfigNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
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
        this.expect("default_felix_hp", 10, SyncSide.SERVER_ONLY);
        this.expect("default_felix_walkspeed", 0.35F, SyncSide.SERVER_ONLY);
        this.expect("cum_ball_hunger_gain", 1.0F, SyncSide.SERVER_ONLY);
        this.expect("cum_ball_saturation", 0, SyncSide.SERVER_ONLY);
        this.expect("cum_bucket_hunger_gain", 3.0F, SyncSide.SERVER_ONLY);
        this.expect("cum_bucket_saturation", 1, SyncSide.SERVER_ONLY);
        this.expect("cum_bucket_health_gain", 4.0F, SyncSide.SERVER_ONLY);
        this.expect("cum_popsicle_hunger_gain", 2.0F, SyncSide.SERVER_ONLY);
        this.expect("cum_popsicle_health_gain", 2.0F, SyncSide.SERVER_ONLY);
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

    public void applyServerEdit(ServerPlayerEntity player, String key, String valueStr) {
        Config.SyncSide side = (Config.SyncSide)this.syncSide.get(key);
        if (!this.expectedDefaults.containsKey(key)) {
            throw new IllegalArgumentException("Unknown config key: " + key);
        } else if (side == SyncSide.CLIENT_ONLY) {
            throw new IllegalArgumentException("Client-only config cannot be changed by server: " + key);
        } else {
            Object defaultVal = this.expectedDefaults.get(key);

            Object parsedVal;
            try {
                if (defaultVal instanceof Boolean) {
                parsedVal = Boolean.parseBoolean(valueStr);
                } else if (defaultVal instanceof Integer) {
                parsedVal = Integer.parseInt(valueStr);
                } else if (defaultVal instanceof Float) {
                parsedVal = Float.parseFloat(valueStr);
                } else if (defaultVal instanceof Double) {
                parsedVal = Double.parseDouble(valueStr);
                } else {
                parsedVal = valueStr;
                }
            } catch (Exception var10) {
                throw new IllegalArgumentException("Failed to parse value for " + key + ": " + valueStr, var10);
            }

            this.values.put(key, parsedVal);
            this.save();
            MinecraftServer server = player.getServer();
            if (server != null) {
                Iterator var8 = server.getPlayerManager().getPlayerList().iterator();

                while(var8.hasNext()) {
                ServerPlayerEntity target = (ServerPlayerEntity)var8.next();
                ConfigNetworking.sendS2CSync(target, this, false);
                }
            }

        }
    }
}
