package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {


    @Override
    public ResponseResult login(LoginDto loginDto) {
       if (!StringUtil.isBlank(loginDto.getPhone()) && !StringUtil.isBlank(loginDto.getPassword())){
          ApUser apUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone,loginDto.getPhone()));
          if (apUser == null){
              return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");
          }

          String salt = apUser.getSalt();
          String password = loginDto.getPassword();
          password = DigestUtils.md5DigestAsHex((password+salt).getBytes());

          if (!password.equals(apUser.getPassword())){
              return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
          }

          Map<String,Object> map = new HashMap<>();
          map.put("token", AppJwtUtil.getToken(apUser.getId().longValue()));
          apUser.setSalt("");
          apUser.setPassword("");
          map.put("user",apUser);
          return ResponseResult.okResult(map);
       }else {
           Map<String,Object> map = new HashMap<>();
           map.put("token", AppJwtUtil.getToken(0L));
           return ResponseResult.okResult(map);
       }
    }
}
