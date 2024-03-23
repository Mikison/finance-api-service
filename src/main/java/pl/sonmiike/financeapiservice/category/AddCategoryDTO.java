package pl.sonmiike.financeapiservice.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCategoryDTO {

    @NotBlank
    private String name;
    // TODO think if it should be required or optional (default icon)
    private String iconUrl;

}
