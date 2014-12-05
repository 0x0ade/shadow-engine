package net.fourbytes.shadow.network.menus;

import com.badlogic.gdx.utils.JsonValue;
import net.fourbytes.shadow.MenuLevel;
import net.fourbytes.shadow.Shadow;
import net.fourbytes.shadow.network.ClientLevel;
import net.fourbytes.shadow.utils.Garbage;
import net.fourbytes.shadow.utils.Options;

public class JoinMenuLevel extends MenuLevel {

    public String name;
    public String ip;

    public JoinMenuLevel(String name, String ip) {
        this(null, name, ip);
    }

    public JoinMenuLevel(MenuLevel parent, String name, String ip) {
        super(parent);
        this.name = name;
        this.ip = ip;

        items.add(new MenuItem(this, "Join Server", new Runnable(){public void run(){
            Shadow.level = new ClientLevel(JoinMenuLevel.this.ip);
            Shadow.cam.firsttick = true;
        }}));

        items.add(new MenuItem(this, "Remove Server", new Runnable(){public void run(){
            String jsonString = Options.getString("mp.servers", "[]");

            if (jsonString != null) {
                JsonValue json = Garbage.jsonReader.parse(jsonString);

                for (JsonValue server = json.child; server != null; server = server.next) {
                    String name = server.getString("name");
                    String ip = server.getString("ip");
                    if (name.equals(JoinMenuLevel.this.name) && ip.equals(JoinMenuLevel.this.ip)) {
                        if (server.prev != null) {
                            server.prev.next = server.next;
                        }
                        if (server.next != null) {
                            server.next.prev = server.prev;
                        }
                        if (json.child == server) {
                            json.child = server.next;
                        }

                        Options.putString("mp.servers", json.toString());
                        Options.flush();
                        break;
                    }
                }
            }

            Shadow.level = JoinMenuLevel.this.parent;
            Shadow.cam.firsttick = true;
        }}));

        items.add(new MenuItem(this, "Back", new Runnable(){public void run(){
            Shadow.level = JoinMenuLevel.this.parent;
            Shadow.cam.firsttick = true;
        }}));

        ready = true;

    }
}