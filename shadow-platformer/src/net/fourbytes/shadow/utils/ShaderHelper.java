package net.fourbytes.shadow.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ObjectMap;
import net.fourbytes.shadow.Shadow;

public final class ShaderHelper {
	private ShaderHelper() {
	}

	private static ObjectMap<String, ShaderProgram> shaders = new ObjectMap<String, ShaderProgram>();
	private static ObjectMap<String, Object[]> defaults = new ObjectMap<String, Object[]>();
	private static ObjectMap<String, Object[]> values = new ObjectMap<String, Object[]>();

	private static ShaderProgram current;
	private static String currentName;

	public static ShaderProgram getCurrentShader() {
		return current;
	}

	public static String getCurrentShaderName() {
		return currentName;
	}

	public static void resetCurrentShader() {
		setCurrentShader("");
	}

	public static void setCurrentShader(String name) {
		current = getShader(name);
		currentName = name;
		Shadow.spriteBatch.setShader(current);
		setAll();
	}

	public static ShaderProgram getDefaultShader() {
		return getShader("");
	}

	public static ShaderProgram getShader(String name) {
		return shaders.get(name);
	}

	public static void addShader(ShaderProgram shader) {
		addShader(shader, "");
	}

	public static void addShader(ShaderProgram shader, String name) {
		shaders.put(name, shader);
		shader.begin();
		for (ObjectMap.Entry entry : values.entries()) {
			set((String)entry.key, entry.value);
		}
		shader.end();
	}

	public static void setDefault(boolean set, String setting, Object... values) {
		if (values[0] instanceof Object[]) {
			values = (Object[]) values[0];
		}

		defaults.put(setting, values);
		if (set) {
			set(setting, (Object[]) values);
		}
	}

	public static void set(String setting, Object... values) {
		if (values[0] instanceof Object[]) {
			values = (Object[]) values[0];
		}

		Object[] rawoldvals = ShaderHelper.values.get(setting);
		if (rawoldvals != null && rawoldvals.length == values.length) {
			MultiObject oldvals = Garbage.multiobjs.getNext();
			oldvals.objects = rawoldvals;
			MultiObject newvals = Garbage.multiobjs.getNext();
			newvals.objects = values;

			boolean equals = oldvals.equals(newvals);

			oldvals.objects = null;
			newvals.objects = null;

			if (equals) {
				return;
			}
		}

		ShaderHelper.values.put(setting, values);

		//1 float or 1 int
		if (values.length == 1) {
			//1 boolean
			try {
				boolean value;
				if (values[0] instanceof Boolean) {
					value = (Boolean)values[0];
				} else {
					value = Boolean.parseBoolean((String)values[0]);
				}
				for (ShaderProgram shader : shaders.values()) {
					shader.setUniformi(setting, value?1:0);
				}
				return;
			} catch (Exception e) {
			}
			//1 int
			try {
				int value;
				if (values[0] instanceof Integer) {
					value = (Integer)values[0];
				} else {
					value = Integer.parseInt((String)values[0]);
				}
				for (ShaderProgram shader : shaders.values()) {
					shader.setUniformi(setting, value);
				}
				return;
			} catch (Exception e) {
			}
			//1 float
			try {
				float value;
				if (values[0] instanceof Float) {
					value = (Float)values[0];
				} else {
					value = Float.parseFloat((String)values[0]);
				}
				for (ShaderProgram shader : shaders.values()) {
					shader.setUniformf(setting, value);
				}
				return;
			} catch (Exception e) {
			}
			System.err.println("1: Can not set ["+values[0]+"] to "+setting+": Unknown input type");
			return;
		}

		//2 floats
		if (values.length == 2) {
			try {
				float value1;
				if (values[0] instanceof Float) {
					value1 = (Float)values[0];
				} else {
					value1 = Float.parseFloat((String)values[0]);
				}
				float value2;
				if (values[1] instanceof Float) {
					value2 = (Float)values[1];
				} else {
					value2 = Float.parseFloat((String)values[1]);
				}
				for (ShaderProgram shader : shaders.values()) {
					shader.setUniformf(setting, value1, value2);
				}
				return;
			} catch (Exception e) {
			}
			System.err.println("2: Can not set ["+values[0]+", "+values[1]+"] to "+setting+": Unknown input type");
			return;
		}

		//3 floats
		if (values.length == 3) {
			try {
				float value1;
				if (values[0] instanceof Float) {
					value1 = (Float)values[0];
				} else {
					value1 = Float.parseFloat((String)values[0]);
				}
				float value2;
				if (values[1] instanceof Float) {
					value2 = (Float)values[1];
				} else {
					value2 = Float.parseFloat((String)values[1]);
				}
				float value3;
				if (values[2] instanceof Float) {
					value3 = (Float)values[2];
				} else {
					value3 = Float.parseFloat((String)values[2]);
				}
				for (ShaderProgram shader : shaders.values()) {
					shader.setUniformf(setting, value1, value2, value3);
				}
				return;
			} catch (Exception e) {
			}
			System.err.println("3: Can not set ["+values[0]+", "+values[1]+", "+values[2]+"] to "+setting+": Unknown input type");
			return;
		}

		//4 floats
		if (values.length == 4) {
			try {
				float value1;
				if (values[0] instanceof Float) {
					value1 = (Float)values[0];
				} else {
					value1 = Float.parseFloat((String)values[0]);
				}
				float value2;
				if (values[1] instanceof Float) {
					value2 = (Float)values[1];
				} else {
					value2 = Float.parseFloat((String)values[1]);
				}
				float value3;
				if (values[2] instanceof Float) {
					value3 = (Float)values[2];
				} else {
					value3 = Float.parseFloat((String)values[2]);
				}
				float value4;
				if (values[3] instanceof Float) {
					value4 = (Float)values[3];
				} else {
					value4 = Float.parseFloat((String)values[3]);
				}
				for (ShaderProgram shader : shaders.values()) {
					shader.setUniformf(setting, value1, value2, value3, value4);
				}
				return;
			} catch (Exception e) {
			}
			System.err.println("4: Can not set ["+values[0]+", "+values[1]+", "+values[2]+", "+values[3]+"] to "+setting+": Unknown input type");
			return;
		}

		System.err.println("?: Can not set value of "+setting+": Unknown input type");
	}

