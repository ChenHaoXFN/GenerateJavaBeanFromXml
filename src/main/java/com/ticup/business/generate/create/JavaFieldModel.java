package com.ticup.business.generate.create;

/**
 * 每一行xml 对应的属性
 *
 * @author chenhao
 * @version 1.0.0
 * @since 1.0.0
 *
 * Created at 2019-11-06 10:46
 */
public class JavaFieldModel {
  // 属性名称
  private String name;
  // 属性类型
  private Class<?> clazz;
  // 属性注释
  private String comment;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  @Override
  public String toString() {
    return "JavaFieldModel{" +
        "name='" + name + '\'' +
        ", clazz=" + clazz +
        ", comment='" + comment + '\'' +
        '}';
  }

}
