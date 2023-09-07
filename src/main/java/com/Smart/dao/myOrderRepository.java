package com.Smart.dao;

import com.Smart.entities.MyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface myOrderRepository extends JpaRepository<MyOrder,Long> {

    public MyOrder findByOrderId(String orderId);
}
