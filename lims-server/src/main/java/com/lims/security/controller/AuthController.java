package com.lims.security.controller;

import com.lims.common.result.Result;
import com.lims.security.dto.LoginDTO;
import com.lims.security.dto.LoginVO;
import com.lims.security.dto.UserInfo;
import com.lims.security.entity.LoginUser;
import com.lims.security.utils.JwtUtil;
import com.lims.system.entity.SysUser;
import com.lims.system.mapper.SysUserMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${jwt.expiration}")
    private Long expiration;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private SysUserMapper sysUserMapper;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        SysUser user = loginUser.getUser();

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        redisTemplate.opsForValue().set("login:" + user.getUsername(), loginUser, expiration, TimeUnit.MILLISECONDS);

        List<String> roles = sysUserMapper.selectRoleCodesByUserId(user.getId());
        List<String> permissions = loginUser.getPermissions();

        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getPhone(),
                user.getEmail(),
                user.getAvatar(),
                user.getDeptId(),
                user.getStatus(),
                roles,
                permissions
        );

        LoginVO vo = new LoginVO(token, userInfo);

        return Result.success(vo);
    }

    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Result<Void> logout() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = loginUser.getUser().getUsername();
        redisTemplate.delete("login:" + username);
        SecurityContextHolder.clearContext();
        return Result.success();
    }

    @GetMapping("/userInfo")
    @ApiOperation("获取当前用户信息")
    public Result<LoginVO> getUserInfo() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUser user = loginUser.getUser();
        List<String> roles = sysUserMapper.selectRoleCodesByUserId(user.getId());

        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getPhone(),
                user.getEmail(),
                user.getAvatar(),
                user.getDeptId(),
                user.getStatus(),
                roles,
                loginUser.getPermissions()
        );

        LoginVO vo = new LoginVO(null, userInfo);

        return Result.success(vo);
    }

    @GetMapping("/captcha")
    @ApiOperation("获取验证码")
    public Result<Map<String, Object>> getCaptcha() {
        Map<String, Object> result = new HashMap<>();
        result.put("uuid", "captcha-uuid");
        result.put("img", "data:image/png;base64,");
        return Result.success(result);
    }
}
