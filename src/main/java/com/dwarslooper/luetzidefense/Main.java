package com.dwarslooper.luetzidefense;

import com.dwarslooper.luetzidefense.arena.Arena;
import com.dwarslooper.luetzidefense.arena.ArenaManager;
import com.dwarslooper.luetzidefense.characters.CharacterManager;
import com.dwarslooper.luetzidefense.characters.activist.Aggressive;
import com.dwarslooper.luetzidefense.characters.activist.Chemist;
import com.dwarslooper.luetzidefense.characters.activist.DarkMonk;
import com.dwarslooper.luetzidefense.characters.activist.Protester;
import com.dwarslooper.luetzidefense.characters.enemy.Policeman;
import com.dwarslooper.luetzidefense.characters.enemy.RweWorker;
import com.dwarslooper.luetzidefense.commands.MainCommand;
import com.dwarslooper.luetzidefense.game.GameLobby;
import com.dwarslooper.luetzidefense.game.LobbyHandler;
import com.dwarslooper.luetzidefense.gui.ClickableItem;
import com.dwarslooper.luetzidefense.gui.GuiUtils;
import com.dwarslooper.luetzidefense.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

import static com.dwarslooper.luetzidefense.game.LobbyHandler.GAMES;


public final class Main extends JavaPlugin {

    private static Main instance;
    public static Logger LOGGER;
    public static String PREFIX;
    public static String langCode;
    public static CharacterManager CM;
    public static Config config;
    public static Config signs;

