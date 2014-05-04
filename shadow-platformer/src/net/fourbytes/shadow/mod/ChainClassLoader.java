package net.fourbytes.shadow.mod;

/**
 * A ChainClassLoader wraps itself around the class loader
 * needed by the currently used backend to modify the
 * behaviour of another class loader to allow dependency
 * checks and loading dependencies go "down" a class loader
 * chain, which means checking the children loader after the
 * parent loader as long as a children loader is given.
 */
public class ChainClassLoader extends ClassLoader {

	public ClassLoader chainParent;
	public ClassLoader chainChild;

	public String[] blacklist;

	public ChainClassLoader(ClassLoader parent) {
		this(parent, null, null);
	}

	public ChainClassLoader(ClassLoader parent, ClassLoader chainParent) {
		this(parent, chainParent, null);
	}

	public ChainClassLoader(ClassLoader parent, ClassLoader chainParent, ClassLoader chainChild) {
		super(parent);
		this.chainParent = chainParent;
		this.chainChild = chainChild;
	}

	protected synchronized Class<?> findClass(String name) throws ClassNotFoundException {
		ClassLoader parent = getParent();

		//Check if class blacklisted
		if (blacklist != null && blacklist.length > 0) {
			for (String aBlacklist : blacklist) {
				if (name.equals(aBlacklist)) {
					//Class is blacklisted, load it from wrapped CL
					try {
						return parent.loadClass(name);
					} catch (ClassNotFoundException e) {
						//c stays null
					}
					break;
				}
			}
		}

		//Load class from chain parent when c == null
		if (chainParent != null) {
			try {
				return chainParent.loadClass(name);
			} catch (ClassNotFoundException e) {
				//c stays null
			}
		}

		//Load class from chain child when c == null
		if (chainChild != null) {
			try {
				return chainChild.loadClass(name);
			} catch (ClassNotFoundException e) {
				//c stays null
			}
		}

		//Finally, check whether the wrapped CL can find the class.
		//This may throw an exception.
		return parent.loadClass(name);
	}

}
