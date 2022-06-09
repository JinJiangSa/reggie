package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);

        return R.success("添加成功");
    }

    /**
     * 查询用户的所有收货地址
     *
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    //由于用户的收货地址可能不止一个，因此返回的收货地址要用list集合传
    public R<List<AddressBook>> list(AddressBook addressBook) {
        //拿到当前登录的用户id
        addressBook.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        addressBookLambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> addressBookList = addressBookService.list(addressBookLambdaQueryWrapper);

        return R.success(addressBookList);

    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> setDefaultAddress(@RequestBody AddressBook addressBook) {
        log.info(addressBook.toString());
        //不能直接去设置默认地址，这样会导致多个默认地址，但实际中我们只有一个默认地址
        //我们需要先将该用户所有地址的is_default更新为0 , 然后将当前的设置的默认地址的is_default设置为1
        //在QueryWrapper中是获取LambdaQueryWrapper
        //在UpdateWrapper中是获取LambdaUpdateWrapper
        LambdaUpdateWrapper<AddressBook> addressBookLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        //去查找当前用户的id
        addressBookLambdaUpdateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        //将当前用户的所有地址全部设置为非默认地址
        addressBookLambdaUpdateWrapper.set(AddressBook::getIsDefault, 0);
        //注意注意：update是修改全部，而updateById是根据id进行修改
        addressBookService.update(addressBookLambdaUpdateWrapper);
        //设置默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return R.success("设置默认地址成功");
    }

    /**
     * 获取默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefaultAddress(){
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        addressBookLambdaQueryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(addressBookLambdaQueryWrapper);

        if(addressBook != null){
            return R.success(addressBook);
        }

        return R.error("获取默认地址失败");
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> selectById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return R.success(addressBook);
    }

    /**
     * 修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        // log.info(addressBook.toString());
        addressBookService.updateById(addressBook);
        return R.success("修改成功");

    }

    /**
     * 删除地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(Long ids){
        addressBookService.removeById(ids);
        return R.success("删除成功");
    }

}
