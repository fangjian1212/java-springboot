package com.fangjian.share.session.client;

public class ObjectPrincipal implements java.security.Principal {
    private Object object = null;

    public ObjectPrincipal(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public String getName() {
        return getObject().toString();
    }

    public int hashCode() {
        return object.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof ObjectPrincipal) {
            ObjectPrincipal op = (ObjectPrincipal) o;
            return getObject().equals(op.getObject());
        }
        return false;
    }

    public String toString() {
        return object.toString();
    }
}