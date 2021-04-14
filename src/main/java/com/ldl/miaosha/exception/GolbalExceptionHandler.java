package com.ldl.miaosha.exception;

import com.ldl.miaosha.result.CodeMsg;
import com.ldl.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@ResponseBody
public class GolbalExceptionHandler {

    @ExceptionHandler(value=Exception.class)
    public Result<CodeMsg> exceptionHandler(HttpServletRequest httpServletRequest, Exception exception) {
        //exception.printStackTrace();
        if (exception instanceof BindException) {
            BindException bindException = (BindException) exception;
            List<ObjectError> allErrors = bindException.getAllErrors();
            ObjectError objectError = allErrors.get(0);
            String defaultMessage = objectError.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillAsg(defaultMessage));
        } else if (exception instanceof GolbalException) {
            GolbalException golbalException = (GolbalException) exception;
            return Result.error(golbalException.getCodeMsg());
        } else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
