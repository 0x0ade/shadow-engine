package net.fourbytes.shadow.gdxutils;


import java.util.Arrays;

import com.badlogic.gdx.math.MathUtils;

/** A resizable, ordered or unordered byte array. Avoids the boxing that occurs with ArrayList<byte>. If unordered, this class
 * avoids a memory copy when removing elements (the last element is moved to the removed element's position).
 * @author Maik Macho (Ported byteArray to ByteArray)
 * @author Nathan Sweet */
public class ByteArray {
	public byte[] items;
	public int size;
	public boolean ordered;

	/** Creates an ordered array with a capacity of 16. */
	public ByteArray () {
		this(true, 16);
	}

	/** Creates an ordered array with the specified capacity. */
	public ByteArray (int capacity) {
		this(true, capacity);
	}

	/** @param ordered If false, methods that remove elements may change the order of other elements in the array, which avoids a
	 *           memory copy.
	 * @param capacity Any elements added beyond this will cause the backing array to be grown. */
	public ByteArray (boolean ordered, int capacity) {
		this.ordered = ordered;
		items = new byte[capacity];
	}

	/** Creates a new array containing the elements in the specific array. The new array will be ordered if the specific array is
	 * ordered. The capacity is set to the number of elements, so any subsequent elements added will cause the backing array to be
	 * grown. */
	public ByteArray (ByteArray array) {
		this.ordered = array.ordered;
		size = array.size;
		items = new byte[size];
		System.arraycopy(array.items, 0, items, 0, size);
	}

	/** Creates a new ordered array containing the elements in the specified array. The capacity is set to the number of elements,
	 * so any subsequent elements added will cause the backing array to be grown. */
	public ByteArray (byte[] array) {
		this(true, array, 0, array.length);
	}

	/** Creates a new array containing the elements in the specified array. The capacity is set to the number of elements, so any
	 * subsequent elements added will cause the backing array to be grown.
	 * @param ordered If false, methods that remove elements may change the order of other elements in the array, which avoids a
	 *           memory copy. */
	public ByteArray (boolean ordered, byte[] array, int startIndex, int count) {
		this(ordered, array.length);
		size = count;
		System.arraycopy(array, startIndex, items, 0, count);
	}

