package sl.nuclearw.laserpointer;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class LaserPointerPlayerListener implements Listener {
	public static LaserPointer plugin;

	public LaserPointerPlayerListener(LaserPointer instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		//We paying attention?
		if(!plugin.pointer.containsKey(event.getPlayer())) return;
		//We looking at our pointer?
		if(plugin.pointer.get(event.getPlayer()).equals(event.getPlayer().getTargetBlock(null, 500).getLocation())) return;
		//No, well lets move it
		plugin.pointer.get(event.getPlayer()).getBlock().setTypeId(0);
		Block tBlock = event.getPlayer().getLastTwoTargetBlocks(null, 500).get(0);
		if(tBlock.getTypeId() != 0) return;
		tBlock.setTypeIdAndData(35, (byte) 0xE, false);
		plugin.pointer.put(event.getPlayer(), tBlock.getLocation());
	}
}
