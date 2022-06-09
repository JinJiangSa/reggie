package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {

        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        //查找条件，相当于where name = #{name}
        lqw.like(name != null, Dish::getName, name);
        //根据更新时间降序排序
        lqw.orderByDesc(Dish::getUpdateTime);

        dishService.page(dishPage, lqw);

        //对象拷贝
        //此处对象拷贝中需要将dishPage中的records忽略掉，由于原records即展示出来的页面数据中菜品分类是categoryId
        //而在list.html中我们菜品分类需要的是categoryName，属性不一致，需要对其重新设置值
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");
        List<Dish> records = dishPage.getRecords();
        //使用stream流中的map方法，将原List<Dish> records转换成List<DishDto> list
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //将当前界面数据records拷贝给dishDto
            BeanUtils.copyProperties(item, dishDto);
            //拿到当前页面的菜品分类id
            Long categoryId = item.getCategoryId();
            //通过菜品id拿到category
            Category category = categoryService.getById(categoryId);
            //拿到菜品id对应的菜品名称
            String categoryName = category.getName();
            if (categoryName != null) {
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        //重新对records赋值
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id进行数据回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> selectById(@PathVariable Long id) {
        DishDto dishDto = dishService.selectById(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> updateWithFlavor(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据id单/批量删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteByIds(Long[] ids) {
        dishService.deleteByIds(ids);
        return R.success("删除成功");
    }

    /**
     * 根据id单/批量禁售菜品
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> updateEnableStatus(Long[] ids){

        for (Long id : ids) {
            LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(Dish::getId,id);
            Dish dish = dishService.getOne(dishLambdaQueryWrapper);
            dish.setStatus(0);
            dishService.updateById(dish);
        }
        return R.success("修改菜品状态成功");
    }

    /**
     * 根据id单/批量起售菜品
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> updateDisableStatus(Long[] ids){

        for (Long id : ids) {
            LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(Dish::getId,id);
            Dish dish = dishService.getOne(dishLambdaQueryWrapper);
            dish.setStatus(1);
            dishService.updateById(dish);
        }
        return R.success("修改菜品状态成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    //由于菜品不只一个，因此用List集合进行传递
    public R<List<DishDto>> queryDishList(Dish dish){
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //将前端传回来的分类categoryId在dish表中查找对应的category_id
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,dish.getCategoryId());
        //只展示启售的菜品
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //在dish表中查找对应的菜品
        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);

        List<DishDto> dishDtoList = dishList.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);

            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());


        return R.success(dishDtoList);
    }
}
