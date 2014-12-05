package net.fourbytes.shadow;

import com.badlogic.gdx.utils.JsonValue;
import net.fourbytes.shadow.network.menus.AddServerMenuLevel;
import net.fourbytes.shadow.network.menus.JoinMenuLevel;
import net.fourbytes.shadow.network.ServerLevel;
import net.fourbytes.shadow.utils.Garbage;
import net.fourbytes.shadow.utils.Options;

public class MultiplayerMenuLevel extends MenuLevel {

    public boolean dirty = false;

    public MultiplayerMenuLevel() {
        this(null);
    }

    public MultiplayerMenuLevel(MenuLevel parent) {
        super(parent);

        refresh();

        ready = true;
    }

    protected void refresh() {
        dirty = false;

        int indexOld = items.indexOf(current, true);
        items.clear();

        String usernameOld = Shadow.playerInfo.getUserName();
        Shadow.playerInfo.setUserName(new String(Shadow.playerInfo.getUserName()));
        if (usernameOld != Shadow.playerInfo.getUserName()) {
            items.add(new MenuItem(this, "Username: " + Shadow.playerInfo.getUserName(), new Runnable() {
                public void run() {
                    Shadow.level = new TextInputLevel(MultiplayerMenuLevel.this, "mp.user.name");
                    Shadow.cam.firsttick = true;
                    dirty = true;
                }
            }));
        }

        items.add(new MenuItem(this, "Start Server", new Runnable(){public void run(){
            Shadow.level = new ServerLevel();
            Shadow.cam.firsttick = true;
        }}));

        String jsonString = Options.getString("mp.servers", null);

        if (jsonString != null) {
            JsonValue json = Garbage.jsonReader.parse(jsonString);

            for (JsonValue server = json.child; server != null; server = server.next) {
                final String name = server.getString("name");
                final String ip = server.getString("ip");
                items.add(new MenuItem(this, name, new Runnable() {
                    public void run() {
                        Shadow.level = new JoinMenuLevel(MultiplayerMenuLevel.this, name, ip);
                        Shadow.cam.firsttick = true;
                        dirty = true;
                    }
                }));
            }
        }

        items.add(new MenuItem(this, "Add Server", new Runnable(){public void run(){
            Shadow.level = new AddServerMenuLevel(MultiplayerMenuLevel.this);
            Shadow.cam.firsttick = true;
            dirty = true;
        }}));

        items.add(new MenuItem(this, "Back", new Runnable(){public void run(){
            Shadow.level = MultiplayerMenuLevel.this.parent;
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
