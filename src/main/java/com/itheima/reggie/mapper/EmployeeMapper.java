package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: JinjiangSa
 * @Date: 2022/05/28/9:40
 * @Description:
 */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
