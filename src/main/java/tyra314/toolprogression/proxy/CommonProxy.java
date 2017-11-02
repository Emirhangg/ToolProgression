package tyra314.toolprogression.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import tyra314.toolprogression.ToolProgressionMod;
import tyra314.toolprogression.compat.tconstruct.TiCHelper;
import tyra314.toolprogression.compat.tconstruct.TiCMiningLevels;
import tyra314.toolprogression.compat.waila.WailaPlugin;
import tyra314.toolprogression.config.ConfigHandler;
import tyra314.toolprogression.handlers.HarvestEventHandler;
import tyra314.toolprogression.harvest.OverwriteHelper;

import java.io.File;


public class CommonProxy
{

    // ConfigHandler INSTANCE
    public static Configuration base_config;
    public static Configuration mining_level_config;

    public static Configuration blocks_config;
    public static Configuration block_overwrites_config;
    public static Configuration tools_config;
    public static Configuration tool_overwrites_config;
    public static Configuration mats_config;
    public static Configuration mat_overwrites_config;

    public void preInit(FMLPreInitializationEvent e)
    {
        File directory = e.getModConfigurationDirectory();
        base_config =
                new Configuration(new File(directory.getPath(), "tool_progression/general.cfg"));
        mining_level_config =
                new Configuration(new File(directory.getPath(),
                        "tool_progression/mining_level_names.cfg"));

        blocks_config =
                new Configuration(new File(directory.getPath(), "tool_progression/blocks.cfg"));
        block_overwrites_config =
                new Configuration(new File(directory.getPath(),
                        "tool_progression/block_overwrites.cfg"));

        tools_config =
                new Configuration(new File(directory.getPath(), "tool_progression/tools.cfg"));
        tool_overwrites_config =
                new Configuration(new File(directory.getPath(),
                        "tool_progression/tool_overwrites.cfg"));

        mats_config = new Configuration(new File(directory.getPath(),
                "tool_progression/materials.cfg"));
        mat_overwrites_config =
                new Configuration(new File(directory.getPath(),
                        "tool_progression/materials_overwrites.cfg"));

        ConfigHandler.readBaseConfig();

        if (TiCHelper.isLoaded())
        {
            TiCHelper.preInit();
        }

        WailaPlugin.preInit();
    }

    public void init(@SuppressWarnings("unused") FMLInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(new HarvestEventHandler());

        if (TiCHelper.isLoaded())
        {
            TiCHelper.init();
            TiCMiningLevels.overwriteMiningLevels();
        }
    }

    public void postInit(@SuppressWarnings("unused") FMLPostInitializationEvent e)
    {
        blocks_config.addCustomCategoryComment("block",
                "The list of all block harvest levels with toolclass\nThis file will be generated on every launch\nDO NOT EDIT THIS FILE");

        tools_config.addCustomCategoryComment("tool",
                "The list of all tool harvest levels with toolclass\nThis file will be generated on every launch\nDO NOT EDIT THIS FILE");

        mats_config.addCustomCategoryComment("material",
                "The list of all tool materials with harvest level\nThis file will be generated " +
                "on every launch\nDO NOT EDIT THIS FILE");


        ToolProgressionMod.logger.info("Start doing stupid things");

        final IForgeRegistry<Block> block_registry = GameRegistry.findRegistry(Block.class);

        for (Block block : block_registry)
        {
            OverwriteHelper.handleBlock(block);
        }

        final IForgeRegistry<Item> item_registry = GameRegistry.findRegistry(Item.class);

        for (Item item : item_registry)
        {
            if (item instanceof ItemTool || item instanceof ItemHoe)
            {
                OverwriteHelper.handleItem(item);
            }
        }

        for (ItemTool.ToolMaterial mat : ItemTool.ToolMaterial.values())
        {
            OverwriteHelper.handleMaterial(mat);
        }

        ToolProgressionMod.logger.info("Finished doing stupid things");

        if (blocks_config.hasChanged())
        {
            blocks_config.save();
        }

        if (tools_config.hasChanged())
        {
            tools_config.save();
        }

        if (mats_config.hasChanged())
        {
            mats_config.save();
        }
    }
}

