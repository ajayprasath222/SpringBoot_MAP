package com.example.printer_springbe.auth.web;

import com.example.printer_springbe.auth.constant.AuthConstants;
import com.example.printer_springbe.auth.model.AuthenticatedUser;
import com.example.printer_springbe.common.exception.BusinessException;
import com.example.printer_springbe.common.response.ResponseCode;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && AuthenticatedUser.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        AuthenticatedUser user = (AuthenticatedUser) webRequest.getAttribute(
                AuthConstants.AUTH_USER_REQUEST_ATTR,
                NativeWebRequest.SCOPE_REQUEST
        );
        if (user == null) {
            throw new BusinessException(ResponseCode.UNAUTHENTICATED, HttpStatus.UNAUTHORIZED);
        }
        return user;
    }
}
