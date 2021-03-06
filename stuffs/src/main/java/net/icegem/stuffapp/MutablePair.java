package net.icegem.stuffapp;

/**
 * Created by mikael.korpela on 19.5.2015.
 */
public class MutablePair<F, S> {
    public F first;
    public S second;

    public MutablePair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals( final MutablePair<F, S> other ) {
        return first.equals(other.first) && second.equals(other.second);
    }
}
