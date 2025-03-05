package com.example.rbacsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.rbacsystem.common.Result;
import com.example.rbacsystem.model.param.UserCreateParam;
import com.example.rbacsystem.model.param.UserPasswordParam;
import com.example.rbacsystem.model.param.UserUpdateParam;
import com.example.rbacsystem.entity.SysUser;
import com.example.rbacsystem.service.SysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class SysUserController {

    private final SysUserService userService;

    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result<Page<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.getUserList(pageNum, pageSize, keyword));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('sys:user:create')")
    public Result<Void> create(@RequestBody UserCreateParam param) {
        SysUser user = new SysUser();
        user.setUsername(param.getUsername());
        user.setPassword(param.getPassword());
        user.setNickname(param.getNickname());
        user.setEmail(param.getEmail());
        user.setPhone(param.getPhone());
        user.setStatus(param.getStatus());
        userService.createUser(user);
        return Result.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserUpdateParam param) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setNickname(param.getNickname());
        user.setEmail(param.getEmail());
        user.setPhone(param.getPhone());
        user.setStatus(param.getStatus());
        userService.updateUser(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody UserUpdateParam param) {
        userService.updateUserStatus(id, param.getStatus());
        return Result.success();
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody UserPasswordParam param) {
        userService.resetUserPassword(id, param.getNewPassword());
        return Result.success();
    }
}