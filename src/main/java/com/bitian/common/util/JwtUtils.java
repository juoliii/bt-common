package com.bitian.common.util;

import com.bitian.common.dto.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * jwt工具类
 */
public class JwtUtils {

    private static final String CLAIM_KEY_USER_ID = "user_id";

    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    private static JwtUtils instance;

    public static JwtUtils getInstance(){
        if (instance == null) {
            synchronized (JwtUtils.class) {
                if (instance == null) {
                    instance = new JwtUtils();
                }
            }
        }
        return instance;
    }

    public JwtUser getUserFromToken(String token,String secret) {
        JwtUser userDetail=new JwtUser();
        try {
            final Claims claims = getClaimsFromToken(token,secret);
            long userId = getUserIdFromToken(token,secret);
            String username = claims.getSubject();
            userDetail.setUsername(username);
            userDetail.setId(userId);
        } catch (Exception e) {
            userDetail = null;
        }
        return userDetail;
    }

    public long getUserIdFromToken(String token,String secret) {
        long userId;
        try {
            final Claims claims = getClaimsFromToken(token,secret);
            userId = Long.parseLong(String.valueOf(claims.get(CLAIM_KEY_USER_ID)));
        } catch (Exception e) {
            userId = 0;
        }
        return userId;
    }

    public String getUsernameFromToken(String token,String secret) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token,secret);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getCreatedDateFromToken(String token,String secret) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token,secret);
            created = claims.getIssuedAt();
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public String generateAccessToken(JwtUser userDetail,long expiration,String secret) {
        Map<String, Object> claims = generateClaims(userDetail);
        return generateAccessToken(userDetail.getUsername(), claims,expiration,secret);
    }

    public Date getExpirationDateFromToken(String token,String secret) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token,secret);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset,String secret) {
        final Date created = getCreatedDateFromToken(token,secret);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token,secret));
    }

    public String refreshToken(String token,long expiration,String secret) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token,secret);
            refreshedToken = generateAccessToken(claims.getSubject(), claims,expiration,secret);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }


    public Boolean validateToken(String token,String secret) {
        return !isTokenExpired(token,secret);
    }


    private Claims getClaimsFromToken(String token,String secret) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Date generateExpirationDate(long expiration) {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    private Boolean isTokenExpired(String token,String secret) {
        final Date expiration = getExpirationDateFromToken(token,secret);
        return expiration.before(new Date());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private Map<String, Object> generateClaims(JwtUser userDetail) {
        Map<String, Object> claims = new HashMap<>(16);
        claims.put(CLAIM_KEY_USER_ID, userDetail.getId());
        return claims;
    }

    private String generateAccessToken(String subject, Map<String, Object> claims,long expiration,String secret) {
        return generateToken(subject, claims, expiration,secret);
    }


    private String generateRefreshToken(String subject, Map<String, Object> claims,long expiration,String secret) {
        return generateToken(subject, claims, expiration,secret);
    }

    private String generateToken(String subject, Map<String, Object> claims, long expiration,String secret) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate(expiration))
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SIGNATURE_ALGORITHM, secret)
                .compact();
    }

}