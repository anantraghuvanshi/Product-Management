package com.anant.order_service.service;

import com.anant.order_service.dto.InventoryResponse;
import com.anant.order_service.dto.OrderListItemsDto;
import com.anant.order_service.dto.OrderRequest;
import com.anant.order_service.model.Order;
import com.anant.order_service.model.OrderListItems;
import com.anant.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderListItems> orderListItems = orderRequest.getOrderListItemsDtoList().stream()
                .map(this::mapToDto)
                .toList();


        order.setOrderLineItemsList(orderListItems);

        List<String> skuCodes =  order.getOrderLineItemsList().stream()
                .map(OrderListItems::getSkucode)
                .toList();

        InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean allProductIsInStock = Arrays.stream(inventoryResponsesArray)
                .allMatch(InventoryResponse::isInStock);
        if (allProductIsInStock){
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product not in stock, please try again!");
        }

    }

    private OrderListItems mapToDto(OrderListItemsDto orderListItemsDto) {
        OrderListItems orderListItems = new OrderListItems();
        orderListItems.setPrice(orderListItemsDto.getPrice());
        orderListItems.setQuatity(orderListItemsDto.getQuantity());
        orderListItems.setSkucode(orderListItemsDto.getSkucode());
        return orderListItems;
    }
}
