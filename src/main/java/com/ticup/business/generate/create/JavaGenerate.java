package com.ticup.business.generate.create;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;

/**
 * java生成
 *
 * @author chenhao
 * @version 1.0.0
 * @since 1.0.0
 *
 * Created at 2019-11-06 10:47
 */
public class JavaGenerate {

  /**
   * 根据 字段集合 构造所有的 私有字段.
   *
   * @param javaFieldModels xml解析出来的字段集合
   * @return 构造好的字段
   */
  public List<FieldSpec> generateField(List<JavaFieldModel> javaFieldModels) {
    FieldSpec fieldSpec;
    List<FieldSpec> result = new ArrayList<>();
    for (JavaFieldModel model : javaFieldModels) {
      // 构建类型
      fieldSpec = FieldSpec.builder(model.getClazz(), model.getName(), Modifier.PRIVATE)
          .addJavadoc(model.getComment() + ".\n")
          .build();
      result.add(fieldSpec);
    }
    return result;
  }


  /**
   * 根据字段生成 get set 方法.
   *
   * @param fieldSpecList 字段
   * @return 生成的方法合集
   */
  public List<MethodSpec> generateGetSetMethod(List<FieldSpec> fieldSpecList) {
    List<MethodSpec> result = new ArrayList<>();
    MethodSpec methodSpec;
    for (FieldSpec fieldSpec : fieldSpecList) {
      String name = GenerateUtil.upper(fieldSpec.name);
      methodSpec = MethodSpec.methodBuilder("get" + name)
          .addModifiers(Modifier.PUBLIC)
          .returns(fieldSpec.type)
          .addStatement("return " + fieldSpec.name)
          .build();
      result.add(methodSpec);

      methodSpec = MethodSpec.methodBuilder("set" + name)
          .addModifiers(Modifier.PUBLIC)
          .addParameter(fieldSpec.type, fieldSpec.name)
          .addStatement("this." + fieldSpec.name + "=" + fieldSpec.name)
          .build();
      result.add(methodSpec);

    }
    return result;
  }


  /**
   * 根据字段生成 toString 方法
   *
   * @param fieldSpecList 字段集合
   * @return 返回toString 方法
   */
  public MethodSpec generateToString(List<FieldSpec> fieldSpecList, String className) {
    StringBuilder sb = new StringBuilder();
    sb.append("return " + "\"" + className + "{\"" + " +\n" + "\"");
    for (int i = 0; i < fieldSpecList.size(); i++) {
      sb.append(fieldSpecList.get(i).name + "= \'\"" + " + "
          + fieldSpecList.get(i).name + " +'\\\''" + " + ");
      if (i != fieldSpecList.size() - 1) {
        sb.append("\n" + "\",");
      } else {
        sb.append("\'}\'");
      }

    }

    return MethodSpec.methodBuilder("toString")
        .addModifiers(Modifier.PUBLIC)
        .returns(String.class)
        .addAnnotation(Override.class)
        .addStatement(sb.toString()).build();
  }

}
