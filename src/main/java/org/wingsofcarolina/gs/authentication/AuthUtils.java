package org.wingsofcarolina.gs.authentication;

import java.util.Date;
import java.util.HashMap;

import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wingsofcarolina.gs.GsConfiguration;
import org.wingsofcarolina.gs.model.User;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;

public class AuthUtils {
	private static final Logger LOG = LoggerFactory.getLogger(AuthUtils.class);

	private static AuthUtils instance = null;
	
	// For SecretKeySpec generation
	private String algorithm = "HmacSHA512";
	private byte[] encoded = {-8, -36, 93, 58, -106, 123, -77, -120, -119, 80, -67, -58, -103,
			                  40, 49, -81, 51, -91, -19, 83, -67, 69, 22, 71, 74, -109, -125, 67,
			                  -72, -39, -11, -63, 42, 1, 5, 3, -32, -97, -21, -67, -127, 47,
			                  -46, -108, 99, -69, 36, 120, -67, 92, 113, 51, 96, 34, 67, -12, -44,
			                  -31, -117, -37, 92, -97, -100, 67};

	private SecretKeySpec key;
	private JwtParser parser;
	private ObjectMapper mapper;


	public AuthUtils() {
		key = new SecretKeySpec(encoded, algorithm);
		parser = Jwts.parser().setSigningKey(key);
		mapper = new ObjectMapper();
	}

	public static AuthUtils instance() {
		if (instance == null) {
			instance = new AuthUtils();
		}
		return instance;
	}

	// The following header hack is due to (a) Chrome demanding SameSite be set
	// and (b) NewCookie having no way to freaking do that. WTF people? So instead
	// of using the .cookie() call on the Response the cookie gets turned into a
	// String, and the SameSite setting gets added to the end, and the .header()
	// function is used instead. What a hack.
	public static String sameSite(NewCookie cookie) {
		return cookie.toString() + ";SameSite=None";
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
	
	public String generateToken(User user) {
		// Now generate the Java Web Token
		// https://github.com/jwtk/jjwt
		// https://stormpath.com/blog/jwt-java-create-verify
		// https://scotch.io/tutorials/the-anatomy-of-a-json-web-token
		Claims claims = new DefaultClaims();
		claims.setIssuedAt(new Date());
		claims.setSubject(user.getName());
		claims.put("version", 1);
		claims.put("email", user.getEmail());
		claims.put("userId", user.getUUID());
		claims.put("admin", user.isAdmin());
		
		String compactJws = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS512, key).compact();

		return compactJws;
	}

	public NewCookie generateCookie(User user) {
		boolean secure = GsConfiguration.instance().getMode().compareTo("DEV") == 0 ? false : true;
		int maxAge = 86400*30;  // Seconds per day, times days to live
		NewCookie cookie = new NewCookie("wcfc.gs.token", generateToken(user), "/", null, "WCFC Groundschool ID", maxAge, secure, true);
		return cookie;
	}

	public NewCookie removeCookie() {
		boolean secure = GsConfiguration.instance().getMode().compareTo("DEV") == 0 ? false : true;
		return new NewCookie("wcfc.gs.token", null, "/", null, "WCFC Groundschool ID", 0, secure, true);
	}

	public User getUserFromCookie(Cookie cookie) {
		User user = null;
		
		if (cookie != null) {
			Jws<Claims> claims = decodeCookie(cookie);
			Claims body = claims.getBody();
			
			user = new User(
					(String) body.getSubject(),
					(String) body.get("email"),
					(String) body.get("userId")
			);
			user.setAdmin((Boolean) body.get("admin"));
		}
		return user;
	}
}
