package org.wannes;

import org.wannes.plugModCore.api.Module;
import org.wannes.plugModCore.api.ModuleContext;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

/** Minimal separate module: command and listener cleanup is owned by the context. */
public class TemplateModule implements Module, Listener {
    private ModuleContext context;
    public void onEnable(ModuleContext context) {
        this.context = context;
        context.registerListener(this);
        context.registerCommand("modulehello", (sender, command, label, args) -> {
            sender.sendMessage("Hello from " + context.getModuleName());
            return true;
        }, "Example module command", "/modulehello");
        context.getLogger().info("Example enabled");
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        context.getLogger().fine(event.getPlayer().getName() + " joined");
    }
    public void onDisable() { context = null; }
}
