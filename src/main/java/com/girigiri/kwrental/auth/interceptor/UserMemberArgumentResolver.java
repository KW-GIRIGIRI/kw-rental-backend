package com.girigiri.kwrental.auth.interceptor;

import com.girigiri.kwrental.auth.domain.Role;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.exception.MemberNotUserException;
import com.girigiri.kwrental.auth.exception.SessionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class UserMemberArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        final boolean assignableFrom = parameter.getParameterType().isAssignableFrom(SessionMember.class);
        return parameter.hasParameterAnnotation(UserMember.class) && assignableFrom;
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);
        if (session == null) throw new SessionNotFoundException();
        final SessionMember sessionMember = (SessionMember) session.getAttribute("member");
        if (sessionMember.getRole() != Role.USER) throw new MemberNotUserException();
        return sessionMember;
    }
}
