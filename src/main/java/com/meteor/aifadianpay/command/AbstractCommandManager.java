package com.meteor.aifadianpay.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.*;

public abstract class AbstractCommandManager implements CommandExecutor, TabCompleter {
    private JavaPlugin plugin;
    private Map<String, SubCmd> commands;
    public AbstractCommandManager(JavaPlugin plugin){
        this.plugin = plugin;
        this.commands = new HashMap<>();
    }


    /**
     * 复写初始化逻辑(注册命令等操作)
     */
    public abstract void init();


    /**
     * 注册指令
     *
     * @param cmd        继承SubCmd的命令类
     */
    public void register(SubCmd cmd){
        this.commands.put(cmd.label(),cmd);
    }


    public static List<String> getSugg(final String arg, final List<String> source) {
        if (source == null) {
            return null;
        }
        final List<String> ret = new ArrayList<String>();
        final List<String> sugg = new ArrayList<String>(source);
        StringUtil.copyPartialMatches(arg, (Iterable)sugg, (Collection)ret);
        Collections.sort(ret);
        return ret;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SubCmd SubCmd = commands.get("help");
        if (args.length > 0 && this.commands.containsKey(args[0])) {
            SubCmd = commands.get(args[0]);
        }
        SubCmd.execute(sender,args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(!(sender instanceof Player)){
            return null;
        }
        if(args.length<=0){
            return null;
        }
        if(args.length==1){
            List<String> sugg = new ArrayList<>();
            for(SubCmd c : commands.values()){
                if (c.hasPerm(sender)) {
                    sugg.remove(c.label());
                }
            }
            return getSugg(args[0],sugg);
        }
        SubCmd SubCmd = commands.get(args[0]);
        if(SubCmd==null){
            return new ArrayList<>(commands.keySet());
        }
        List<String> list = SubCmd.getTab((Player)sender,args.length-1,args);
        return getSugg(args[args.length-1],list);
    }
}