	public static void setAll() {
		for (ObjectMap.Entry entry : values.entries()) {
			set((String)entry.key, entry.value);
		}
	}

	public static void reset(String setting) {
		set(setting, defaults.get(setting));
	}

	public static Object[] getDefault(String setting) {
		return defaults.get(setting);
	}

	public static Object[] get(String setting) {
		return values.get(setting);
	}

	public static String loadImports(String shader, String imports_root) {
		String finalShader = "";

		String[] lines = shader.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String input = lines[i];
			String output;

			if (input.startsWith("#import ")) {
				String importPath = input.substring(8, input.indexOf(';'));

				String subshader = Gdx.files.internal(imports_root + "/" + importPath + ".glsl").readString();
				output = loadImports(subshader, imports_root);

			} else if (input.startsWith("#copy ")) {
				String importPath = input.substring(6, input.indexOf(';'));

				String subshader = Gdx.files.internal(importPath).readString();
				output = loadImports(subshader, imports_root);

			} else {
				output = input;
			}

			finalShader += output;
			if (i < lines.length - 1) {
				finalShader += "\n";
			}
		}

		finalShader = finalShader.trim();

		return finalShader;
	}

	public static String setupShaderSource(String shader, String imports_root) {
		shader = loadImports(shader, imports_root);
		String finalShader = "";

		String[] lines = shader.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String input = lines[i];
			String output;

			if (input.startsWith("#setting ")) {
				String postPrefix = input.substring(9, input.indexOf(';'));
				String setting = postPrefix.trim();
				String type = setting.substring(0, setting.indexOf(' ')).trim();
				setting = setting.substring(setting.indexOf(' ')+1).trim();

				Object[] values = {-1};
				if (setting.contains("=")) {
					String rawValue = setting.substring(setting.indexOf('=')+1).trim();
					if (rawValue.contains(",")) {
						values = rawValue.split(",");
						for (int ii = 0; ii < values.length; ii++) {
							values[ii] = ((String)values[ii]).trim();
						}
					} else {
						values[0] = rawValue;
					}

					setting = setting.substring(0, setting.indexOf('=')).trim();
				}

				setDefault(false, setting, values);
				output = "uniform "+type+" "+setting+";";
			} else {
				output = input;
			}

			finalShader += output;
			if (i < lines.length - 1) {
				finalShader += "\n";
			}
		}

		finalShader = finalShader.trim();

		return finalShader;
	}

	public static ShaderProgram loadShader(String path) {
		String vertex = Gdx.files.internal(path+".vert.glsl").readString();
		String fragment = Gdx.files.internal(path+".frag.glsl").readString();

		vertex = ShaderHelper.setupShaderSource(vertex, "shaders/imports");
		fragment = ShaderHelper.setupShaderSource(fragment, "shaders/imports");

		ShaderProgram shader = new ShaderProgram(vertex, fragment);

		if (shader.getLog().length()!=0) {
			System.err.println("--------SHADER ERROR--------");
			System.err.print(shader.getLog());
			System.err.println("--------SOURCES--------");
			String[] lines;
			System.err.println("--------VERTEX--------");
			lines = vertex.split("\n");
			for (int i = 0; i < lines.length; i++) {
				System.err.println((i+1)+": "+lines[i]);
			}
			System.err.println("--------FRAGMENT--------");
			lines = fragment.split("\n");
			for (int i = 0; i < lines.length; i++) {
				System.err.println((i+1)+": "+lines[i]);
			}
			System.err.println("--------END--------");
		}

		return shader;
	}


}
