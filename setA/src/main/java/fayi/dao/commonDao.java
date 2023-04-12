package fayi.dao;


import java.util.ArrayList;

public interface commonDao<T> {
    int insert (T t);
    int insertBatch(ArrayList<T> Ts);
}
