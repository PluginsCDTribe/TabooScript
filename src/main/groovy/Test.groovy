import com.ilummc.tlib.scripting.api.GroovyPluginApi
import com.ilummc.tlib.scripting.api.TabooScriptingApi
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.player.AsyncPlayerChatEvent

def plugin = new GroovyPluginApi(null)
def api = new TabooScriptingApi()
def bukkit = Bukkit.getServer()

plugin.onEnable {
    listen { AsyncPlayerChatEvent event ->
        if (event.message == 'test')
            event.player.inventory.addItem api.item {
                type 'POTION'
                enchant LUCK: 1, DAMAGE: 2
                name '鸡毛'
                lore '狗毛', '皮蛇'
                unbreakable true
                potionEffect {
                    type 'HARM'
                    level 1
                    duration 100
                    color 'RED'
                    particle false
                }
            }
        bukkit.getPlayer 'xxx'
    }
    listen('AsyncPlayerChatEvent') {
        if (it.message == 'test2')
            it.player.sendMessage 'No type register'
    }
}

plugin.description {
    name 'Test'
    version '1.0'
    depend 'TabooLib', 'Vault'
}

println Event.asSubclass(AsyncPlayerChatEvent)
