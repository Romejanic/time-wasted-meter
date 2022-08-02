package macs.timewasted.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import macs.timewasted.meter.BarPreferences;
import macs.timewasted.meter.BarPreferences.Prefs;
import macs.timewasted.meter.TimeMeter;
import macs.timewasted.util.Util;

public class CommandTimeMeter implements CommandExecutor, TabCompleter {

	private BarPreferences prefs;
	private TimeMeter meter;
	
	public CommandTimeMeter(BarPreferences prefs, TimeMeter meter) {
		this.prefs = prefs;
		this.meter = meter;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!Util.isPlayer(sender)) {
			sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
			return true;
		}
		if(args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Usage: " + command.getUsage());
			return true;
		}
		
		Player target = (Player)sender;
		String property = args[0].toLowerCase();
		
		Prefs prefs = this.prefs.forPlayer(target);
		
		switch(property) {
		case "show":
			prefs.hidden = false;
			this.meter.updatePlayerPrefs(target, prefs);
			this.prefs.save();
			sender.sendMessage(ChatColor.GREEN + "Time meter is now shown");
			break;
		case "hide":
			prefs.hidden = true;
			this.meter.updatePlayerPrefs(target, prefs);
			this.prefs.save();
			sender.sendMessage(ChatColor.GREEN + "Time meter is now hidden");
			break;
		case "colour":
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + label + " colour <colour>");
			} else {
				try {
					String input = args[1].toUpperCase();
					BarColor color = BarColor.valueOf(input);
					prefs.color = color;
					this.meter.updatePlayerPrefs(target, prefs);
					this.prefs.save();
					sender.sendMessage(ChatColor.GREEN + "Changed meter colour to " + color.name().toLowerCase());
				} catch(IllegalArgumentException e) {
					sender.sendMessage(ChatColor.RED + "Invalid colour: " + args[1]);
				}
			}
			break;
		case "style":
			if(args.length < 2) {
				sender.sendMessage(ChatColor.RED + "Usage: /" + label + " style <style>");
			} else {
				try {
					String input = args[1].toUpperCase();
					BarStyle style = BarStyle.valueOf(input);
					prefs.style = style;
					this.meter.updatePlayerPrefs(target, prefs);
					this.prefs.save();
					sender.sendMessage(ChatColor.GREEN + "Changed meter style to " + style.name().toLowerCase());
				} catch(IllegalArgumentException e) {
					sender.sendMessage(ChatColor.RED + "Invalid style: " + args[1]);
				}
			}
			break;
		default:
			sender.sendMessage(ChatColor.RED + "Unknown property: " + property);
			sender.sendMessage(ChatColor.RED + "Usage: " + command.getUsage());
			break;
		}
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> options = Lists.newArrayList();
		if(!Util.isPlayer(sender)) {
			return options;
		}
		
		switch(args.length) {
		case 1:
			options.addAll(Arrays.asList("show", "hide", "colour", "style"));
			break;
		case 2:
			if(args[0].equalsIgnoreCase("colour")) {
				options.addAll(
					Arrays.asList(BarColor.values())
					.stream()
					.map(c -> c.name().toLowerCase())
					.collect(Collectors.toList())
				);
			} else if(args[0].equalsIgnoreCase("style")) {
				options.addAll(
					Arrays.asList(BarStyle.values())
					.stream()
					.map(c -> c.name().toLowerCase())
					.collect(Collectors.toList())
				);
			}
		default:
			break;
		}
		
		return options.stream()
					.filter(s -> s.startsWith(args[args.length-1].toLowerCase()))
					.collect(Collectors.toList());
	}

}