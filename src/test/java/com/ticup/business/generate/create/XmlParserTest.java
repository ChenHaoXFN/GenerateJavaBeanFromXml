package com.ticup.business.generate.create;

import static javax.lang.model.element.Modifier.PUBLIC;
import static org.junit.jupiter.api.Assertions.*;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 * @author chenhao
 * @version 1.0.0
 * @since 1.0.0
 *
 * Created at 2019-11-06 14:28
 */
class XmlParserTest {

  @Test
  public void parser() throws IOException, ClassNotFoundException {
    XmlParser xmlParser = new XmlParser();
    String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
        + "<transaction>\n"
        + "  <header>\n"
        + "    <ver>1.0</ver>\n"
        + "    <msg>\n"
        + "      <msgCd>FAS.OB0050000.01</msgCd>\n"
        + "      <seqNb>CBS201910240000001</seqNb>\n"
        + "      <sndAppCd>CBS</sndAppCd>\n"
        + "      <sndDt>20191024</sndDt>\n"
        + "      <sndTm>171100</sndTm>\n"
        + "      <rcvAppCd>FAS</rcvAppCd>\n"
        + "    </msg>\n"
        + "  </header>\n"
        + "  <body>\n"
        + "    <request>\n"
        + "      <linkBillNo>编号</linkBillNo>\n"
        + "      <bizFlowNo>biz号</bizFlowNo>\n"
        + "      <newCustomerNo>新建人账号</newCustomerNo>\n"
        + "      <newCustomerName>新建人名称</newCustomerName>\n"
        + "      <projectDisposeNo>处置项目编号</projectDisposeNo>\n"
        + "      <projectDisposeName>处置项目名称</projectDisposeName>\n"
        + "      <billingType>类型</billingType>\n"
        + "      <fundCode>fcode</fundCode>\n"
        + "      <recoverDate>日期</recoverDate>\n"
        + "      <operatorName>operator</operatorName>\n"
        + "      <operatorInstitute>operins</operatorInstitute>\n"
        + "      <operatorDepartment>operdep</operatorDepartment>\n"
        + "      <bizDate>biz日期</bizDate>\n"
        + "      <cashRecoverList>\n"
        + "        <Row>\n"
        + "          <newDebtContract>ndc</newDebtContract>\n"
        + "          <newRecoverAmount>金额1</newRecoverAmount>\n"
        + "          <newRcReturnPrincipal>金额1</newRcReturnPrincipal>\n"
        + "          <newRcReturnInnerInt>金额1</newRcReturnInnerInt>\n"
        + "          <newRcReturnOuterInt>金额1</newRcReturnOuterInt>\n"
        + "          <newRcReturnFruits>金额1</newRcReturnFruits>\n"
        + "          <newIsPassRecover>金额1</newIsPassRecover>\n"
        + "          <newOverchargeAmt>金额1</newOverchargeAmt>\n"
        + "          <returnPrincipal>金额1</returnPrincipal>\n"
        + "          <returnInnerInt>金额1</returnInnerInt>\n"
        + "          <returnOuterInt>金额1</returnOuterInt>\n"
        + "          <returnFruits>金额1</returnFruits>\n"
        + "          <recoverCurrency>币种</recoverCurrency>\n"
        + "        </Row>\n"
        + "      </cashRecoverList>\n"
        + "      <npaFASAttachInfoList>\n"
        + "        <Row>\n"
        + "          <sid>xxxxxxxxxxxxxxxx</sid>\n"
        + "          <uploadDate>2019-10-22</uploadDate>\n"
        + "          <attachName>附件名称</attachName>\n"
        + "          <uploader>上传人</uploader>\n"
        + "          <attachType>TXT</attachType>\n"
        + "          <attachSize>10</attachSize>\n"
        + "          <path>path</path>\n"
        + "        </Row>\n"
        + "      </npaFASAttachInfoList>\n"
        + "    </request>\n"
        + "  </body>\n"
        + "</transaction>\n";

    Map<String, String> map = new HashMap<>();

    Map<String, List<JavaFieldModel>> demo = xmlParser.parser(xml, "demo");

    Set<Entry<String, List<JavaFieldModel>>> entries = demo.entrySet();

    JavaGenerate javaGenerate = new JavaGenerate();
    for (Entry<String, List<JavaFieldModel>> entry : entries) {
      // 最后生成base 主类模板
      if (!entry.getKey().equals("demo")) {
        List<FieldSpec> fieldSpecs = javaGenerate.generateField(entry.getValue());
        List<MethodSpec> methodSpecs = javaGenerate.generateGetSetMethod(fieldSpecs);
        MethodSpec methodSpec = javaGenerate.generateToString(fieldSpecs, entry.getKey());

        Builder builder = TypeSpec.classBuilder(entry.getKey());
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
                .addMember("value", "$T.$L", XmlAccessType.class,XmlAccessType.FIELD).build()).addMethod(methodSpec).build();



        //生成一个Java文件
        JavaFile javaFile = JavaFile.builder("com.xupt.willscorpio.javatest", build)
            .build();

        System.out.println(javaFile);
        map.put(entry.getKey(), javaFile.packageName);
      }
    }

    //--------------补充上列表属性------------
    // 生成主类模板
    List<JavaFieldModel> list = demo.get("demo");
    List<FieldSpec> fieldSpecs = javaGenerate.generateField(list);
    List<MethodSpec> methodSpecs = javaGenerate.generateGetSetMethod(fieldSpecs);
    Builder builderBase = TypeSpec.classBuilder("demo");
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

    MethodSpec toString = javaGenerate.generateToString(fieldSpecs, "javaName");
    builderBase.addMethod(toString);
    builderBase.addAnnotation(AnnotationSpec.builder(XmlAccessorType.class)
        .addMember("value", "$T.$L", XmlAccessType.class, XmlAccessType.FIELD).build()).build();

    //生成一个Java文件
    JavaFile javaFile = JavaFile.builder("com.xupt.willscorpio.javatest", builderBase.build())
        .build();

    System.out.println(javaFile);
  }

}