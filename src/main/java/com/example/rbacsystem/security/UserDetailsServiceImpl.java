package com.example.rbacsystem.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.rbacsystem.entity.SysPermission;
import com.example.rbacsystem.entity.SysUser;
import com.example.rbacsystem.mapper.SysUserMapper;
import com.example.rbacsystem.mapper.SysUserPermissionMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;
    private final SysUserPermissionMapper userPermissionMapper;

    public UserDetailsServiceImpl(SysUserMapper userMapper, SysUserPermissionMapper userPermissionMapper) {
        this.userMapper = userMapper;
        this.userPermissionMapper = userPermissionMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
        );

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已被禁用");
        }

        // 获取用户的权限列表
        List<SysPermission> permissions = userPermissionMapper.selectPermissionsByUserId(user.getId());
        List<SimpleGrantedAuthority> authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermissionCode()))
                .collect(Collectors.toList());
        Set<String> permissionCodes = permissions.stream()
                .map(SysPermission::getPermissionCode)
                .collect(Collectors.toSet());

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities,
                permissionCodes
        );
    }
}