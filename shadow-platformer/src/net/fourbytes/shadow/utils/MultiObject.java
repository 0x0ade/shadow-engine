package net.fourbytes.shadow.utils;

public class MultiObject {

	public Object[] objects;

	public MultiObject() {
	}

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

			if (objects == null || mo.objects == null) {
				return objects == mo.objects;
			}

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

	@Override
	public int hashCode() {
		int hash = Integer.MIN_VALUE;
		for (Object o : objects) {
			hash += o.hashCode();
			hash *= 0.75f;
		}
		hash *= objects.length;
		return hash;
	}

}
