package com.heima.app.gateway.filter;

import com.heima.app.gateway.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
//为什么要实现这两个接口，为什么过滤器要加Component
public class AuthorizeFilter implements Ordered, GlobalFilter {

    //过滤方法
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取request和response对象（为什么要获取response对象）
        //通过当前参数exchange拿
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //判断是否为登录
        if (request.getURI().getPath().contains("/login")){
            return chain.filter(exchange); //这行代码代表放行，为什么？
        }

        //获取token
        String token = request.getHeaders().getFirst("token");

        //判断token是否存在
        if (StringUtils.isBlank("token")){
            response.setStatusCode(HttpStatus.UNAUTHORIZED); //返回401
            return response.setComplete(); //结束本次请求
        }

        //判断token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody("token"); //解析当前token,也可能会解析失败，失败也是需要返回401的
            //是否是过期
            int result = AppJwtUtil.verifyToken(claimsBody);
            if (result == 1 || result == 2) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED); //返回401
                return response.setComplete(); //结束本次请求
            }
        }catch (Exception e){
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED); //返回401
            return response.setComplete(); //结束本次请求
        }

        //放行
        return chain.filter(exchange);

    }

    //下面是优先级设置，值越小优先级越高
    @Override
    public int getOrder() {
        return 0;
    }
}
