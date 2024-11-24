package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import org.springframework.web.bind.annotation.RequestBody;

public interface ApUserService extends IService<ApUser> {

    public ResponseResult login(@RequestBody LoginDto loginDto);
}
