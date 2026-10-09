package onon1101.lendingsystem.configurations.pagination;

public record PaginationResponse(
        int page,
        int size,
        boolean hasNext
) {

    public static PaginationResponse from(
            PaginationRequest request,
            boolean hasNext
    ) {
        return new PaginationResponse(
                request.page(),
                request.size(),
                hasNext
        );
    }
}
