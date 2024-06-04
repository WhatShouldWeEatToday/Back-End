package kit.project.whatshouldweeattoday.security.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExtractUtil { // HTTP 요청의 Authorization 헤더에서 JWT 토큰 추출

    private static final String BEARER_TYPE = "Bearer";

    public static String extractToken(String authHeaderValue) {
        if (authHeaderValue.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
            return authHeaderValue.substring(BEARER_TYPE.length()).trim();
        }
        return null;
    }
}