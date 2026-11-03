package com.mygym.app.config;

import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.mygym.app.models.Admin;
import com.mygym.app.models.AdminWrapper;
import com.mygym.app.models.Member;
import com.mygym.app.models.MemberWrapper;
import com.mygym.app.models.Trainer;
import com.mygym.app.models.TrainerWrapper;

@RestControllerAdvice
public class PasswordRedactingAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {
        return redact(body);
    }

    static Object redact(Object body) {
        return switch (body) {
            case Member member -> member.withoutPassword();
            case Admin admin -> admin.withoutPassword();
            case Trainer trainer -> trainer.withoutPassword();
            case MemberWrapper wrapper -> new MemberWrapper(redactAll(wrapper.allMembers()));
            case AdminWrapper wrapper -> new AdminWrapper(redactAll(wrapper.allAdmins()));
            case TrainerWrapper wrapper -> new TrainerWrapper(redactAll(wrapper.allTrainers()));
            case List<?> list -> redactAll(list);
            case null, default -> body;
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> redactAll(List<T> items) {
        return items.stream().map(item -> (T) redact(item)).toList();
    }
}
