package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.IntMap;

public class Musics {
	
	public static IntMap<Music> playing = new IntMap<Music>();
    public static IntMap<String> playingNames = new IntMap<String>();
	
	public static Music set(int slot, String name) {
        Music current = playing.get(slot);
		if (current != null) {
            current.stop();
            current.dispose();
            current = null;
		}
        if (name != null) {
            current = Gdx.audio.newMusic(Gdx.files.internal("data/music/" + name + ".ogg"));
            current.setVolume(0.6f);
            current.setLooping(true);
        }
        playing.put(slot, current);
        playingNames.put(slot, name);
		return current;
	}
	
	
}
