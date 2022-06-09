package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 结算
     *
     * @param orders
     */
    void submit(Orders orders);

    /**
     * 历史订单分页查询
     * @param page
     * @param pageSize
     * @return
     */
    Page<OrdersDto> orderDetailPage(int page, int pageSize);

    /**
     * 再来一单
     * @param orders
     */
    void again(Orders orders);


}
