package com.ticup.business.generate.controller;

import static javax.lang.model.element.Modifier.PUBLIC;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.ticup.business.generate.create.GenerateUtil;
import com.ticup.business.generate.create.JavaFieldModel;
import com.ticup.business.generate.create.JavaGenerate;
import com.ticup.business.generate.create.XmlParser;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author chenhao
 * @version 1.0.0
 * @since 1.0.0
 *
 * Created at 2019-11-06 10:52
 */
@RestController
public class Controller {

  @PostMapping("/generate")
  public String test(@RequestBody String xml, String packageName, String clazzName)
      throws IOException, ClassNotFoundException {

    StringBuilder resultSb = new StringBuilder();

    XmlParser xmlParser = new XmlParser();
    Map<String, String> map = new HashMap<>();

    Map<String, List<JavaFieldModel>> demo = xmlParser.parser(xml, clazzName);

    Set<Entry<String, List<JavaFieldModel>>> entries = demo.entrySet();

    JavaGenerate javaGenerate = new JavaGenerate();
    for (Entry<String, List<JavaFieldModel>> entry : entries) {
      // 最后生成base 主类模板
      if (!entry.getKey().equals(clazzName)) {
        List<FieldSpec> fieldSpecs = javaGenerate.generateField(entry.getValue());
        List<MethodSpec> methodSpecs = javaGenerate.generateGetSetMethod(fieldSpecs);
        MethodSpec methodSpec = javaGenerate.generateToString(fieldSpecs, entry.getKey());

        Builder builder = TypeSpec.classBuilder(GenerateUtil.upper(entry.getKey()));
        // 循环封装属性
        for (FieldSpec fieldSpec : fieldSpecs) {
          builder.addField(fieldSpec);
        }

        // 循环封装方法
        for (MethodSpec spec : methodSpecs) {
          builder.addMethod(spec);
        }

        // list 列表类需要打上注解 加上方法
        TypeSpec build = builder.addAnnotation(
            AnnotationSpec.builder(XmlAccessorType.class)
                .addMember("value", "$T.$L", XmlAccessType.class, XmlAccessType.FIELD).build())
            .addMethod(methodSpec).build();

        //生成一个Java文件
        JavaFile javaFile = JavaFile.builder(packageName, build)
            .build();

        System.out.println(javaFile);
        resultSb.append(javaFile).append("\n\n\n");
        map.put(entry.getKey(), javaFile.packageName);
      }
    }

    //--------------补充上列表属性------------
    // 生成主类模板
    List<JavaFieldModel> list = demo.get(clazzName);
    List<FieldSpec> fieldSpecs = javaGenerate.generateField(list);
    List<MethodSpec> methodSpecs = javaGenerate.generateGetSetMethod(fieldSpecs);
    Builder builderBase = TypeSpec.classBuilder(clazzName);
    // 循环封装属性
    for (FieldSpec fieldSpec : fieldSpecs) {
      builderBase.addField(fieldSpec);
    }
    // 循环封装方法
    for (MethodSpec spec : methodSpecs) {
      builderBase.addMethod(spec);
    }
    //---补充列表的 getter setter
    // 先构造一个list，然后构造一个泛型的类型
    ClassName className = ClassName.get("java.util", "List");
    Set<Entry<String, String>> entries1 = map.entrySet();
    MethodSpec methodSpec;
    for (Entry<String, String> stringStringEntry : entries1) {
      ClassName fanxing = ClassName.get(stringStringEntry.getValue(), stringStringEntry.getKey());
      // 泛型组装
      TypeName listOfHoverboards = ParameterizedTypeName.get(className, fanxing);

      // 补充列表属性，需要加上注解
      FieldSpec listBuild = FieldSpec.builder(listOfHoverboards, stringStringEntry.getKey())
          .build();
      builderBase.addField(
          FieldSpec.builder(listOfHoverboards, stringStringEntry.getKey())
              .addAnnotation(AnnotationSpec.builder(XmlElement.class)
                  .addMember("name", "$S", stringStringEntry.getKey() + "/Row").build()).build()
      );

      // getter
      String name = GenerateUtil.upper(stringStringEntry.getKey());
      methodSpec = MethodSpec.methodBuilder("get" + name)
          .addModifiers(PUBLIC)
          .returns(listOfHoverboards)
          .addStatement("return " + stringStringEntry.getKey())
          .build();
      builderBase.addMethod(methodSpec);

      // setter
      methodSpec = MethodSpec.methodBuilder("set" + name)
          .addModifiers(PUBLIC)
          .addParameter(listOfHoverboards, name)
          .addStatement("this." + name + "=" + name)
          .build();
      builderBase.addMethod(methodSpec);

      // 将list这个属性 加入list
      fieldSpecs.add(listBuild);

    }

    MethodSpec toString = javaGenerate.generateToString(fieldSpecs, clazzName);
    builderBase.addMethod(toString);
    builderBase.addAnnotation(AnnotationSpec.builder(XmlAccessorType.class)
        .addMember("value", "$T.$L", XmlAccessType.class, XmlAccessType.FIELD).build()).build();

    //生成一个Java文件
    JavaFile javaFile = JavaFile.builder(packageName, builderBase.build())
        .build();

    System.out.println(javaFile);
    resultSb.append(javaFile);
    return resultSb.toString();
  }


}
