package cn.hermesdi.crypto.advice;

import cn.hermesdi.crypto.algorithm.ApiCryptoAlgorithm;
import cn.hermesdi.crypto.annotation.NotEncrypt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @Author hermes·di
 * @Date 2020/7/6 0006 11:40
 * @Describe 加密响应类
 */
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object>, Serializable {

    private static final Log logger = LogFactory.getLog(EncryptResponseBodyAdvice.class);

    @Autowired(required = false)
    private List<ApiCryptoAlgorithm> apiCryptoAlgorithms;

    private ApiCryptoAlgorithm apiCryptoAlgorithm;


    @Override
    public boolean supports(MethodParameter parameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // 方法上有排除加密注解
        if (parameter.hasMethodAnnotation(NotEncrypt.class)) {
            return false;
        }

        if (Objects.nonNull(apiCryptoAlgorithms)) {
            logger.debug("【ApiCrypto】 all Encrypt Algorithm : [" + apiCryptoAlgorithms + "]");

            for (ApiCryptoAlgorithm a : apiCryptoAlgorithms) {
                if (a.isCanRealize(parameter, false)) {
                    apiCryptoAlgorithm = a;
                    return true;
                }
            }
        } else {
            logger.debug("【ApiCrypto】 no Encrypt Algorithm.( 没有可用的 ApiCryptoAlgorithm 实现 )");
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        return apiCryptoAlgorithm.responseBefore(body, methodParameter, mediaType, aClass, serverHttpRequest, serverHttpResponse);
    }

}