package org.wingsofcarolina.gs.authentication;

import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.GsConfiguration;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.DefaultClaims;

public class AuthUtils {
	private static final Logger LOG = LoggerFactory.getLogger(AuthUtils.class);

	private static AuthUtils instance = null;
	
	// For SecretKeySpec generation
	private String algorithm = "HmacSHA512";
	private byte[] encoded = {-8, -36, 93, 58, -106, 123, -77, -120, -119, 80, -67, -58, -103,
			                  40, 49, -81, 51, -91, -19, 83, -67, 69, 22, 71, 74, -109, -125, 67,
			                  -72, -39, -11, -63, 42, -121, -85, 3, -32, -97, -21, -67, -127, 47,
			                  -46, -108, 99, -69, 36, 120, -67, 92, 113, 51, 96, 34, 67, -12, -44,
			                  -31, -117, -37, 92, -97, -100, 67};

	private SecretKeySpec key;
	private JwtParser parser;


	public AuthUtils() {
		key = new SecretKeySpec(encoded, algorithm);
		parser = Jwts.parser().setSigningKey(key);
	}

	public static AuthUtils instance() {
		if (instance == null) {
			instance = new AuthUtils();
		}
		return instance;
	}
	
	public SecretKeySpec getKey() {
		return key;
	}
	
	public JwtParser getParser() {
		return parser;
	}

	
	public Jws<Claims> decodeCookie(Cookie cookie) {
		Jws<Claims> claims = null;
		String compactJws = cookie.getValue();
		if (compactJws != null && !compactJws.isEmpty()) {
			claims = parser.setSigningKey(key).parseClaimsJws(compactJws);
		}
		return claims;
	}
	
	public String generateToken() {
		// Now generate the Java Web Token
		// https://github.com/jwtk/jjwt
		// https://stormpath.com/blog/jwt-java-create-verify
		// https://scotch.io/tutorials/the-anatomy-of-a-json-web-token
		Claims claims = new DefaultClaims();
		claims.setIssuedAt(new Date());
		//claims.setSubject(user.getEmail());
		//claims.put("email", user.getEmail());
		//claims.put("userId", user.getUserId());
		//claims.put("uuid", user.getUserId());
		String compactJws = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, key).compact();

		return compactJws;
	}

	public NewCookie generateCookie() {
		boolean secure = GsConfiguration.instance().getMode().compareTo("DEV") == 0 ? false : true;
		return new NewCookie("wcfc.gs.token", generateToken(), "/", "", "WCFC Groundschool ID", -1, secure, true);
	}

	public NewCookie removeCookie() {
		boolean secure = GsConfiguration.instance().getMode().compareTo("DEV") == 0 ? false : true;
		return new NewCookie("wcfc.gs.token", null, "/", "", "WCFC Groundschool ID", 0, secure, true);
	}
}
