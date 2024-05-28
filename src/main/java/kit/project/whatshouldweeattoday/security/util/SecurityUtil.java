package kit.project.whatshouldweeattoday.security.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    public static String getLoginId(){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername();
    }
}