    @Override
    public void saveDefaultConfig() {
        super.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        LOGGER = Bukkit.getLogger();

        instance = this;
        config = new Config("data.yml", getDataFolder());
        signs = new Config("signs.yml", getDataFolder());
        Translate.init();

        PREFIX = "§8[§cD§6L§8] ";
        LOGGER.info("                                                                                                                                                                                                   \n" +
                "                                                                                                                                                   bbbbbbbb                                        \n" +
                "LLLLLLLLLLL             uuuuu     uuuuu          tttt                             iiii       BBBBBBBBBBBBBBBBB   lllllll                      iiii b::::::b                     tttt           !!! \n" +
                "L:::::::::L             uuuuu     uuuuu       ttt:::t                            i::::i      B::::::::::::::::B  l:::::l                     i::::ib::::::b                  ttt:::t          !!:!!\n" +
                "L:::::::::L                                   t:::::t                             iiii       B::::::BBBBBB:::::B l:::::l                      iiii b::::::b                  t:::::t          !:::!\n" +
                "LL:::::::LL                                   t:::::t                                        BB:::::B     B:::::Bl:::::l                            b:::::b                  t:::::t          !:::!\n" +
                "  L:::::L               uuuuuu    uuuuuuttttttt:::::ttttttt    zzzzzzzzzzzzzzzzziiiiiii        B::::B     B:::::B l::::l     eeeeeeeeeeee   iiiiiii b:::::bbbbbbbbb    ttttttt:::::ttttttt    !:::!\n" +
                "  L:::::L               u::::u    u::::ut:::::::::::::::::t    z:::::::::::::::zi:::::i        B::::B     B:::::B l::::l   ee::::::::::::ee i:::::i b::::::::::::::bb  t:::::::::::::::::t    !:::!\n" +
                "  L:::::L               u::::u    u::::ut:::::::::::::::::t    z::::::::::::::z  i::::i        B::::BBBBBB:::::B  l::::l  e::::::eeeee:::::eei::::i b::::::::::::::::b t:::::::::::::::::t    !:::!\n" +
                "  L:::::L               u::::u    u::::utttttt:::::::tttttt    zzzzzzzz::::::z   i::::i        B:::::::::::::BB   l::::l e::::::e     e:::::ei::::i b:::::bbbbb:::::::btttttt:::::::tttttt    !:::!\n" +
                "  L:::::L               u::::u    u::::u      t:::::t                z::::::z    i::::i        B::::BBBBBB:::::B  l::::l e:::::::eeeee::::::ei::::i b:::::b    b::::::b      t:::::t          !:::!\n" +
                "  L:::::L               u::::u    u::::u      t:::::t               z::::::z     i::::i        B::::B     B:::::B l::::l e:::::::::::::::::e i::::i b:::::b     b:::::b      t:::::t          !:::!\n" +
                "  L:::::L               u::::u    u::::u      t:::::t              z::::::z      i::::i        B::::B     B:::::B l::::l e::::::eeeeeeeeeee  i::::i b:::::b     b:::::b      t:::::t          !!:!!\n" +
                "  L:::::L         LLLLLLu:::::uuuu:::::u      t:::::t    tttttt   z::::::z       i::::i        B::::B     B:::::B l::::l e:::::::e           i::::i b:::::b     b:::::b      t:::::t    tttttt !!! \n" +
                "LL:::::::LLLLLLLLL:::::Lu:::::::::::::::uu    t::::::tttt:::::t  z::::::zzzzzzzzi::::::i     BB:::::BBBBBB::::::Bl::::::le::::::::e         i::::::ib:::::bbbbbb::::::b      t::::::tttt:::::t     \n" +
                "L::::::::::::::::::::::L u:::::::::::::::u    tt::::::::::::::t z::::::::::::::zi::::::i     B:::::::::::::::::B l::::::l e::::::::eeeeeeee i::::::ib::::::::::::::::b       tt::::::::::::::t !!! \n" +
                "L::::::::::::::::::::::L  uu::::::::uu:::u      tt:::::::::::ttz:::::::::::::::zi::::::i     B::::::::::::::::B  l::::::l  ee:::::::::::::e i::::::ib:::::::::::::::b          tt:::::::::::tt!!:!!\n" +
                "LLLLLLLLLLLLLLLLLLLLLLLL    uuuuuuuu  uuuu        ttttttttttt  zzzzzzzzzzzzzzzzziiiiiiii     BBBBBBBBBBBBBBBBB   llllllll    eeeeeeeeeeeeee iiiiiiiibbbbbbbbbbbbbbbb             ttttttttttt   !!! \n" +
                "                                                                                                                                                                                                   \n" +
                "                                                                                                                                                                                                   \n" +
                "                                                                                                                                                                                                   \n" +
                "                                                                                                                                                                                                   \n" +
                "                                                                                                                                                                                                   \n" +
                "                                                                                                                                                                                                   \n" +
                "                                                                                                                                                                                                   ");

        getServer().getConsoleSender().sendMessage(PREFIX + "§f--==[§a+§f]==--".repeat(4));

        getServer().getConsoleSender().sendMessage(PREFIX + "§eSuccessfully enabled DefendLuetzi!");
        getServer().getConsoleSender().sendMessage(PREFIX + "§cPlease notice this is a dev build! Do not use this on production!");
        getServer().getConsoleSender().sendMessage(PREFIX + "§eFor more information join my Discord: §9§n§odsc.gg/dwars");
        getServer().getConsoleSender().sendMessage(PREFIX + "§eLützi bleibt!");

        getServer().getConsoleSender().sendMessage(PREFIX + "§f--==[§a+§f]==--".repeat(4));

        CM = new CharacterManager();

        CM.add(new Protester());
        CM.add(new Aggressive());
        CM.add(new Chemist());
        CM.add(new DarkMonk());

        CM.add(new RweWorker());
        CM.add(new Policeman());

        CM.updateCharacters();

        MainCommand mainCmd = new MainCommand();
        Objects.requireNonNull(getCommand("dl")).setExecutor(mainCmd);
        Objects.requireNonNull(getCommand("dl")).setTabCompleter(mainCmd);

        saveDefaultConfig();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ClickListener(), this);
        pluginManager.registerEvents(new ChatListener(), this);
        pluginManager.registerEvents(new InteractListener(), this);
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new ServerListener(), this);

        GuiUtils.register();
        ArenaManager.reload();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for(Arena a : ArenaManager.ARENAS.values()) {
            if(LobbyHandler.gameUsesArena(GAMES, a) != null) LobbyHandler.resetGame(a);
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }
}
