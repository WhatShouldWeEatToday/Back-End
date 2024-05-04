package kit.project.whatshouldweeattoday.domain.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtils {
    public static Pageable createSortedPageable(Pageable originalPageable, String sortProperty) {
        return PageRequest.of(originalPageable.getPageNumber(), originalPageable.getPageSize(), Sort.by(sortProperty).ascending());
    }
}

