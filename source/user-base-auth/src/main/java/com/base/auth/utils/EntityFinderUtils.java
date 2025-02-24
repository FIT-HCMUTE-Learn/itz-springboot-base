package com.base.auth.utils;

import java.util.function.Supplier;

import com.base.auth.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public class EntityFinderUtils {

    private EntityFinderUtils() {
    }

    public static <T, ID> T findByIdOrThrow(JpaRepository<T, ID> repository, ID id, Supplier<NotFoundException> exceptionSupplier) {
        return repository.findById(id).orElseThrow(exceptionSupplier);
    }
}
