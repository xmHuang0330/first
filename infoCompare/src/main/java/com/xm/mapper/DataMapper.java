package com.xm.mapper;

import com.xm.entity.Data;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DataMapper {
    List<Data> findAll();
}
