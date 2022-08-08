package com.ssafy.B310.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ssafy.B310.exception.UnauthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtServiceImpl implements JwtService {

	public static final Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

	private static final String SALT = "yazanyaSecret";
	private static final int EXPIRE_MINUTES = 60;

	// Access 토큰 생성
	@Override
	public <T> String createAccessToken(String key, T data, String subject) {
		String jwt = Jwts.builder().setHeaderParam("typ", "ACCESS").setHeaderParam("regDate", System.currentTimeMillis())
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * EXPIRE_MINUTES)).setSubject(subject)
				.claim(key, data).signWith(SignatureAlgorithm.HS256, this.generateKey()).compact();
		
		return jwt;
	}
	
	// Refresh 토큰 생성
	@Override
	public <T> String createRefreshToken(String key, T data, String subject) {
		Claims claims = Jwts.claims();
		claims.put(key, data.toString());
		
		Date now = new Date();
		Date expiration = new Date(now.getTime() + 60 * 60 * 24 * 7 * 1000L);
		
		return Jwts.builder().setHeaderParam("typ", "REFRESH").setClaims(claims).setIssuedAt(now).setExpiration(expiration)
				.signWith(SignatureAlgorithm.HS256, this.generateKey()).compact();
		
		
//		String jwt = Jwts.builder().setHeaderParam("typ", "REFRESH").setHeaderParam("regDate", System.currentTimeMillis())
//				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * EXPIRE_MINUTES)).setSubject(subject)
//				.claim(key, data).signWith(SignatureAlgorithm.HS256, this.generateKey()).compact();
//		
//		return jwt;
	}

	private byte[] generateKey() {
		byte[] key = null;
		try {
			key = SALT.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			if (logger.isInfoEnabled()) {
				e.printStackTrace();
			} else {
				logger.error("Making JWT Key Error ::: {}", e.getMessage());
			}
		}
		return key;
	}

	@Override
	public Map<String, Object> get(String key) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String jwt = request.getHeader("access-token");
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parser().setSigningKey(SALT.getBytes("UTF-8")).parseClaimsJws(jwt);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new UnauthorizedException();
		}
		Map<String, Object> value = claims.getBody();
		logger.info("value : {}", value);
		return value;
	}

	@Override
	public String getUserID(String jwt) {
		Jws<Claims> claims = Jwts.parser().setSigningKey(this.generateKey()).parseClaimsJws(jwt);
		String userId = (String) claims.getBody().get("userId");
		return userId;
	}

	// 전달 받은 토큰 유효성 검사
	@Override
	public boolean isUsable(String jwt) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(this.generateKey()).parseClaimsJws(jwt);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	// JWT 갱신
	@Override
	public <T> String refresh(String jwt) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(this.generateKey()).parseClaimsJws(jwt);
			String userId = (String) claims.getBody().get("userId");
			System.out.println(userId + " 유저가 토큰을 갱신했습니다.");
			return createAccessToken("userId", userId, "access-token");
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

}
