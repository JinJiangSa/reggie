package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，需要同时操作setmeal和setmeal_dish表格
     *
     * @param setmealDto
     */
    @Override
    public void saveWithSetmealDish(SetmealDto setmealDto) {
        //将基本数据存入setmeal表中
        setmealMapper.insert(setmealDto);

        //将套餐菜品数据存入setmeal_dish表中，同时还要将对应的setmealId一同存入
        //获取到setmealDish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //使用stream流对每个套餐菜品的setmeal_id进行赋值
        setmealDishes = setmealDishes.stream().map((item) -> {
            //拿到setmealId
            Long setmealId = setmealDto.getId();
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    @Override
    public SetmealDto selectById(Long id) {
        //先通过id将setmeal的基本数据查询到
        Setmeal setmeal = setmealMapper.selectById(id);

        //将setmeal数据拷贝到setmealDto
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        //再通过id将setmeal_dish的菜品数据查询到
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);
        //设置setmealDto的setmealDish值
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }


    /**
     * 修改套餐
     *
     * @param setmealDto
     */
    @Override
    public void updateBySetmealDish(SetmealDto setmealDto) {
        //先将setmeal表的数据修改
        setmealMapper.updateById(setmealDto);

        //对于setmeal_dish表格，先将对应位置的原有数据删除，然后再将修改后的数据写入到数据库中
        //删除setmeal_dish表格对应位置的原有数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //由于setmealDto继承setmeal,因此setmealDto的id其实就是setmeal的id
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);


        //获取修改后的套餐菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //将setmeal_id写入
        setmealDishes = setmealDishes.stream().map((item) -> {
            Long setmealId = setmealDto.getId();
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        //将修改后的数据重新写入setmeal_dish表中
        setmealDishService.saveBatch(setmealDishes);
    }


    /**
     * 根据id单/批量删除
     * @param ids
     */
    @Override
    public void deleteByIds(Long[] ids) {
        List<Long> deleteList = new ArrayList<>();
        for (Long id : ids) {
            deleteList.add(id);
        }

        setmealMapper.deleteBatchIds(deleteList);
    }
}
