package com.bitian.common.util;

import com.alibaba.fastjson2.JSON;
import com.bitian.common.dto.User;
import com.bitian.common.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtUtils {

    private static volatile JwtUtils instance=null;

    private static String USER_KEY="jwtUser_POIUQWERTY";

    private JwtUtils() {
    }

    public static JwtUtils instance(){
        if(instance==null){
            synchronized (JwtUtils.class){
                if (instance == null) {
                    instance=new JwtUtils();
                }
            }
        }
        return instance;
    }

    public <T extends User> T getUser(String token, String security,Class<T> cls) {
        final Claims claims = getClaims(token,security);
        String json= (String) claims.get(USER_KEY);
        if(StringUtils.isNotBlank(json)){
            return JSON.parseObject(json,cls);
        }else{
            return null;
        }
    }

    public <T> T getValue(String token,String security,String key,Class<T> cls) {
        final Claims claims = getClaims(token,security);
        return claims.get(key,cls);
    }

    public Date getExpirationDate(String token,String security) {
        final Claims claims = getClaims(token,security);
        if(claims==null){
            return null;
        }else{
            return claims.getExpiration();
        }
    }

    public boolean validate(String token,String security) {
        try{
            return !isTokenExpired(token,security);
        }catch (Exception e){
            return false;
        }
    }

    public Claims getClaims(String token,String security) {
        try{
            SecretKey key = Keys.hmacShaKeyFor(security.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims;
        }catch (Exception e){
            throw new CustomException("jwt解析异常->"+e.getMessage());
        }
    }

    public Boolean isTokenExpired(String token,String security) {
        final Date expiration = getExpirationDate(token,security);
        if(expiration==null){
            return true;
        }
        return expiration.before(new Date());
    }

    public String generateToken(User user,long expiration,String security){
        Map<String, Object> claims=new HashMap<>();
        claims.put(USER_KEY,JSON.toJSONString(user));
        return this.generateToken(claims,expiration,security);
    }

    public String generateToken(Map<String, Object> claims, long expiration,String security){
        return this.generateToken("user",claims,expiration,security);
    }

    /**
     * 生成token
     * @param subject 主题
     * @param claims 自定义信息
     * @param expiration 过期时间，单位小时
     * @param security 密钥
     * @return
     */
    public String generateToken(String subject, Map<String, Object> claims, long expiration,String security) {
        SecretKey key = Keys.hmacShaKeyFor(security.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000 * 60 * 60))
                .signWith(key)
                .compact();
    }

}