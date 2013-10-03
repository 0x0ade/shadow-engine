package net.fourbytes.shadow.utils;

public class MultiObject {

	public Object[] objects;

	public MultiObject(Object... objects) {
		this.objects = objects;
	}

	public MultiObject(MultiObject mo) {
		set(mo);
	}

	public void set(MultiObject mo) {
		this.objects = mo.objects;
	}

	public void set(Object... objects) {
		this.objects = objects;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MultiObject) {
			MultiObject mo = (MultiObject) obj;
			if (objects.length != mo.objects.length) {
				return false;
			}

			for (int i = 0; i < objects.length; i++) {
				if (!mo.objects[i].equals(objects[i])) {
					return false;
				}
			}

			return true;
		}

		return false;
	}


}
