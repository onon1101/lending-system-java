package onon1101.lendingsystem.list;


import onon1101.lendingsystem.web.pagination.PaginationResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ListOrderResponse(
        List<OrderResponse> orders,
        PaginationResponse pagination
) {
    public ListOrderResponse {
        orders = List.copyOf(orders);
    }

    public static ListOrderResponse from(ListOrderResult result) {
        List<OrderResponse> orders = result.orders()
                .stream()
                .map(OrderResponse::from)
                .toList();

        PaginationResponse pagination = new PaginationResponse(
                result.page(),
                result.size(),
                result.hasNext()
        );

        return new ListOrderResponse(orders, pagination);
    }

    public record OrderResponse(
            UUID orderId,
            UUID itemId,
            String itemName,
            UUID borrowerId,
            String borrowerUsername,
            UUID lenderId,
            String lenderUsername,
            String status,
            LocalDate plannedBorrowDate,
            LocalDate plannedReturnDate,
            Instant requestedAt
    ) {
        public static OrderResponse from(ListedOrder order) {
            return new OrderResponse(
                    order.orderId(),
                    order.itemId(),
                    order.itemName(),
                    order.borrowerId(),
                    order.borrowerUsername(),
                    order.lenderId(),
                    order.lenderUsername(),
                    order.status(),
                    order.plannedBorrowDate(),
                    order.plannedReturnDate(),
                    order.requestedAt()
            );
        }
    }
}
