package pl.sonmiike.financeapiservice.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagedUsersDTO {

    private int currentPage;
    private int totalPages;
    private Iterable<UserDTO> users;

}
