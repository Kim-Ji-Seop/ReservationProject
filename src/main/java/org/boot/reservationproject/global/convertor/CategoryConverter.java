package org.boot.reservationproject.global.convertor;

import java.beans.PropertyEditorSupport;
import org.boot.reservationproject.global.Category;

public class CategoryConverter extends PropertyEditorSupport {

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    setValue(Category.valueOf(text.toUpperCase()));
  }
}
