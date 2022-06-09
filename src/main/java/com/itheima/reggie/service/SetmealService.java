package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐
     * @param setmealDto
     */
    void saveWithSetmealDish(SetmealDto setmealDto);

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    SetmealDto selectById(Long id);

    /**
     * 修改套餐
     * @param setmealDto
     */
    void updateBySetmealDish(SetmealDto setmealDto);

    /**
     * 根据id单/批量删除
     * @param ids
     */
    void deleteByIds(Long[] ids);
}
