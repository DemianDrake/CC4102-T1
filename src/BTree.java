package src;

import java.util.List;
import java.util.ArrayList;

public class BTree {

    private int B;
    private BNode root;
    private int height;
    private int N;

    private static final class BNode {

        private int size;
        private Entry[] data;

        private BNode(int B) {
            this.size = B;
            this.data = new Entry[B];
        }

        private Entry[] get_data() {
            return this.data;
        }

        private int get_size() {
            return this.size;
        }

        private void set_size(int new_size) {
            this.size = new_size;
        }

        private void increment_size() {
            this.size += 1;
        }

    }

    public static class RangeList {

        private long puntero;
        private RangeList next;

        public RangeList(long puntero) {
            this.puntero = puntero;
        }

        public long get_puntero() {
            return this.puntero;
        }

        public RangeList get_next() {
            return this.next;
        }

        public void set_puntero(long new_puntero) {
            this.puntero = new_puntero;
        }

        public void set_next(RangeList new_next) {
            this.next = new_next;
        }

        public void append_puntero(long puntero) {
            RangeList aux = new RangeList(puntero);
            this.append_RangeList(aux);
        }

        public void append_RangeList(RangeList to_append) {
            if (this.get_next() == null) {
                this.set_next(to_append);
            } else {
                this.get_next().append_RangeList(to_append);
            }
        }

        public long[] to_longarray() {
            int length = 1;
            RangeList aux = this;
            while (aux.get_next() != null) {
                length += 1;
                aux = aux.get_next();
            }
            long[] resp = new long[length];
            int index = 0;
            aux = this;
            resp[index] = this.get_puntero();
            while (aux.get_next() != null) {
                index += 1;
                aux = aux.get_next();
                resp[index] = this.get_puntero();
            }
            return resp;
        }

        public List<Long> toJavaList() {
            List<Long> ret = new ArrayList<Long>();
            ret.add(puntero);
            RangeList tmp = this;
            while (tmp.get_next() != null) {
                tmp = tmp.next;
                ret.add(tmp.get_puntero());
            }
            return ret;
        }
    }

    private static class Entry {

        private String key;
        private final long val;
        private BNode next;     // helper field to iterate over array entries

        public Entry(String key, long val, BNode next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }

        public String get_key() {
            return this.key;
        }

        public long get_val() {
            return this.val;
        }

        public BNode get_next() {
            return this.next;
        }

        public void set_key(String new_key) {
            this.key = new_key;
        }

        public void set_next(BNode new_next) {
            this.next = new_next;
        }

    }

    public BTree(int B) {
        this.B = B;
        this.root = new BNode(0);
    }

    public BNode get_root() {
        return this.root;
    }

    public int get_B() {
        return this.B;
    }

    public int get_N() {
        return this.N;
    }

    public int get_height() {
        return this.height;
    }

    public void set_root(BNode new_root) {
        this.root = new_root;
    }

    public void increment_N() {
        this.N += 1;
    }

    public void increment_height() {
        this.height += 1;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return get_N();
    }

    public int height() {
        return this.height;
    }

    public long search_by_key(String key) {
        return search(this.root, key, this.height);
    }

    public long search(BNode node, String id, int height) {
        Entry[] data = node.get_data();

        //hoja
        if (height == 0) {
            for (int i = 0; i < node.get_size(); i++) {
                if (data[i].get_key().compareTo(id) == 0) {
                    return data[i].get_val();
                }
            }
        } else {
            for (int i = 0; i < node.get_size(); i++) {
                if (i + 1 == node.get_size() || id.compareTo(data[i + 1].get_key()) < 0) {
                    return search(data[i].get_next(), id, height - 1);
                }
            }
        }
        return -1;
    }

    public List<Long> search_by_key_range(String key_min, String key_max) {
        return search_range(this.root, key_min, key_max, this.height).toJavaList();
    }

    public RangeList search_range(BNode node, String key_min, String key_max, int height) {
        RangeList resp = null;
        Entry[] data = node.get_data();
        //hoja
        if (height == 0) {
            for (int i = 0; i < node.get_size(); i++) {
                if (data[i].get_key().compareTo(key_min) >= 0 && data[i].get_key().compareTo(key_max) <= 0) {
                    if (resp == null) {
                        resp = new RangeList(data[i].get_val());
                    } else {
                        resp.append_puntero(data[i].get_val());
                    }
                }
            }
        } else {
            for (int i = 0; i < node.get_size(); i++) {
                if (i + 1 == node.get_size() || key_max.compareTo(data[i + 1].get_key()) > 0 || key_min.compareTo(data[i + 1].get_key()) < 0) {
                    //return search(data[i].get_next(),id,height-1);
                    if (resp == null) {
                        resp = search_range(data[i].get_next(), key_min, key_max, height - 1);
                    } else {
                        resp.append_RangeList(search_range(data[i].get_next(), key_min, key_max, height - 1));
                    }
                }
            }
        }
        return resp;
    }

    public void put(String key, long puntero) {
        BNode aux = insert(this.get_root(), key, puntero, this.get_height());
        increment_N();
        if (aux == null) {
            return;
        }
        BNode aux2 = new BNode(this.get_B());
        aux2.get_data()[0] = new Entry(this.get_root().get_data()[0].get_key(), -1, this.get_root());
        aux2.get_data()[1] = new Entry(aux.get_data()[0].get_key(), -1, aux);
        this.set_root(aux2);
        this.increment_height();
    }

    private BNode insert(BNode h, String key, long puntero, int ht) {
        int j;
        Entry t = new Entry(key, puntero, null);

        if (ht == 0) {
            for (j = 0; j < h.get_size(); j++) {
                if (key.compareTo(h.get_data()[j].get_key()) < 0) {
                    break;
                }
            }
        } else {
            for (j = 0; j < h.get_size(); j++) {
                if (j + 1 == h.get_size() || key.compareTo(h.get_data()[j + 1].get_key()) < 0) {
                    BNode u = insert(h.get_data()[j++].get_next(), key, puntero, ht - 1);
                    if (u == null) {
                        return null;
                    }
                    t.set_key(u.get_data()[0].get_key());
                    t.set_next(u);
                    break;
                }
            }
        }
        for (int i = h.get_size(); i > j; i--) {
            h.get_data()[i] = h.get_data()[i - 1];
        }
        h.get_data()[j] = t;
        h.increment_size();
        if (h.get_size() < this.get_B()) {
            return null;
        } else {
            return split(h);
        }
    }

    private BNode split(BNode h) {
        BNode t = new BNode(this.get_B() / 2);
        h.set_size(this.get_B() / 2);
        for (int i = 0; i < this.get_B() / 2; i++) {
            t.get_data()[i] = h.get_data()[this.get_B() / 2 + i];
        }
        return t;
    }

}
