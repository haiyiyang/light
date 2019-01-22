package com.haiyiyang.light.service.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.stereotype.Service;

@Service
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface IAmALightService {

}
