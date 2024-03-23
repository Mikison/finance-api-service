package pl.sonmiike.financeapiservice.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDTO {

    @NotBlank
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String iconUrl;



}
