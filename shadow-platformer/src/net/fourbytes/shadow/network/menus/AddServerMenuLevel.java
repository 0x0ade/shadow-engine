package net.fourbytes.shadow.network.menus;

import com.badlogic.gdx.utils.JsonValue;
import net.fourbytes.shadow.*;
import net.fourbytes.shadow.utils.Garbage;
import net.fourbytes.shadow.utils.Options;

public class AddServerMenuLevel extends MenuLevel {

    public boolean dirty = false;
    public String[] name = {""};
    public String[] ip = {""};

    public AddServerMenuLevel() {
        this(null);
    }

    public AddServerMenuLevel(MenuLevel parent) {
        super(parent);

        refresh();

        ready = true;
    }

    protected void refresh() {
        dirty = false;

        int indexOld = items.indexOf(current, true);
        items.clear();

        items.add(new MenuItem(this, "Name: "+name[0], new Runnable(){public void run(){
            Shadow.level = new TextInputLevel(AddServerMenuLevel.this, name);
            Shadow.cam.firsttick = true;
            dirty = true;
        }}));

        items.add(new MenuItem(this, "IP: "+ip[0], new Runnable(){public void run(){
            Shadow.level = new TextInputLevel(AddServerMenuLevel.this, ip);
            Shadow.cam.firsttick = true;
            dirty = true;
        }}));

        items.add(new MenuItem(this, "Add", new Runnable(){public void run(){
            String jsonString = Options.getString("mp.servers", "[]");

            if (jsonString != null) {
                JsonValue json = Garbage.jsonReader.parse(jsonString);

                boolean add = true;
                for (JsonValue server = json.child; server != null; server = server.next) {
                    String name = server.getString("name");
                    String ip = server.getString("ip");
                    if (name.equals(AddServerMenuLevel.this.name[0]) || ip.equals(AddServerMenuLevel.this.ip[0])) {
                        add = false;
                        break;
                    }
                }

                if (add) {
                    JsonValue server = new JsonValue(JsonValue.ValueType.object);

                    server.child = new JsonValue(name[0]);
                    server.child.name = "name";

                    server.child.next = new JsonValue(ip[0]);
                    server.child.next.prev = server.child;
                    server.child.next.name = "ip";

                    if (json.child != null) {
                        server.next = json.child;
                        json.child.prev = server;
                    }
                    json.child = server;

                    Options.putString("mp.servers", json.toString());
                    Options.flush();
                }
            }


            Shadow.level = AddServerMenuLevel.this.parent;
            Shadow.cam.firsttick = true;
        }}));

        items.add(new MenuItem(this, "Back", new Runnable(){public void run(){
            Shadow.level = AddServerMenuLevel.this.parent;
            Shadow.cam.firsttick = true;
        }}));

        if (indexOld < 0) {
            indexOld = 0;
        }
        if (indexOld >= items.size) {
            indexOld = items.size-1;
        }
        current = items.get(indexOld);
    }

    @Override
    public void tick(float delta) {
        if (dirty) {
            refresh();
        }
        super.tick(delta);
    }
}
