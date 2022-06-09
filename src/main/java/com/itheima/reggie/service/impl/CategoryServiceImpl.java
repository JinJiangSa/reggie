package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id进行删除分类，删除之前需要判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {

        //查询当前分类是否关联了菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //如果已经关联了菜品，那么抛出一个异常
        if (count1 > 0) {
            throw new CustomException("当前分类关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);//相当于where category_id = #{id}
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        //如果已经关联了套餐，那么抛出一个异常
        if (count2 > 0) {
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        //说明当前分类没有关联菜品以及套餐，可以直接删除
        super.removeById(id);
    }
}
