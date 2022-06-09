package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface DishService extends IService<Dish> {
    /**
     * 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id获取数据
     * @param id
     * @return
     */
    DishDto selectById(Long id);

    /**
     * 修改菜品，同时修改菜品对应的口味数据，需要操作两张表：dish、dish_flavor
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);

    /**
     * 根据id单/批量逻辑删除
     * @param ids
     */
    void deleteByIds(Long[] ids);


}
