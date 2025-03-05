package com.example.rbacsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rbacsystem.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    /**
     * 分页查询用户列表
     */
    Page<SysUser> getUserList(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 创建新用户
     */
    void createUser(SysUser user);

    /**
     * 更新用户信息
     */
    void updateUser(SysUser user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 修改用户状态
     */
    void updateUserStatus(Long id, Integer status);

    /**
     * 重置用户密码
     */
    void resetUserPassword(Long id, String newPassword);
}