package com.zziri.tacocloud.data;

import com.zziri.tacocloud.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
