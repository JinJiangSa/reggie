package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *分类管理
 */

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分类信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //构造分页构造器，page表示当前页，pageSize表示当前页展示的条数
        Page<Category> pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper();
        //添加排序条件
        lqw.orderByAsc(Category::getSort);

        //执行查询
        categoryService.page(pageInfo,lqw);

        return R.success(pageInfo);

    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long id){
        categoryService.remove(id);

        // categoryService.removeById(id);
        return R.success("删除成功");
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    //因为我们的前端传过来的type是"1"，表示的是菜品类型，而菜品类型有多个，需要用list集合接收而不能使用Category实体类接收
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper();
        //相当于where type = #{type}
        lqw.eq(category.getType() != null,Category::getType,category.getType());
        //按照顺序进行升序排序，如果顺序一样按照更新时间降序排序
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //查询到所有的菜品类型
        List<Category> list = categoryService.list(lqw);

        return R.success(list);
    }
}
