package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithSetmealDish(setmealDto);
        return R.success("添加菜品成功");
    }

    /**
     * 套餐分页
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.like(name != null, Setmeal::getName, name);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(setmealPage, setmealLambdaQueryWrapper);

        //属性拷贝
        //此处对象拷贝中需要将setmealPage中的records忽略掉，由于原records即展示出来的页面数据中菜品分类是categoryId
        //而在list.html中我们菜品分类需要的是categoryName，属性不一致，需要对其重新设置值
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        List<Setmeal> setmealRecords = setmealPage.getRecords();
        List<SetmealDto> setmealDtoRecords = setmealRecords.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //将setmeal的值拷贝到setmealDto
            BeanUtils.copyProperties(item,setmealDto);
            //拿到当前页面分类id
            Long categoryId = item.getCategoryId();
            //通过分类id获取到菜品类别
            Category category = categoryService.getById(categoryId);
            //通过菜品类别获取到类别名
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());

        //重新设置records
        setmealDtoPage.setRecords(setmealDtoRecords);

        return R.success(setmealDtoPage);
    }

    /**
     * 根据id查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> selectById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.selectById(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> updateBySetmealDish(@RequestBody SetmealDto setmealDto){
        setmealService.updateBySetmealDish(setmealDto);
        return R.success("修改套餐成功");

    }

    /**
     * 根据id单/批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByIds(Long[] ids){
        setmealService.deleteByIds(ids);
        return R.success("删除成功");
    }

    /**
     * 根据id单/批量禁售套餐
     * @return
     */
    @PostMapping("/status/0")
    public R<String> updateEnableStatus(Long[] ids){
        for (Long id : ids) {
            LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealLambdaQueryWrapper.eq(Setmeal::getId,id);
            Setmeal setmeal = setmealService.getOne(setmealLambdaQueryWrapper);
            setmeal.setStatus(0);
            setmealService.updateById(setmeal);
        }

        return R.success("禁售成功");
    }

    /**
     * 根据id单/批量启售套餐
     * @return
     */
    @PostMapping("/status/1")
    public R<String> updateDisEnableStatus(Long[] ids){
        for (Long id : ids) {
            LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealLambdaQueryWrapper.eq(Setmeal::getId,id);
            Setmeal setmeal = setmealService.getOne(setmealLambdaQueryWrapper);
            setmeal.setStatus(1);
            setmealService.updateById(setmeal);
        }

        return R.success("启售成功");
    }

    /**
     * 手机端查询套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> querySetmealList(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,setmeal.getStatus());

        List<Setmeal> setmealList = setmealService.list(setmealLambdaQueryWrapper);

        return R.success(setmealList);
    }


}
