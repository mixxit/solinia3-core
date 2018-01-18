package com.solinia.solinia.Interfaces;

import java.util.List;
import java.util.function.Predicate;

public interface IRepository<T> {
    void add(T item);

    void add(Iterable<T> items);

    void update(T item);

    void remove(T item);

    T getByKey(Object key);
    
    void remove(Predicate<T> filter);

    List<T> query(Predicate<T> filter);
    
    void commit();
    
    void reload();
}
