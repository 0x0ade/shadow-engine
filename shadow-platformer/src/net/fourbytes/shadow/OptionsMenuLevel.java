package net.fourbytes.shadow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.lang.reflect.Constructor;

public class OptionsMenuLevel extends MenuLevel {

	public String category;

	public OptionsMenuLevel() {
		this(null, null);
	}

	public OptionsMenuLevel(String category) {
		this(null, category);
	}

	public OptionsMenuLevel(MenuLevel parent) {
		this(parent, null);
	}

	public OptionsMenuLevel(final MenuLevel parent, String category) {
		super(parent);
		showtitle = false;

		this.category = category;

		String filename = category;
		if (category == null || category.trim().isEmpty()) {
			filename = "main";
		}
		filename = (filename.trim().replaceAll("/.", "/"))+".menu";
		System.out.println(filename);
		FileHandle file = Gdx.files.internal("menus/options/"+filename);
		String[] lines = file.readString().split("\n");

		for (String line : lines) {
			String tmp = line;
			final String type = tmp.substring(0, tmp.indexOf(' ')).trim();
			tmp = tmp.substring(tmp.indexOf(' ')).trim();
			final String arg = tmp.substring(0, tmp.indexOf(' ')).trim();
			final String[] args = arg.split(";");
			final String text = tmp.substring(tmp.indexOf(' ')).trim();

			if (type.equals("cat") || type.equals("category")) {
				items.add(new MenuItem(this, text, new Runnable() {
					public void run() {
						Shadow.level = new OptionsMenuLevel(OptionsMenuLevel.this, arg);
						Shadow.cam.firsttick = true;
					}
				}));
			}
			if (type.equals("link")) {
				items.add(new MenuItem(this, text, new Runnable() {
					public void run() {
						try {
							Class clazz = getClass().getClassLoader().loadClass(arg);
							Constructor constr = clazz.getConstructor(MenuLevel.class);
							Shadow.level = (Level) constr.newInstance(OptionsMenuLevel.this);
							Shadow.cam.firsttick = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}));
			}

			//TODO bool / boolean
			if (type.equals("bool") || type.equals("boolean")) {
				boolean value = Shadow.options.getBoolean(args[0], Boolean.parseBoolean(args[1]));
				final MenuItem mi = new MenuItem(this, text+": "+(value?"Yes":"No"), null);
				Runnable run = new Runnable() {
					public void run() {
						boolean value = Shadow.options.getBoolean(args[0], Boolean.parseBoolean(args[1]));
						value = !value;
						Shadow.options.putBoolean(args[0], value);
						Shadow.options.flush();
						mi.text = text+": "+(value?"Yes":"No");
					}
				};
				mi.action = run;
				items.add(mi);
			}

			//TODO int / integer

			//TODO float

		}

		if (parent != null) {
			items.add(new MenuItem(this, "Back", new Runnable(){public void run(){
				Shadow.level = parent;
			}}));
		}

		if (getClass().equals(TitleLevel.class)) {
			Shadow.cam.bg = Background.getDefault();
		}

		ready = true;
	}

}
