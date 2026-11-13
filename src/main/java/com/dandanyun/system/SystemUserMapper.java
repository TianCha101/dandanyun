package com.dandanyun.system;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SystemUserMapper {

    SystemUser findByUsername(@Param("username") String username);
}
