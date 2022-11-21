package com.example;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyRequestContext;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.Headers;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.function.aws.proxy.MicronautLambdaHandler;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public final class TestUtils {
    private TestUtils() {

    }

    public static <T> Response<T> exchange(ObjectMapper objectMapper,
                                    MicronautLambdaHandler handler,
                                    Context context,
                                    HttpRequest<?> request,
                                       @Nullable Class<T> responseType) throws JsonProcessingException {
        AwsProxyRequest awsProxyRequest = new AwsProxyRequest();
        awsProxyRequest.setRequestContext(new AwsProxyRequestContext());
        awsProxyRequest.setHttpMethod(request.getMethod().toString());
        awsProxyRequest.setPath(request.getPath());

        Headers headers = new Headers();
        for (String headerName : request.getHeaders().names()) {
            headers.put(headerName, request.getHeaders().getAll(headerName));
        }
        awsProxyRequest.setMultiValueHeaders(headers);
        if (request.getMethod() == HttpMethod.POST) {
            request.getBody().ifPresent(b -> {

                if (request.getContentType().isPresent() && request.getContentType().get().toString().equals(MediaType.APPLICATION_FORM_URLENCODED.toString())) {
                    request.getBody(Map.class).ifPresent(m -> {
                        Map<String, Object> body = (Map<String, Object>) m;
                        String formUrlEncodedBody = body.entrySet().stream()
                                .map(p -> urlEncodeUTF8(p.getKey()) + "=" + urlEncodeUTF8(p.getValue().toString()))
                                .reduce((p1, p2) -> p1 + "&" + p2)
                                .orElse("");
                        awsProxyRequest.setBody(formUrlEncodedBody);
                    });
                } else {
                    try {
                        awsProxyRequest.setBody(objectMapper.writeValueAsString(b));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        AwsProxyResponse response = handler.handleRequest(awsProxyRequest, context);
        return responseType == null ?
                new Response(response.getStatusCode()) :
                new Response(response.getStatusCode(), objectMapper.readValue(response.getBody(), responseType));
    }

    static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }
    static String urlEncodeUTF8(Map<?,?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    urlEncodeUTF8(entry.getKey().toString()),
                    urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();
    }
}
