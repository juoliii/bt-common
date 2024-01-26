package com.bitian.common.util;

import com.bitian.common.exception.CustomException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JwtUtils {

    private static volatile JwtUtils instance=null;

    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

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

    public Object getValue(String token,String security,String key) {
        final Claims claims = getClaims(token,security);
        return claims.get(key);
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

    public Boolean validate(String token,String security) {
        return !isTokenExpired(token,security);
    }

    public Claims getClaims(String token,String security) {
        try{
            Claims claims = Jwts.parser()
                    .setSigningKey(Base64.encodeBase64String(security.getBytes(StandardCharsets.UTF_8)))
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        }catch (Exception e){
            throw new CustomException("jwt解析异常->"+e.getMessage());
        }
    }

    private Date generateExpirationDate(long expiration) {
        return new Date(System.currentTimeMillis() + expiration * 1000 * 60 * 60);
    }

    public Boolean isTokenExpired(String token,String security) {
        final Date expiration = getExpirationDate(token,security);
        if(expiration==null){
            return true;
        }
        return expiration.before(new Date());
    }

    public String generateToken(String key,Object value,long expiration,String security){
        Map<String, Object> claims=new HashMap<>();
        claims.put(key,value);
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
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate(expiration))
                .signWith(SIGNATURE_ALGORITHM, Base64.encodeBase64String(security.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

}