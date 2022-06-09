package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishFlavorMapper;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
     *
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //由于dishDto继承了Dish，因此Dish直接保存其对应的数据
        //保存菜品的基本信息到菜品表dish
        dishMapper.insert(dishDto);

        //dishDto.getId()拿到的是dish表格的id,并且这个id也对应着dish_flavor表格的dish_id
        Long dishId = dishDto.getId();

        //获取菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //使用stream流对每个菜品口味的dish_id进行赋值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id获取数据
     *
     * @param id
     * @return
     */
    @Override
    public DishDto selectById(Long id) {
        //通过id将dish包含的基本数据拿到
        Dish dish = dishMapper.selectById(id);

        log.info(dish.toString());
        DishDto dishDto = new DishDto();
        //将dish拷贝到dishDto
        BeanUtils.copyProperties(dish, dishDto);

        //通过dish表的id找到dishFlavor表中对应的风味集合
        LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper();
        flavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavorList = dishFlavorService.list(flavorLambdaQueryWrapper);

        dishDto.setFlavors(flavorList);

        return dishDto;
    }

    /**
     * 修改菜品，同时修改菜品对应的口味数据，需要操作两张表：dish、dish_flavor
     *
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //先将dish表格的数据修改
        dishMapper.updateById(dishDto);

        //对于dish_flavor表格，先将对应的数据库中原有数据删除，然后再将修改后的数据写入到数据库中
        //删除原有数据
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper();
        lqw.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(lqw);

        //获取修改后的菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        //将dish_id写入
        flavors = flavors.stream().map((item) -> {
            Long dishId = dishDto.getId();
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //将修改后的数据写入数据库
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id单、批量逻辑删除菜品，口味不需要操作，因此只需要操作dish表
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {

        //将要删除的菜品的id信息封装成list集合
        List<Long> deleteList = new ArrayList<>();
        for (Long id : ids) {
            deleteList.add(id);
        }

        //逻辑删除dish表格中的数据
        dishMapper.deleteBatchIds(deleteList);

    }


}
