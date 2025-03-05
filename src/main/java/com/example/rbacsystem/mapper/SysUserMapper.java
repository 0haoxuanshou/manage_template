package com.example.rbacsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.rbacsystem.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}