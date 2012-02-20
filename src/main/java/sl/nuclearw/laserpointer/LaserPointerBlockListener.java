package sl.nuclearw.laserpointer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class LaserPointerBlockListener implements Listener {
	public static LaserPointer plugin;

	public LaserPointerBlockListener(LaserPointer instance) {
		plugin = instance;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(event.getBlock().getTypeId() != 35) return;
		if(event.getBlock().getData() != (byte) 0x0E) return;
		if(!plugin.pointer.containsValue(event.getBlock().getLocation())) return;
		event.setCancelled(true);
	}
}
