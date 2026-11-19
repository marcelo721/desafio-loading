package com.marcelo721.desfio_loading_full__stack.application.model;

import java.util.List;

public record PageResult<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
}
