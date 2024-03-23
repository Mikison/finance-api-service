package pl.sonmiike.financeapiservice.category;

import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .iconUrl(category.getIconUrl())
                .build();
    }

    public Category toEntity(CategoryDTO categoryDTO) {
        return Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .iconUrl(categoryDTO.getIconUrl())
                .build();
    }

    public Category toEntity(AddCategoryDTO categoryDTO) {
        return Category.builder()
                .name(categoryDTO.getName())
                .iconUrl(categoryDTO.getIconUrl())
                .build();
    }

    public Category toEntity(CategoryDTO categoryDTO, Category category) {
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        category.setIconUrl(categoryDTO.getIconUrl());
        return category;
    }
}
