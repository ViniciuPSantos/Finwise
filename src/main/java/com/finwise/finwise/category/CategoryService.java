package com.finwise.finwise.category;

import com.finwise.finwise.auth.User;
import com.finwise.finwise.auth.UserRepository;
import com.finwise.finwise.category.dto.CategoryRequest;
import com.finwise.finwise.category.dto.CategoryResponse;
import com.finwise.finwise.shared.exception.CategoryNotFoundException;
import com.finwise.finwise.shared.exception.InvalidCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public CategoryResponse create(String email, CategoryRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        Category category = new Category();
        category.setName(request.name());
        category.setUser(user);

        return CategoryResponse.from(categoryRepository.save(category));
    }

    public List<CategoryResponse> listByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        return categoryRepository.findByUser(user).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse getById(String email, Long id) {
        return CategoryResponse.from(getOwnedCategory(email, id));
    }

    public CategoryResponse update(String email, Long id, CategoryRequest request) {
        Category category = getOwnedCategory(email, id);
        category.setName(request.name());
        return CategoryResponse.from(categoryRepository.save(category));
    }

    public void delete(String email, Long id) {
        categoryRepository.delete(getOwnedCategory(email, id));
    }

    private Category getOwnedCategory(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        return categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(CategoryNotFoundException::new);
    }
}
