package cn.master.hub.handler.result;

import cn.master.hub.handler.Translator;
import cn.master.hub.handler.exception.CustomException;
import cn.master.hub.handler.exception.IResultCode;
import cn.master.hub.util.ServiceUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by 11's papa on 2025/8/29
 */
@RestControllerAdvice
public class RestControllerExceptionHandler {
    /**
     * 处理方法参数验证异常的处理器
     * 当控制器方法的参数验证失败时，此方法会捕获MethodArgumentNotValidException异常并进行处理
     *
     * @param ex 方法参数验证异常对象，包含验证失败的详细信息
     * @return 返回封装了验证错误信息的ResultHolder对象，包含错误码、错误消息和具体的字段错误信息
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultHolder handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 提取所有验证错误信息，构建字段名到错误消息的映射
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        // 返回验证失败的统一响应结果
        return ResultHolder.error(ResultCode.VALIDATE_FAILED.getCode(), ResultCode.VALIDATE_FAILED.getMessage(), errors);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResultHolder> handleCustomException(CustomException ex) {
        IResultCode errorCode = ex.getErrorCode();
        if (errorCode == null) {
            // 如果抛出异常没有设置状态码，则返回错误 message
            return ResponseEntity.internalServerError()
                    .body(ResultHolder.error(ResultCode.FAILED.getCode(), ex.getMessage()));
        }
        int code = errorCode.getCode();
        String message = errorCode.getMessage();
        message = Translator.get(message, message);

        if (errorCode instanceof ResultCode) {
            // 如果是 ResultCode，则设置响应的状态码，取状态码的后三位
            if (errorCode.equals(ResultCode.NOT_FOUND)) {
                message = getNotFoundMessage(message);
            }
            return ResponseEntity.status(code % 1000)
                    .body(ResultHolder.error(code, message, ex.getMessage()));
        } else {
            // 响应码返回 500，设置业务状态码
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultHolder.error(code, Translator.get(message, message), ex.getMessage()));
        }
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResultHolder> handleException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(ResultHolder.error(ResultCode.FAILED.getCode(),
                        e.getMessage(), getStackTraceAsString(e)));
    }

    public static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }

    private String getNotFoundMessage(String message) {
        String resourceName = ServiceUtils.getResourceName();
        if (StringUtils.isNotBlank(resourceName)) {
            message = String.format(message, Translator.get(resourceName, resourceName));
        } else {
            message = String.format(message, Translator.get("resource.name"));
        }
        ServiceUtils.clearResourceName();
        return message;
    }
}
