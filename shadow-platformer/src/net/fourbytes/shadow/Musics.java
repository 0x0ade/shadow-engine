package net.fourbytes.shadow;

import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class Musics {
	
	public static Music current;
	
	public static Music set(String name) {
		if (current != null) {
			current.stop();
			current.dispose();
		}
		current = Gdx.audio.newMusic(Gdx.files.internal("data/music/"+name+".ogg"));
		current.setVolume(0.6f);
		current.setLooping(true);
		current.play();
		return current;
	}
	
	
}
