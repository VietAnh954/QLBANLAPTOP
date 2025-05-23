package vn.hoidanit.laptopshop.config;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;

// import java.io.IOException;
// import java.util.Collection;
// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.web.DefaultRedirectStrategy;
// import org.springframework.security.web.RedirectStrategy;
// import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UserService;

// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import jakarta.servlet.http.HttpSession;
// import vn.hoidanit.laptopshop.service.UserService;

public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    protected String determineTargetUrl(final Authentication authentication) {

        Map<String, String> roleTargetUrlMap = new HashMap<>();
        roleTargetUrlMap.put("ROLE_USER", "/");
        roleTargetUrlMap.put("ROLE_ADMIN", "/admin");

        final Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (roleTargetUrlMap.containsKey(authorityName)) {
                return roleTargetUrlMap.get(authorityName);
            }
        }

        throw new IllegalStateException();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, Authentication authentication) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        // get email
        String email = authentication.getName();
        // query user
        Optional<User> user = this.userService.getUserByEmail(email);
        if (user != null) {
            session.setAttribute("fullName", user.get().getFullName());
            session.setAttribute("avatar", user.get().getAvatar());
            session.setAttribute("email", user.get().getEmail());
            session.setAttribute("id", user.get().getId());
            int sum = user.get().getCart() == null ? 0 : user.get().getCart().getSum();
            session.setAttribute("sum", sum);
        }

    }

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {

            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
        clearAuthenticationAttributes(request, authentication);
    }

    // @Autowired
    // private UserService userService;

    // protected String determineTargetUrl(final Authentication authentication) {

    // Map<String, String> roleTargetUrlMap = new HashMap<>();
    // roleTargetUrlMap.put("ROLE_USER", "/");
    // roleTargetUrlMap.put("ROLE_ADMIN", "/admin");

    // final Collection<? extends GrantedAuthority> authorities =
    // authentication.getAuthorities();
    // for (final GrantedAuthority grantedAuthority : authorities) {
    // String authorityName = grantedAuthority.getAuthority();
    // if (roleTargetUrlMap.containsKey(authorityName)) {
    // return roleTargetUrlMap.get(authorityName);
    // }
    // }

    // throw new IllegalStateException();
    // }

    // protected void handle(
    // HttpServletRequest request,
    // HttpServletResponse response,
    // Authentication authentication) throws IOException {

    // String targetUrl = determineTargetUrl(authentication);

    // if (response.isCommitted()) {

    // return;
    // }

    // redirectStrategy.sendRedirect(request, response, targetUrl);
    // }

    // protected void clearAuthenticationAttributes(HttpServletRequest request,
    // Authentication authentication) {
    // HttpSession session = request.getSession(false);
    // if (session == null) {
    // return;
    // }
    // session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

    // // get email
    // String email = authentication.getName();

    // // Query data

    // }

    // private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    // @Override
    // public void onAuthenticationSuccess(HttpServletRequest request,
    // HttpServletResponse response,
    // Authentication authentication) throws IOException, ServletException {
    // handle(request, response, authentication);

    // clearAuthenticationAttributes(request, authentication);

    // }

}