	public void add (byte value) {
		byte[] items = this.items;
		if (size == items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
		items[size++] = value;
	}

	public void addAll (ByteArray array) {
		addAll(array, 0, array.size);
	}

	public void addAll (ByteArray array, int offset, int length) {
		if (offset + length > array.size)
			throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
		addAll(array.items, offset, length);
	}

	public void addAll (byte[] array) {
		addAll(array, 0, array.length);
	}

	public void addAll (byte[] array, int offset, int length) {
		byte[] items = this.items;
		int sizeNeeded = size + length;
		if (sizeNeeded >= items.length) items = resize(Math.max(8, (int)(sizeNeeded * 1.75f)));
		System.arraycopy(array, offset, items, size, length);
		size += length;
	}

	public byte get (int index) {
		if (index >= size) throw new IndexOutOfBoundsException(String.valueOf(index));
		return items[index];
	}

	public void set (int index, byte value) {
		if (index >= size) throw new IndexOutOfBoundsException(String.valueOf(index));
		items[index] = value;
	}

	public void insert (int index, byte value) {
		if (index > size) throw new IndexOutOfBoundsException(String.valueOf(index));
		byte[] items = this.items;
		if (size == items.length) items = resize(Math.max(8, (int)(size * 1.75f)));
		if (ordered)
			System.arraycopy(items, index, items, index + 1, size - index);
		else
			items[size] = items[index];
		size++;
		items[index] = value;
	}

	public void swap (int first, int second) {
		if (first >= size) throw new IndexOutOfBoundsException(String.valueOf(first));
		if (second >= size) throw new IndexOutOfBoundsException(String.valueOf(second));
		byte[] items = this.items;
		byte firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	public boolean contains (byte value) {
		int i = size - 1;
		byte[] items = this.items;
		while (i >= 0)
			if (items[i--] == value) return true;
		return false;
	}

	public int indexOf (byte value) {
		byte[] items = this.items;
		for (int i = 0, n = size; i < n; i++)
			if (items[i] == value) return i;
		return -1;
	}

	public int lastIndexOf (char value) {
		byte[] items = this.items;
		for (int i = size - 1; i >= 0; i--)
			if (items[i] == value) return i;
		return -1;
	}

	public boolean removeValue (byte value) {
		byte[] items = this.items;
		for (int i = 0, n = size; i < n; i++) {
			if (items[i] == value) {
				removeIndex(i);
				return true;
			}
		}
		return false;
	}

	/** Removes and returns the item at the specified index. */
	public byte removeIndex (int index) {
		if (index >= size) throw new IndexOutOfBoundsException(String.valueOf(index));
		byte[] items = this.items;
		byte value = items[index];
		size--;
		if (ordered)
			System.arraycopy(items, index + 1, items, index, size - index);
		else
			items[index] = items[size];
		return value;
	}

	/** Removes from this array all of elements contained in the specified array.
	 * @return true if this array was modified. */
	public boolean removeAll (ByteArray array) {
		int size = this.size;
		int startSize = size;
		byte[] items = this.items;
		for (int i = 0, n = array.size; i < n; i++) {
			byte item = array.get(i);
			for (int ii = 0; ii < size; ii++) {
				if (item == items[ii]) {
					removeIndex(ii);
					size--;
					break;
				}
			}
		}
		return size != startSize;
	}

	/** Removes and returns the last item. */
	public byte pop () {
		return items[--size];
	}

	/** Returns the last item. */
	public byte peek () {
		return items[size - 1];
	}

	/** Returns the first item. */
	public byte first () {
		if (size == 0) throw new IllegalStateException("Array is empty.");
		return items[0];
	}

	public void clear () {
		size = 0;
	}

	/** Reduces the size of the backing array to the size of the actual items. This is useful to release memory when many items have
	 * been removed, or if it is known that more items will not be added. */
	public void shrink () {
		resize(size);
	}

	/** Increases the size of the backing array to acommodate the specified number of additional items. Useful before adding many
	 * items to avoid multiple backing array resizes.
	 * @return {@link #items} */
	public byte[] ensureCapacity (int additionalCapacity) {
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded >= items.length) resize(Math.max(8, sizeNeeded));
		return items;
	}

	protected byte[] resize (int newSize) {
		byte[] newItems = new byte[newSize];
		byte[] items = this.items;
		System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
		this.items = newItems;
		return newItems;
	}

	public void sort () {
		Arrays.sort(items, 0, size);
	}

	public void reverse () {
		for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
			int ii = lastIndex - i;
			byte temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	public void shuffle () {
		for (int i = size - 1; i >= 0; i--) {
			int ii = MathUtils.random(i);
			byte temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}
	}

	/** Reduces the size of the array to the specified size. If the array is already smaller than the specified size, no action is
	 * taken. */
	public void truncate (int newSize) {
		if (size > newSize) size = newSize;
	}

	/** Returns a random item from the array, or zero if the array is empty. */
	public byte random () {
		if (size == 0) return 0;
		return items[MathUtils.random(0, size - 1)];
	}

	public byte[] toArray () {
		byte[] array = new byte[size];
		System.arraycopy(items, 0, array, 0, size);
		return array;
	}

	public boolean equals (Object object) {
		if (object == this) return true;
		if (!(object instanceof ByteArray)) return false;
		ByteArray array = (ByteArray)object;
		int n = size;
		if (n != array.size) return false;
		for (int i = 0; i < n; i++)
			if (items[i] != array.items[i]) return false;
		return true;
	}

	public String toString () {
		if (size == 0) return "[]";
		byte[] items = this.items;
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('[');
		buffer.append(items[0]);
		for (int i = 1; i < size; i++) {
			buffer.append(", ");
			buffer.append(items[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}

	public String toString (String separator) {
		if (size == 0) return "";
		byte[] items = this.items;
		StringBuilder buffer = new StringBuilder(32);
		buffer.append(items[0]);
		for (int i = 1; i < size; i++) {
			buffer.append(separator);
			buffer.append(items[i]);
		}
		return buffer.toString();
	}
}

