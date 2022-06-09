package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Employee;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: JinjiangSa
 * @Date: 2022/05/28/9:42
 * @Description:
 */
public interface EmployeeService extends IService<Employee> {

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    Page<Employee> page(int page,int pageSize,String name);
}
