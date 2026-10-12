package onon1101.lendingsystem.web.pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PaginationRequest(@Min(0) Integer page, @Min(1) @Max(100) Integer size) {

    public PaginationRequest {
        page = page == null ? 0 : page;
        size = size == null ? 20 : size;
    }

}
