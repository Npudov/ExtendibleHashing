package org.example.data;

import java.util.Objects;

public class Rec {
    private int id;

    private Object value;

    // constructor without params for serialized by kryo
    public Rec() {

    }

    public Rec(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    public Object getValue() {return  value; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rec rec = (Rec) o;
        return Objects.equals(id, rec.id)  && Objects.equals(value, rec.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }
}
