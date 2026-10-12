package onon1101.lendingsystem.shared.pagination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PaginationTests {
    @Test
    void validatesBoundsAndUsesLongOffset() {
        assertThatIllegalArgumentException().isThrownBy(() -> new PageSpec(-1, 20));
        assertThatIllegalArgumentException().isThrownBy(() -> new PageSpec(0, 0));
        assertThatIllegalArgumentException().isThrownBy(() -> new PageSpec(0, 101));
        var page = new PageSpec(Integer.MAX_VALUE, 100);
        assertThat(page.offset()).isEqualTo(214748364700L);
        assertThat(page.fetchLimit()).isEqualTo(101);
    }

    @Test
    void detectsNextPageOnlyWhenAnExtraRowExists() {
        var page = new PageSpec(0, 2);
        assertThat(Slice.from(List.of(), page).hasNext()).isFalse();
        assertThat(Slice.from(List.of(1), page).hasNext()).isFalse();
        assertThat(Slice.from(List.of(1, 2), page).hasNext()).isFalse();
        var fetched = new ArrayList<>(List.of(1, 2, 3));
        var slice = Slice.from(fetched, page);
        fetched.clear();
        assertThat(slice.items()).containsExactly(1, 2);
        assertThat(slice.hasNext()).isTrue();
    }
}
