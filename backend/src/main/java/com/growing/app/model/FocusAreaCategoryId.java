package com.growing.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * FocusAreaCategory复合主键类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FocusAreaCategoryId implements Serializable {
    private Long focusAreaId;
    private Long categoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FocusAreaCategoryId that = (FocusAreaCategoryId) o;
        return Objects.equals(focusAreaId, that.focusAreaId) &&
               Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(focusAreaId, categoryId);
    }
}
