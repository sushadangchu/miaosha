package com.ldl.miaosha.exception;

import com.ldl.miaosha.result.CodeMsg;

public class GolbalException extends RuntimeException{

    private CodeMsg codeMsg;

    public GolbalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
