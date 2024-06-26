package com.cdgeekcamp.shijianxing.util;

import com.cdgeekcamp.shijianxing.config.ApplicationConfig;
import com.cdgeekcamp.shijianxing.model.User;
import com.cdgeekcamp.shijianxing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;

@SuppressWarnings("unused")
@Component
public class LoginUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    ApplicationConfig applicationConfig;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        HttpSession session = request.getSession();
        final String userName = authentication.getName();

        String login_time = String.valueOf(new Timestamp(System.currentTimeMillis()));
        session.setAttribute("LOGIN_TIME", login_time);

        // 每次登录随机生成用户头像（主要为了多次更新磁盘文件，用于其它测试目的）
        final String imageFilename = UtilTool.createUserPhotoImage(applicationConfig.getUserPhotoSaveDir(), userName);
        session.setAttribute("PHOTO", "/images/" + imageFilename);

        User existingUser = userService.findUserByEmail(userName);

        if (existingUser != null) {
            session.setAttribute("USER_NAME", existingUser.getName());
        }

        redirectStrategy.sendRedirect(request, response, "/");
    }
}