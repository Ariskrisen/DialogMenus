package dialogmenus.manager;

import io.papermc.paper.dialog.Dialog;
import dialogmenus.DialogMenus;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class MenuManager {

    private final DialogMenus plugin;
    private final File folder;
    private final Map<String, YamlConfiguration> menus = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> actionRegistry = new HashMap<>();

    public MenuManager(DialogMenus plugin, File folder) {
        this.plugin = plugin;
        this.folder = folder;
    }

    public void loadMenus() {
        menus.clear();
        actionRegistry.clear();
        if (!folder.exists())
            return;
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null)
            return;

        for (File file : files) {
            String name = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            menus.put(name.toLowerCase(), config);
            plugin.getLogger().info("Loaded menu: " + name);
        }
    }

    public Map<String, YamlConfiguration> getMenus() {
        return menus;
    }

    public YamlConfiguration getMenu(String name) {
        return menus.get(name.toLowerCase());
    }

    public java.util.Set<String> getMenuNames() {
        return menus.keySet();
    }

    public void registerActions(String key, List<Map<String, Object>> actions) {
        actionRegistry.put(key, actions);
    }

    public List<Map<String, Object>> getActions(String key) {
        return actionRegistry.get(key);
    }
}
