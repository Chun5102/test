package com.course.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.course.entity.StaffEntity;
import com.course.entity.TableEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtUtil {

    private String secretKey;

    private Long expiration;

    private Long accessExpiration;

    private Long refreshExpiration;

    public String generateTableToken(TableEntity tableEntity) {

        return Jwts.builder()
                .setSubject("tableToken")
                .claim("tableId", tableEntity.getId())
                .claim("openedAt", tableEntity.getOpenedAt().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token).getBody();
    }

    public String generateAccessToken(StaffEntity staffEntity) {
        return Jwts.builder()
                .setSubject("accessToken")
                .claim("staffId", staffEntity.getId())
                .claim("name", staffEntity.getName())
                .claim("username", staffEntity.getUsername())
                .claim("role", staffEntity.getRole())
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(StaffEntity staffEntity) {
        return Jwts.builder()
                .setSubject("refreshToken")
                .claim("staffId", staffEntity.getId())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
