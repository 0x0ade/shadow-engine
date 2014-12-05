package net.fourbytes.shadow.network;

import net.fourbytes.shadow.map.MapObject;

public class DataMapUpdate extends Data {

    public final static class MapUpdateModes {
        private MapUpdateModes() {
        }

        public final static int ADD = 0;
        public final static int REMOVE = 1;
        public final static int UPDATE = 2;
    }

    public int mode = 0;
    public MapObject object;

    public DataMapUpdate() {
    }

}
