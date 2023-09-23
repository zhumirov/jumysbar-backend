package kz.btsd.edmarket.user.activity.filter;

import kz.btsd.edmarket.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@Component
public class UserActivityFilter implements Filter {
    @Autowired
    private UserService userService;

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        Principal principal = req.getUserPrincipal();
        if (principal != null && StringUtils.isNotBlank(principal.getName())) {
            userService.userLastActivityDateAsync(principal.getName());
        }
        chain.doFilter(request, response);
    }
}

