package net.fourbytes.shadow.systems;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntMap;
import net.fourbytes.shadow.Level;
import net.fourbytes.shadow.Musics;
import net.fourbytes.shadow.utils.backend.BackendHelper;
import net.fourbytes.shadow.utils.gdx.FloatMap;

public class DefaultMusicSystem implements IMusicSystem {

    public Level level;
    public String current = "main";
    protected ITimeDaySystem timeDaySystem;
    protected Music[] musics;

    public DefaultMusicSystem(Level level) {
        this.level = level;
        for (IntMap.Entry<Music> entry : Musics.playing) {
            if (entry.key < 1 || entry.key > 3) {
                Musics.set(entry.key, null);
            }
        }
        musics = new Music[] {
                Musics.set(1, "main_1"),
                Musics.set(2, "main_2"),
                Musics.set(-2, "main_night_2"),
                Musics.set(3, "main_3")
        };
        musics[0].setVolume(1f);
        for (int i = 1; i < musics.length; i++) {
            musics[i].setVolume(0f);
        }
        for (int i = 1; i < musics.length; i++) {
            musics[i].setPosition(musics[i-1].getPosition());
        }
        for (int i = 0; i < musics.length; i++) {
            musics[i].play();
        }
    }

    @Override
    public void tick(float delta) {
        ITimeDaySystem time = level.systems.get(ITimeDaySystem.class);
        if (time != null && musics[0] != null) {
            float vol = 0.2f * MathUtils.sin(time.getTimeNormalized() * MathUtils.PI2 - (MathUtils.PI / 2f)) + 0.8f;
            musics[0].setVolume(vol);
        }
    }

    @Override
    public String getCurrent() {
        return current;
    }

    @Override
    public void setCurrent(String current) {
        this.current = current;
    }

    @Override
    public String getName() {
        return "MusicSystem";
    }
}
