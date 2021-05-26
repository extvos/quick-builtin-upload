package org.extvos.builtin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author shenmc
 */
@EntityScan("org.extvos.builtin.entity")
@MapperScan("org.extvos.builtin.mapper")
@ComponentScan(basePackages = "org.extvos.builtin")
public class BuiltinAutoConfigure {
}
