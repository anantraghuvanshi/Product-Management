package com.anant.order_service.service;

import com.anant.order_service.dto.OrderListItemsDto;
import com.anant.order_service.dto.OrderRequest;
import com.anant.order_service.model.Order;
import com.anant.order_service.model.OrderListItems;
import com.anant.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderListItems> orderListItems = orderRequest.getOrderListItemsDtoList().stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderListItems);
        orderRepository.save(order);
    }

    private OrderListItems mapToDto(OrderListItemsDto orderListItemsDto) {
        OrderListItems orderListItems = new OrderListItems();
        orderListItems.setPrice(orderListItemsDto.getPrice());
        orderListItems.setQuatity(orderListItemsDto.getQuantity());
        orderListItems.setSkucode(orderListItemsDto.getSkucode());
        return orderListItems;
    }
}
