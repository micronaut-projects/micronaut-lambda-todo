package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.json.JsonMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public final class TestUtils {
    private TestUtils() {

    }

    public static APIGatewayProxyRequestEvent create(@NonNull HttpRequest<?> request, JsonMapper jsonMapper) {
        Map<String, String> headers = new LinkedHashMap<>();
        Map<String, List<String>> multiHeaders = new LinkedHashMap<>();
        request.getHeaders().forEach((name, values) -> {
            if (values.size() > 1) {
                multiHeaders.put(name, values);
            } else {
                headers.put(name, values.get(0));
            }
        });
        return new APIGatewayProxyRequestEvent() {

            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            public Map<String, List<String>> getMultiValueHeaders() {
                return multiHeaders;
            }

            @Override
            public Map<String, String> getQueryStringParameters() {
                Map<String, String> result = new HashMap<>();
                for (String paramName : request.getParameters().names()) {
                    result.put(paramName, request.getParameters().get(paramName));
                }
                return result;
            }

            @Override
            public Map<String, List<String>> getMultiValueQueryStringParameters() {
                Map<String, List<String>> result = new HashMap<>();
                for (String paramName : request.getParameters().names()) {
                    result.put(paramName, request.getParameters().getAll(paramName));
                }
                return result;
            }

            @Override
            public String getPath() {
                return request.getPath();
            }

            @Override
            public String getHttpMethod() {
                return request.getMethodName();
            }

            @Override
            public String getBody() {
                return request.getBody()
                        .flatMap(b -> bodyAsString(jsonMapper,
                                () -> request.getContentType().orElse(null),
                                request::getCharacterEncoding,
                                () -> b)
                        ).orElse(null);
            }
        };
    }

    public static Optional<String> bodyAsString(@NonNull JsonMapper jsonMapper,
                                                @NonNull Supplier<MediaType> contentTypeSupplier,
                                                @NonNull Supplier<Charset> characterEncodingSupplier,
                                                @NonNull Supplier<Object> bodySupplier) {
        Object body = bodySupplier.get();
        MediaType mediaType = contentTypeSupplier.get();
        boolean mapFromJson = mediaType == null || mediaType.equals(MediaType.APPLICATION_JSON_TYPE);
        if (body instanceof CharSequence) {
            return Optional.of(body.toString());
        } else if (body instanceof byte[] bytes) {
            return Optional.of(new String(bytes, characterEncodingSupplier.get()));
        } else if (mapFromJson) {
            try {
                return Optional.of(jsonMapper.writeValueAsString(body));
            } catch (IOException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static <T> Response<T> exchange(
            ApiGatewayProxyRequestEventFunction handler,
            JsonMapper jsonMapper,
            Context context,
            HttpRequest<?> request,
            @Nullable Class<T> responseType
    ) throws IOException {
        APIGatewayProxyRequestEvent awsProxyRequest = create(request, jsonMapper);
        APIGatewayProxyResponseEvent awsProxyResponse = handler.handleRequest(awsProxyRequest, context);
        return new Response(awsProxyResponse.getStatusCode(), awsProxyResponse.getBody());
    }

    static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    static String urlEncodeUTF8(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
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
