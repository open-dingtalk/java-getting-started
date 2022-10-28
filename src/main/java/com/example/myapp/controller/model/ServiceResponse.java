package com.example.myapp.controller.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jack.kj@alibaba-inc.com
 * @date 2022/10/2022/10/24
 */
@Getter
@Setter
public class ServiceResponse<T> {
    private boolean success;
    private Integer code;
    private String message;
    private T data;

    public static <T> ServiceResponse<T> buildSuccessServiceResponse(T data) {
        ServiceResponse<T> serviceResponse = new ServiceResponse<>();
        serviceResponse.setData(data);
        serviceResponse.setSuccess(true);
        serviceResponse.setCode(200);
        serviceResponse.setMessage("ok");

        return serviceResponse;
    }
}
