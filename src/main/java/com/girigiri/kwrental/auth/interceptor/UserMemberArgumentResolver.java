package com.girigiri.kwrental.auth.interceptor;

import com.girigiri.kwrental.auth.domain.Member;
import com.girigiri.kwrental.auth.domain.Role;
import com.girigiri.kwrental.auth.domain.SessionMember;
import com.girigiri.kwrental.auth.exception.MemberNotFoundException;
import com.girigiri.kwrental.auth.exception.MemberNotUserException;
import com.girigiri.kwrental.auth.exception.SessionNotFoundException;
import com.girigiri.kwrental.auth.repository.MemberRepository;
import com.girigiri.kwrental.common.CustomHandlerMethodArgumentResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserMemberArgumentResolver implements CustomHandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    public UserMemberArgumentResolver(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        final boolean assignableFrom = parameter.getParameterType().isAssignableFrom(SessionMember.class);
        return parameter.hasParameterAnnotation(UserMember.class) && assignableFrom;
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
        final SessionMember sessionMember = getSessionMember(webRequest);
        validateConsistency(sessionMember);
        if (sessionMember.getRole() != Role.USER) throw new MemberNotUserException();
        return sessionMember;
    }

    private SessionMember getSessionMember(final NativeWebRequest webRequest) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);
        if (session == null) throw new SessionNotFoundException();
        return (SessionMember) session.getAttribute("member");
    }

    private void validateConsistency(final SessionMember sessionMember) {
        final Member member = memberRepository.findById(sessionMember.getId())
                .orElseThrow(MemberNotFoundException::new);
        sessionMember.validateConsistency(member);
    }
}
