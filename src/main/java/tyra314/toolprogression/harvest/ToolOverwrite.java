package tyra314.toolprogression.harvest;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import org.apache.logging.log4j.Level;
import tyra314.toolprogression.ToolProgressionMod;
import tyra314.toolprogression.config.ConfigHandler;

import java.util.HashMap;
import java.util.Map;

public class ToolOverwrite
{
    private final Map<String, Integer> harvest_levels = new HashMap<>();

    public String getConfig()
    {
        StringBuilder res = new StringBuilder();
        String prefix = "";

        for (Map.Entry<String, Integer> entry : harvest_levels.entrySet())
        {
            res.append(prefix);
            res.append(entry.getKey()).append('=').append(entry.getValue());

            prefix = ",";
        }

        return res.toString();
    }

    static String getConfig(Item item)
    {
        if (!(item instanceof ItemTool))
        {
            return null;
        }

        StringBuilder config = new StringBuilder();

        for (String toolclass : item.getToolClasses(new ItemStack(item)))
        {
            int level = item.getHarvestLevel(new ItemStack(item), toolclass, null, null);

            String config_line = String.format("%s=%d", toolclass, level);

            if (config.length() > 0)
            {
                config.append(",").append(config_line);
            } else
            {
                config.append(config_line);
            }
        }

        return config.toString();
    }

    public static void applyToItem(Item item)
    {
        if (!(item instanceof ItemTool))
        {
            return;
        }

        ToolOverwrite overwrite = ConfigHandler.toolOverwrites.get(item);

        if (overwrite != null)
        {
            overwrite.apply((ItemTool) item);
        }
    }

    public static ToolOverwrite readFromConfig(String config)
    {
        ToolOverwrite overwrite = new ToolOverwrite();

        String[] tokens = config.split(",");

        for (String token : tokens)
        {
            String[] tok = token.split("=");

            if (tok.length == 2)
            {
                overwrite.addOverwrite(tok[0], Integer.parseInt(tok[1]));
            } else
            {
                ToolProgressionMod.logger.log(Level.WARN, "Problem parsing tool overwrite: ", config);
            }
        }

        return overwrite;
    }

    public void addOverwrite(String toolclass, int level)
    {
        harvest_levels.put(toolclass, level);
    }

    public void apply(ItemTool item)
    {
        for (Map.Entry<String, Integer> entry : harvest_levels.entrySet())
        {
            item.setHarvestLevel(entry.getKey(), entry.getValue());

            ToolProgressionMod.logger.log(Level.INFO, String.format("Applying overwrite to item %s: %s %d", item.getRegistryName(), entry.getKey(), entry.getValue()));

        }
    }
}
