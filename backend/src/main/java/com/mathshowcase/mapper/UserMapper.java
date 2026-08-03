package com.mathshowcase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mathshowcase.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
